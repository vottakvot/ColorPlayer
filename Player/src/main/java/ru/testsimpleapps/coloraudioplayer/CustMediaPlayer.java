package ru.testsimpleapps.coloraudioplayer;


import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.Random;

import ru.testsimpleapps.music_explorer.FindMedia;

public class CustMediaPlayer
        extends MediaPlayer
            implements  MediaPlayer.OnCompletionListener,
                        AudioManager.OnAudioFocusChangeListener {

    public static final String LOG_PLAYER = "PLAYER_LOG";

    private boolean isPrepared = false;
    private final Handler seekHandler;
    private final AudioManager audioManager;
    private boolean isAudioFocusPause = false;

    /**
     * This method is used for update all players views. Run in separate thread and send messages to handler.
     */
    private Thread updatePlayerControlViewThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                do {
                    seekHandler.sendEmptyMessage(PlayService.UPDATE_SEEK_BAR_POSITION);
                    seekHandler.sendEmptyMessage(PlayService.UPDATE_COUNT_TRACKS);
                    Thread.currentThread().sleep(1000);
                } while(!Thread.currentThread().isInterrupted());
            } catch (InterruptedException e) {
                e.printStackTrace();
                seekHandler.removeCallbacksAndMessages(null);
            }
        }
    });

    public CustMediaPlayer(Handler seekHandler, AudioManager audioManager){
        super();
        setOnCompletionListener(this);
        this.seekHandler = seekHandler;
        this.audioManager = audioManager;

        updatePlayerControlViewThread.setDaemon(true);
    }

    @Override
    public void reset() {
        super.reset();
        isPrepared = false;
        // Update selected position in playlist
        seekHandler.sendEmptyMessage(PlayService.UPDATE_PLAYLIST_POSITION);
        // Update seeker
        seekHandler.sendEmptyMessage(PlayService.UPDATE_SEEK_BAR_RESET);
        // Update button play-pause
        seekHandler.sendMessage(seekHandler.obtainMessage(PlayService.UPDATE_PLAY_PAUSE, false));
        // Update track name view
        seekHandler.sendMessage(seekHandler.obtainMessage(PlayService.UPDATE_NAME_TRACK, ""));
    }

    @Override
    public void release() {
        PlayerApplication.getPlayerApplication().setSeekCurrentPosition(getCurrentPosition());
        interruptSeekBarUpdate();
        audioManager.abandonAudioFocus(this);
        isPrepared = false;
        super.release();
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        super.prepare();
        isPrepared = true;
    }

    public boolean isPrepared() {
        return isPrepared;
    }


    @Override
    public void start() throws IllegalStateException {
        super.start();
        // Update button play-pause
        seekHandler.sendMessage(seekHandler.obtainMessage(PlayService.UPDATE_PLAY_PAUSE, true));
        // Get audio focus
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        // Reset audio focus trigger
        isAudioFocusPause = false;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        // Update button play-pause
        seekHandler.sendMessage(seekHandler.obtainMessage(PlayService.UPDATE_PLAY_PAUSE, false));
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        // Update button play-pause
        seekHandler.sendMessage(seekHandler.obtainMessage(PlayService.UPDATE_PLAY_PAUSE, false));
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        synchronized(this){
            Log.i(LOG_PLAYER, this.getClass().getName().toString() + " - onAudioFocusChange - " + focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    if(isPlaying()){
                        isAudioFocusPause = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if(isPlaying()){
                        isAudioFocusPause = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    setVolume(0.5f, 0.5f);
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (!isPlaying() && isAudioFocusPause){
                        start();
                    }
                    setVolume(1.0f, 1.0f);
                    break;
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {;
        synchronized(this){
            Log.i(LOG_PLAYER, this.getClass().getName().toString() + " - onCompletion");
            audioManager.abandonAudioFocus(this);
            next();
        }
    }


    private void interruptSeekBarUpdate(){
        if(updatePlayerControlViewThread != null && updatePlayerControlViewThread.isAlive())
            updatePlayerControlViewThread.interrupt();

        seekHandler.sendEmptyMessage(PlayService.UPDATE_SEEK_BAR_RESET);
    }

    /**
     * This main player method for play music.
     * @param saveQuene - if true, then put son id to Quene.
     * @param isLastPosition - if true then pre-load last track position
     */
    public void play(boolean saveQuene, boolean isLastPosition){
        try {
                Cursor activePlaylist = PlayerApplication.getPlayerApplication().getActivePlaylist();
                String trackPath = null;
                if(activePlaylist != null){
                    trackPath = activePlaylist.getString(activePlaylist.getColumnIndex(MediaStore.Audio.Media.DATA));
                }

                if(trackPath != null){
                    reset();
                    setDataSource(trackPath);
                    prepare();

                    // If we need set last track position, after reboot
                    if(isLastPosition){
                        if(0 < PlayerApplication.getPlayerApplication().getSeekCurrentPosition() &&
                            PlayerApplication.getPlayerApplication().getSeekCurrentPosition() < getDuration()) {
                                seekTo(PlayerApplication.getPlayerApplication().getSeekCurrentPosition());
                        }
                    } else {
                        start();
                    }

                    // Add to play track quene
                    if(saveQuene){
                        addToPlaylistQuene(activePlaylist.getPosition());
                    }

                    // Save current track id for select item view
                    PlayerApplication.getPlayerApplication().setSongId(activePlaylist.getLong(activePlaylist.getColumnIndex(MediaStore.Audio.Media._ID)));
                    // Save current track name for notification only :(
                    PlayerApplication.getPlayerApplication().setTrackName(FindMedia.getName(trackPath));

                    // Update track name view
                    seekHandler.sendMessage(seekHandler.obtainMessage(PlayService.UPDATE_NAME_TRACK, FindMedia.getName(trackPath)));
                    // Update duration track for textview and seekbar
                    seekHandler.sendEmptyMessage(PlayService.UPDATE_SEEK_BAR_DURATION);
                    // Update count track
                    seekHandler.sendEmptyMessage(PlayService.UPDATE_COUNT_TRACKS);
                    // Update selected position in playlist
                    seekHandler.sendEmptyMessage(PlayService.UPDATE_PLAYLIST_POSITION);

                    // Start control view updater
                    if(updatePlayerControlViewThread != null && !updatePlayerControlViewThread.isAlive()){
                        updatePlayerControlViewThread.start();
                    }
                }
        } catch (Exception e){
            e.printStackTrace();

            try {
                if(!isLastPosition){
                    int currentRepeat = PlayerApplication.getPlayerApplication().getRepeat();
                    boolean currentRand = PlayerApplication.getPlayerApplication().isRandom();
                    PlayerApplication.getPlayerApplication().setRepeat(PlayerApplication.REPEAT_NONE);
                    PlayerApplication.getPlayerApplication().setIsRandom(false);
                    next();
                    PlayerApplication.getPlayerApplication().setRepeat(currentRepeat);
                    PlayerApplication.getPlayerApplication().setIsRandom(currentRand);
                } else {
                        reset();
                    }
            } catch (Exception ex){
                    ex.printStackTrace();
                    reset();
                }
        }
    }

    private void addToPlaylistQuene(Integer trackPosition){
        if(PlayerApplication.getPlayerApplication().getPreviousQuene() != null){
            PlayerApplication.getPlayerApplication().getPreviousQuene().addLast(trackPosition);
            if(PlayerApplication.getPlayerApplication().getPreviousQuene().size() > PlayerApplication.getPlayerApplication().getActivePlaylist().getCount())
                PlayerApplication.getPlayerApplication().getPreviousQuene().removeFirst();
        }
    }

    public void next(){
        Cursor activePlaylist = PlayerApplication.getPlayerApplication().getActivePlaylist();
        if(activePlaylist != null && activePlaylist.getCount() > 0) {
            synchronized (PlayerApplication.getPlayerApplication().getActivePlaylist()) {
                if (PlayerApplication.getPlayerApplication().isRandom()) {
                    Log.i(LOG_PLAYER, this.getClass().getName().toString() + " - next - Set.size is - " + PlayerApplication.getPlayerApplication().getUniqueRandom().size() +
                                        " = activePlaylist.getCount - " + activePlaylist.getCount());
                    // if container is full
                    if(PlayerApplication.getPlayerApplication().getUniqueRandom().size() == activePlaylist.getCount()){
                        PlayerApplication.getPlayerApplication().getUniqueRandom().clear();
                    }

                    // Find repeat by cycle
                    do {
                            Integer position = new Random().nextInt(activePlaylist.getCount());
                            if(!PlayerApplication.getPlayerApplication().getUniqueRandom().contains(position)){
                                Log.i(LOG_PLAYER, this.getClass().getName().toString() + " - while - position - " + position);
                                activePlaylist.moveToPosition(position);
                                play(true, false);
                                PlayerApplication.getPlayerApplication().getUniqueRandom().add(position);
                                break;
                            }
                    } while(true);

                } else {
                    switch (PlayerApplication.getPlayerApplication().getRepeat()) {
                        case PlayerApplication.REPEAT_NONE:
                            if (activePlaylist.moveToNext()) {
                                play(true, false);
                            } else {
                                activePlaylist.moveToFirst();
                                reset();
                            }
                            break;

                        case PlayerApplication.REPEAT_ONE:
                            play(false, false);
                            break;

                        case PlayerApplication.REPEAT_ALL:
                            if (!activePlaylist.moveToNext()) {
                                activePlaylist.moveToFirst();
                            }

                            play(true, false);
                            break;
                    }
                }

                // Update selected position in playlist
                seekHandler.sendEmptyMessage(PlayService.UPDATE_PLAYLIST_POSITION);
            }
        }
    }

    public void previous(){
        if(PlayerApplication.getPlayerApplication().getPreviousQuene() != null &&
            PlayerApplication.getPlayerApplication().getPreviousQuene().size() > 0){
                PlayerApplication.getPlayerApplication().getPreviousQuene().removeLast();

                if(!PlayerApplication.getPlayerApplication().getPreviousQuene().isEmpty()){
                    int previousTrack = PlayerApplication.getPlayerApplication().getPreviousQuene().getLast();

                    if(0 <= previousTrack && previousTrack < PlayerApplication.getPlayerApplication().getActivePlaylist().getCount()){
                        PlayerApplication.getPlayerApplication().getActivePlaylist().moveToPosition(previousTrack);
                        play(false, false);
                    }

                    // Update selected position in playlist
                    seekHandler.sendEmptyMessage(PlayService.UPDATE_PLAYLIST_POSITION);
                }
        }
    }
}
