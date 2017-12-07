package ru.testsimpleapps.coloraudioplayer.managers.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.util.Log;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.RandomSet;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.StrictQueue;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;

public class AudioPlayer implements IAudioPlayer, MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    public static final int MIN_SEEK_POSITION = 0;

    /*
    * Application mContext
    * */
    private final Context mContext;

    /*
    * Android media player
    * */
    private final MediaPlayer mMediaPlayer;

    /*
    * For audio focus
    * */
    private final AudioManager mAudioManager;

    /*
    * States
    * */
    private enum State {
        INIT, PLAY, PAUSE, STOP, RELEASE
    }

    /*
    * Current mState
    * */
    private State mState = State.INIT;

    /*
    * Playlist/Config/Queue
    * */
    private StrictQueue<Integer> mListenedTracks;
    private RandomSet mTracksId;
    private PlayerConfig mPlayerConfig;
    private IPlaylist mPlaylist;
    private String mPath;
    private boolean mIsPlaylist;

    /*
    * Triggers
    * */
    private boolean isAudioFocusLoss = false;

    public AudioPlayer(@NonNull Context context, @NonNull PlayerConfig playerConfig) {
        mContext = context;
        mPlayerConfig = playerConfig;
        mListenedTracks = new StrictQueue<>();
        mTracksId = new RandomSet();
        mMediaPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mMediaPlayer.setOnCompletionListener(this);
    }

    public AudioPlayer(@NonNull Context context, @NonNull PlayerConfig playerConfig, @NonNull IPlaylist playlist) {
        this(context, playerConfig);
        mPlaylist = playlist;
        mIsPlaylist = true;
    }

    public AudioPlayer(@NonNull Context context, @NonNull PlayerConfig playerConfig, @NonNull String path) {
        this(context, playerConfig);
        mPath = path;
        mIsPlaylist = false;
    }

    /*
    * AudioManager.OnAudioFocusChangeListener
    * */
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mState == State.PLAY) {
                    isAudioFocusLoss = true;
                    pause();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mState == State.PLAY) {
                    isAudioFocusLoss = true;
                    pause();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mMediaPlayer.setVolume(0.5f, 0.5f);
                break;

            case AudioManager.AUDIOFOCUS_GAIN:
                if (mState != State.PLAY && isAudioFocusLoss) {
                    play();
                }
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;
        }
    }

    /*
    * MediaPlayer.OnCompletionListener
    * */
    @Override
    public void onCompletion(MediaPlayer mp) {
        mAudioManager.abandonAudioFocus(this);
        next();
    }

    /*
    * IAudioPlayer
    * */
    @Override
    public boolean play() {
        if (mState != State.RELEASE) {
            try {
                // Prepare player
                if (mState != State.PAUSE) {
                    mMediaPlayer.setDataSource(getPath());
                    mMediaPlayer.prepare();
                    mMediaPlayer.seekTo(mPlayerConfig.getLastSeekPosition());
                }

                // Play source
                mMediaPlayer.start();
                // Get audio focus
                mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                // Reset audio focus trigger
                isAudioFocusLoss = false;
                // Set player mState
                mState = State.PLAY;
                return true;
            } catch (Exception e) { // Path not found or bad file or bad path. Add log.
                Log.e(App.TAG, getClass().getSimpleName() + " - play() - " + e.getMessage());
                mMediaPlayer.reset();
            }
        }

        return false;
    }

    @Override
    public boolean pause() {
        if (mState == State.PLAY) {
            mMediaPlayer.pause();
            mAudioManager.abandonAudioFocus(this);
            mState = State.PAUSE;
            return true;
        }

        return false;
    }

    @Override
    public boolean stop() {
        if (mState == State.PLAY || mState == State.PAUSE) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mAudioManager.abandonAudioFocus(this);
            mState = State.STOP;
            return true;
        }

        return false;
    }

    @Override
    public boolean next() {
        if (mState != State.RELEASE)
            if (nextInPlaylist())
                return playNew();
        return false;
    }

    @Override
    public boolean previous() {
        if (mState != State.RELEASE)
            if (previousInPlaylist())
                return playNew();
        return false;
    }

    @Override
    public boolean seek(final int position) {
        final int duration = mMediaPlayer.getDuration();
        if ((mState == State.PLAY || mState == State.PAUSE) && (position > 0 && position < duration)) {
            mMediaPlayer.seekTo(position);
            return true;
        }

        return false;
    }

    @Override
    public void release() {
        if (mState != State.RELEASE) {
            mMediaPlayer.release();
            mState = State.RELEASE;
        }
    }

    @Override
    public void setConfig(PlayerConfig playerConfig) {
        mPlayerConfig = playerConfig;
    }

    @Override
    public PlayerConfig getConfig() {
        mPlayerConfig.setLastSeekPosition(MIN_SEEK_POSITION);
        if (mState == State.PLAY || mState == State.PAUSE) {
            mPlayerConfig.setLastSeekPosition(getPosition());
        }

        return mPlayerConfig;
    }

    @Override
    public void setPlaylist(@NonNull final IPlaylist playlist) {
        mPlaylist = playlist;
        mIsPlaylist = true;
    }

    @Override
    public void setTrackPath(@NonNull final String path) {
        mPath = path;
        mIsPlaylist = false;
    }

    private String getPath() {
        return mIsPlaylist? mPlaylist.getTrackPath() : mPath;
    }

    private boolean nextInPlaylist() {
        boolean isHasNext = false;
        if (mPlaylist != null && mPlaylist.size() > 0) {

            // Separate, if random. Infinity cycle.
            if (mPlayerConfig.isRandom()) {
                final Integer nextRandomTrack = mTracksId.getNextRandom();
                if (nextRandomTrack != null)
                    return mPlaylist.goToPosition(nextRandomTrack);

                return false;
            }

            // If not random
            switch (mPlayerConfig.getRepeat()) {
                case NONE:
                    if (!mPlaylist.toNext()) {
                        mPlaylist.toFirst();
                        isHasNext = false;
                    } else {
                        isHasNext = true;
                    }

                    break;
                case ONE:
                    isHasNext = true;
                    break;
                case ALL:
                    if (!mPlaylist.toNext())
                        mPlaylist.toFirst();
                    isHasNext = true;
            }
        }

        return isHasNext;
    }

    private boolean previousInPlaylist() {
        boolean isHasPrevious = false;
        final Integer previousPosition = mListenedTracks.pop();
        if (previousPosition != null && mPlaylist != null) {
            isHasPrevious = true;
            mPlaylist.goToPosition(previousPosition);
        }

        return isHasPrevious;
    }

    public boolean setConfigAndPlay(PlayerConfig playerConfig) {
        setConfig(playerConfig);
        return playNew();
    }

    public boolean playPause() {
        return mState == State.PLAY ? pause() : play();
    }

    public boolean playNew() {
        stop();
        return play();
    }

    public boolean isPlaying() {
        return mState == State.PLAY;
    }

    public int getAudioSessionId() {
        return mMediaPlayer.getAudioSessionId();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getPosition() {
        return mMediaPlayer.getCurrentPosition();
    }
}
