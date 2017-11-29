package ru.testsimpleapps.coloraudioplayer.managers.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.RandomSet;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.StrictQueue;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;

public class AudioPlayer
        implements IAudioPlayer,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {


    /*
    * Max seek position interval.
    * */
    public static final int MAX_SEEK_POSITION = 1000;

    /*
    * Min seek position interval
    * */
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
    * Playlist
    * */
    private StrictQueue<Integer> mListenedTracks;
    private RandomSet mTracksId;
    private PlayerConfig mPlayerConfig;

    /*
    * Triggers
    * */
    private boolean isAudioFocusLoss = false;

    public AudioPlayer(@NonNull Context context, @Nullable AudioManager audioManager, @Nullable PlayerConfig playerConfig) {
        mContext = context;
        mPlayerConfig = playerConfig;
        mListenedTracks = new StrictQueue<>();
        mTracksId = new RandomSet();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);

        if (audioManager == null)
            this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        else
            this.mAudioManager = audioManager;
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
                    mMediaPlayer.setDataSource(mPlayerConfig.getTrackPath());
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
                Log.d(App.TAG_APP, getClass().getSimpleName() + " - play() - " + e.getMessage());
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
    public boolean seek(@IntRange(from = MIN_SEEK_POSITION, to = MAX_SEEK_POSITION) int position) {
        if ((mState == State.PLAY || mState == State.PAUSE) && (position >= 0 && position <= MAX_SEEK_POSITION)) {
            final int duration = mMediaPlayer.getDuration();
            final int step = duration / MAX_SEEK_POSITION;
            mMediaPlayer.seekTo(step * position);
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
        this.mPlayerConfig = playerConfig;
    }

    @Override
    public PlayerConfig getConfig() {
        int position = MIN_SEEK_POSITION;
        if (mState == State.PLAY || mState == State.PAUSE) {
            final float part = (float) mMediaPlayer.getCurrentPosition() / (float) mMediaPlayer.getDuration();
            position = (int) (part * MAX_SEEK_POSITION);
        }

        mPlayerConfig.setLastSeekPosition(position);
        return mPlayerConfig;
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

    private boolean nextInPlaylist() {
        boolean isHasNext = false;
        final IPlaylist playlist = mPlayerConfig.getPlaylist();
        if (playlist != null && playlist.size() > 0) {

            // Separate, if random. Infinity cycle.
            if (mPlayerConfig.isRandom()) {
                final Integer nextRandomTrack = mTracksId.getNextRandom();
                if (nextRandomTrack != null && playlist.goTo(nextRandomTrack))
                    mPlayerConfig.setTrackPathFromPlaylist();
                return true;
            }

            // If not random
            switch (mPlayerConfig.getRepeat()) {
                case NONE:
                    if (!playlist.toNext()) {
                        playlist.toFirst();
                        isHasNext = false;
                    } else {
                        isHasNext = true;
                    }

                    break;
                case ONE:
                    isHasNext = true;
                    break;
                case ALL:
                    if (!playlist.toNext())
                        playlist.toFirst();
                    isHasNext = true;
            }
        }

        mPlayerConfig.setTrackPathFromPlaylist();
        return isHasNext;
    }

    private boolean previousInPlaylist() {
        boolean isHasPrevious = false;
        final IPlaylist playlist = mPlayerConfig.getPlaylist();
        final Integer previousPosition = mListenedTracks.pop();
        if (previousPosition != null && playlist.goTo(previousPosition)) {
            isHasPrevious = true;
            mPlayerConfig.setTrackPathFromPlaylist();
        }

        return isHasPrevious;
    }

    public boolean isPlaying() {
        return mState == State.PLAY;
    }

    public int getAudioSessionId() {
        return mMediaPlayer.getAudioSessionId();
    }
}
