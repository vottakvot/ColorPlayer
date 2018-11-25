package ru.testsimpleapps.coloraudioplayer.managers.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log

import ru.testsimpleapps.coloraudioplayer.app.App
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig
import ru.testsimpleapps.coloraudioplayer.managers.player.data.RandomSet
import ru.testsimpleapps.coloraudioplayer.managers.player.data.StrictQueue
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory
import ru.testsimpleapps.coloraudioplayer.managers.tools.FileTool

class AudioPlayer(private val mContext: Context) : IAudioPlayer, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    /*
    * Android media player
    * */
    private val mMediaPlayer: MediaPlayer

    /*
    * For image_audio focus
    * */
    private val mAudioManager: AudioManager

    /*
    * Current mState
    * */
    private var mState = State.INIT

    /*
    * Playlist/Config/Queue
    * */
    private var mOnEvents: OnEvents? = null
    private val mListenedTracks: StrictQueue<Long>
    private val mTracksSet: RandomSet
    private var mPath: String? = null
    private var mTrackName: String? = null

    /*
    * Triggers
    * */
    private var isAudioFocusLoss = false

    val isPlaying: Boolean
        get() = mState == State.PLAY

    val audioSessionId: Int
        get() = mMediaPlayer.audioSessionId

    val duration: Int
        get() = mMediaPlayer.duration

    val position: Int
        get() = mMediaPlayer.currentPosition

    interface OnEvents {
        fun onPlay(track: String?)
    }

    /*
    * States
    * */
    private enum class State {
        INIT, PLAY, PAUSE, STOP, RELEASE
    }

    init {
        mListenedTracks = StrictQueue()
        mTracksSet = RandomSet()
        mMediaPlayer = MediaPlayer()
        mAudioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mMediaPlayer.setOnCompletionListener(this)
    }

    /*
    * AudioManager.OnAudioFocusChangeListener
    * */
    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> if (mState == State.PLAY) {
                isAudioFocusLoss = true
                pause()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (mState == State.PLAY) {
                isAudioFocusLoss = true
                pause()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mMediaPlayer.setVolume(0.5f, 0.5f)

            AudioManager.AUDIOFOCUS_GAIN -> {
                if (mState != State.PLAY && isAudioFocusLoss) {
                    play()
                }
                mMediaPlayer.setVolume(1.0f, 1.0f)
            }
        }
    }

    /*
    * MediaPlayer.OnCompletionListener
    * */
    override fun onCompletion(mp: MediaPlayer) {
        mAudioManager.abandonAudioFocus(this)
        next()
    }

    /*
    * IAudioPlayer
    * */
    override fun play(): Boolean {
        if (mState != State.RELEASE) {
            // Prepare player track and position
            if (mState != State.PAUSE && !preparePlayer()) {
                return false
            }

            // Play source
            mMediaPlayer.start()
            // Get image_audio focus
            mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
            // Reset image_audio focus trigger
            isAudioFocusLoss = false
            // Set player mState
            mState = State.PLAY

            // Update listeners
            if (mOnEvents != null) {
                mOnEvents!!.onPlay(mTrackName)
            }

            return true
        }

        return false
    }

    override fun pause(): Boolean {
        if (mState == State.PLAY) {
            mMediaPlayer.pause()
            mAudioManager.abandonAudioFocus(this)
            mState = State.PAUSE
            return true
        }

        return false
    }

    override fun stop(): Boolean {
        if (mState == State.PLAY || mState == State.PAUSE) {
            mMediaPlayer.stop()
            mMediaPlayer.reset()
            mAudioManager.abandonAudioFocus(this)
            mState = State.STOP
            return true
        }

        return false
    }

    override fun next(): Boolean {
        if (mState != State.RELEASE)
            if (nextInPlaylist())
                return playNew()
        return false
    }

    override fun previous(): Boolean {
        if (mState != State.RELEASE)
            if (previousInPlaylist())
                return playNew()
        return false
    }

    override fun seek(position: Int): Boolean {
        if (mState == State.PLAY || mState == State.PAUSE) {
            val duration = mMediaPlayer.duration
            if (position >= 0 && position < duration) {
                mMediaPlayer.seekTo(position)
                return true
            }
        }

        return false
    }

    override fun release() {
        if (mState != State.RELEASE) {
            mMediaPlayer.release()
            mState = State.RELEASE
        }
    }


    override fun setTrackPath(path: String) {
        mPath = path
    }

    private fun nextInPlaylist(): Boolean {
        var isHasNext = false
        if (CursorFactory.instance.size() > 0) {

            // Separate, if random. Infinity cycle.
//            if (instance.isRandom) {
            if (true) {
                val playlistSize = CursorFactory.instance.size().toInt()
                if (mTracksSet.size != playlistSize) {
                    mTracksSet.size = playlistSize
                }

                val nextRandomTrack = mTracksSet.nextRandom
                if (nextRandomTrack != null) {
                    isHasNext = CursorFactory.instance.goToPosition(nextRandomTrack.toLong())
                }
            } else {
                // If not random
                when (PlayerConfig.Repeat.NONE) {
                    PlayerConfig.Repeat.NONE -> if (!CursorFactory.instance.toNext()) {
                        CursorFactory.instance.toFirst()
                        isHasNext = false
                    } else {
                        isHasNext = true
                    }
                    PlayerConfig.Repeat.ONE -> isHasNext = true
                    PlayerConfig.Repeat.ALL -> {
                        if (!CursorFactory.instance.toNext()) {
                            CursorFactory.instance.toFirst()
                        }
                        isHasNext = true
                    }
                }
            }

            mListenedTracks.push(CursorFactory.instance.trackId)
        }

        return isHasNext
    }

    private fun previousInPlaylist(): Boolean {
        var isHasPrevious = false
        val previousId = mListenedTracks.pop()
        if (previousId != null) {
            isHasPrevious = true
            CursorFactory.instance.goToId(previousId)
        }

        return isHasPrevious
    }

    private fun preparePlayer(): Boolean {
        // Values for player prepare
        val trackPath: String
        var position = MIN_SEEK_POSITION

        if (mPath != null) {
            trackPath = mPath!!
            mPath = null
        } else {
            trackPath = CursorFactory.instance.trackPath
            position = PlayerConfig.instance.lastSeekPosition
            PlayerConfig.instance.trackId = CursorFactory.instance.trackId
        }

        // Save current track for callback
        mTrackName = FileTool.getFileName(trackPath)
        // Try prepare player
        return preparePlayer(trackPath, position)
    }

    private fun preparePlayer(path: String, position: Int): Boolean {
        try {
            mMediaPlayer.setDataSource(path)
            mMediaPlayer.prepare()
            val duration = mMediaPlayer.duration
            if (position > 0 && position < duration) {
                mMediaPlayer.seekTo(position)
            }

            return true
        } catch (e: Exception) { // Path not found or bad file or bad path. Add log.
            Log.e(App.TAG, javaClass.simpleName + " - preparePlayer() - " + e.message)
            mMediaPlayer.reset()
        }

        return false
    }

    fun setOnEvents(onEvents: OnEvents) {
        mOnEvents = onEvents
    }

    fun playPause(): Boolean {
        return if (mState == State.PLAY) pause() else play()
    }

    fun playNew(): Boolean {
        stop()
        return play()
    }

    companion object {

        val MIN_SEEK_POSITION = 0
    }
}
