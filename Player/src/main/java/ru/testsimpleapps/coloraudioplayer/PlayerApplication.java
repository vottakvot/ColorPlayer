package ru.testsimpleapps.coloraudioplayer;

import android.app.ActivityManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import static org.acra.ReportField.ANDROID_VERSION;
import static org.acra.ReportField.APP_VERSION_CODE;
import static org.acra.ReportField.LOGCAT;
import static org.acra.ReportField.PHONE_MODEL;
import static org.acra.ReportField.STACK_TRACE;

@ReportsCrashes(customReportContent = { APP_VERSION_CODE, ANDROID_VERSION, PHONE_MODEL, STACK_TRACE },
                mailTo = "testsimpleapps@gmail.com",
                mode = ReportingInteractionMode.DIALOG,
                resToastText = R.string.acra_crush_title,
                resDialogText = R.string.acra_crush_message_text,
                resDialogIcon = android.R.drawable.ic_dialog_info,
                resDialogTitle = R.string.acra_crush_title,
                resDialogCommentPrompt = R.string.acra_crush_message_comment,
                resDialogOkToast = R.string.acra_crush_ok)

public class PlayerApplication extends Application {

    public static final String LOG_APP = "PLAYER_LOG";
    public static final String PLAYER_PREFERENCES = "PLAYER_PREFERENCES";
    public static PlayerApplication playerApplication = null;
    public static final String EQUALIZER = "EQUALIZER";
    public static final String BASS_BOOST = "BASS_BOOST";

    public static final String NOTE_EXPAND = "EXPAND";
    public static final String NOTE_RANDOM = "RANDOM";
    public static final String NOTE_REPEAT = "REPEAT";
    public static final String NOTE_PLAYLIST_ID = "PLAYLIST_ID";
    public static final String NOTE_SONG_ID = "NOTE_SONG_ID";
    public static final String NOTE_SEEK_POSITION = "SEEK_POSITION";
    public static final String NOTE_SAVE_POSITION = "SAVE_POSITION";
    public static final String NOTE_HEADSET_ON = "HEADSET_ON";
    public static final String NOTE_POWER_BUTTON = "POWER_BUTTON";
    public static final String NOTE_RECEIVE_ALL_TIME = "RECEIVE_ALL_TIME";
    public static final String NOTE_THEME = "THEME";
    public static final String NOTE_VISUALIZER = "VISUALIZER";

    public static final int REPEAT_NONE = 0;
    public static final int REPEAT_ONE = 1;
    public static final int REPEAT_ALL = 2;
    public static final int NONE_DATA = -1;

    public static final int VISUALIZER_NONE = 0;
    public static final int VISUALIZER_LINE = 1;
    public static final int VISUALIZER_CIRCLE = 2;
    public static final int VISUALIZER_RECT = 3;

    public static final int THEME_BLUE = 0;
    public static final int THEME_GREEN = 1;
    public static final int THEME_RED = 2;
    public static int SELECTION_ITEM_COLOR = Color.WHITE;

    private SharedPreferences sharedPreferences;

    private ActionReceiver actionReceiver = null;
    private ComponentName componentName = null;
    private AudioManager audioManager = null;
    private MediaSession mediaSession = null;

    private Cursor activePlaylist = null;
    private Cursor activePlaylistView = null;
    private LinkedList<Integer> previousQuene = null;
    private HashSet<Integer> uniqueRandom = null;

    // Control panel
    private boolean isPlay = false;
    private boolean isRandom = false;
    private boolean isExpand = false;
    private int repeat = REPEAT_NONE;

    private long playlistId = -1;
    private boolean isSavePosition = false;
    private long songId = -1;
    private String trackName = null;
    private int seekCurrentPosition = 0;

    private boolean isPlayHeadsetOn = false;
    private boolean isPowerButton = false;
    private boolean isReceiveAllTime = true;

    private short equalizerPresent = -1;
    private short [] equalizerBands = null;
    private short bassBoostStrength = 0;

    private int numberTheme = 0;
    private int numberVisualizer = 0;

    private Bundle viewDataTimer = null;

    public static PlayerApplication getPlayerApplication() {
        return playerApplication;
    }

    public static String getDateTime(long ms){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(new Date(ms));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_APP, this.getClass().getName().toString() + " - onCreate");
        ACRA.init(this);
        playerApplication = this;
        loadPreferences();
        setActivePlaylist(PlaylistUtil.SORT_NONE, isSavePosition);
        initDefaultViewDataTimer();

        if(isReceiveAllTime()){
            registerActions();
        }
    }

    @Override
    public void onTerminate() {
        Log.i(LOG_APP, this.getClass().getName().toString() + " - onTerminate");
        super.onTerminate();
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    public void setCustomTheme(Context context){
        switch (PlayerApplication.getPlayerApplication().getNumberTheme()){
            case PlayerApplication.THEME_BLUE:
                context.setTheme(R.style.BlueTheme);
                SELECTION_ITEM_COLOR = MainActivity.getColor(this, R.color.blueHints);
                break;
            case PlayerApplication.THEME_GREEN:
                context.setTheme(R.style.GreenTheme);
                SELECTION_ITEM_COLOR = MainActivity.getColor(this, R.color.greenHints);
                break;
            case PlayerApplication.THEME_RED:
                context.setTheme(R.style.RedTheme);
                SELECTION_ITEM_COLOR = MainActivity.getColor(this, R.color.redHints);
                break;
        }
    }

    public int getColorTheme(int alpha, int red, int green, int blue){
        switch (PlayerApplication.getPlayerApplication().getNumberTheme()){
            case PlayerApplication.THEME_BLUE: return Color.argb(alpha, red, green, 255);
            case PlayerApplication.THEME_GREEN: return Color.argb(alpha, red, 255, blue);
            case PlayerApplication.THEME_RED: return Color.argb(alpha, 255, green, blue);
            default: return Color.argb(alpha, red, 255, blue);
        }
    }


    public Cursor setActivePlaylist(final String sortBy, final boolean isPosition) {
        Log.i(LOG_APP, this.getClass().getName().toString() + " - setActivePlaylist - id is - " + getPlaylistId());

        try{
            if(getPlaylistId() != -1){
                closeActiveCursors();
                activePlaylist = PlaylistUtil.getTracksFromPlaylist(getContentResolver(), getPlaylistId(), sortBy);
                activePlaylistView = PlaylistUtil.getTracksFromPlaylist(getContentResolver(), getPlaylistId(), sortBy);
                if(activePlaylist != null && activePlaylist.getCount() > 0 &&
                        activePlaylistView != null && activePlaylistView.getCount() > 0){
                    previousQuene = new LinkedList<>();
                    uniqueRandom = new HashSet<>();
                    initPlaylist(isPosition);
                    return activePlaylistView;
                }
            }
        } catch (RuntimeException e){
                closeActiveCursors();
                //new BugReportDialog(PlayerApplication.this, e.getMessage()).show();
            }

        return null;
    }

    public Bundle initDefaultViewDataTimer(){
        viewDataTimer = new Bundle(3);
        viewDataTimer.putString(PlayService.SUB_ACTION_TIMER_TYPE, PlayService.TIMER_TYPE_NONE);
        viewDataTimer.putInt(PlayService.SUB_ACTION_TIME, 0);
        viewDataTimer.putInt(PlayService.SUB_ACTION_TIMER_VISIBILITY, View.INVISIBLE);

        return viewDataTimer;
    }

    public Cursor initPlaylist(boolean isSetPosition){
        if(activePlaylist != null){
            synchronized(activePlaylist){
                if(activePlaylist != null && activePlaylist.getCount() > 0){
                    if(!isSetPosition){
                        activePlaylist.moveToFirst();
                        return activePlaylist;
                    } else {
                        if(PlaylistUtil.findTrackByID(activePlaylist, getSongId()) != null){
                            return activePlaylist;
                        } else {
                            activePlaylist.moveToFirst();
                            return activePlaylist;
                        }
                    }
                }
            }
        }

        return null;
    }

    public void closeActiveCursors(){
        Log.i(LOG_APP, this.getClass().getName().toString() + " - closeActiveCursors");

        if(activePlaylist != null){
            activePlaylist.close();
            activePlaylist = null;
        }

        if(activePlaylistView != null){
            activePlaylistView.close();
            activePlaylistView = null;
        }
    }

    private void loadPreferences(){
        Log.i(LOG_APP, this.getClass().getName().toString() + " - loadPreferences");

        sharedPreferences = getSharedPreferences(PLAYER_PREFERENCES, MODE_PRIVATE);
        setIsExpand(sharedPreferences.getBoolean(NOTE_EXPAND, false));
        setIsRandom(sharedPreferences.getBoolean(NOTE_RANDOM, false));
        setSeekCurrentPosition(sharedPreferences.getInt(NOTE_SEEK_POSITION, 0));
        setIsSavePosition(sharedPreferences.getBoolean(NOTE_SAVE_POSITION, false));
        setIsPlayHeadsetOn(sharedPreferences.getBoolean(NOTE_HEADSET_ON, false));
        setIsPowerButton(sharedPreferences.getBoolean(NOTE_POWER_BUTTON, false));
        setIsActiveOnBoot(sharedPreferences.getBoolean(NOTE_RECEIVE_ALL_TIME, false));
        setIsPlay(false);

        setPlaylistId(sharedPreferences.getLong(NOTE_PLAYLIST_ID, PlayerApplication.NONE_DATA));
        if (getPlaylistId() < -1) {
            setPlaylistId(-1L);
        }

        setSongId(sharedPreferences.getLong(NOTE_SONG_ID, PlayerApplication.NONE_DATA));
        if (getSongId() < -1){
            setSongId(-1L);
        }

        setRepeat(sharedPreferences.getInt(NOTE_REPEAT, PlayerApplication.REPEAT_NONE));
        if (getRepeat() < PlayerApplication.REPEAT_NONE || getRepeat() > PlayerApplication.REPEAT_ALL) {
            setRepeat(PlayerApplication.REPEAT_NONE);
        }

        setNumberTheme(sharedPreferences.getInt(PlayerApplication.NOTE_THEME, PlayerApplication.THEME_BLUE));
        if (getNumberTheme() < PlayerApplication.THEME_BLUE || getNumberTheme() > PlayerApplication.THEME_RED) {
            setNumberTheme(PlayerApplication.THEME_BLUE);
        }

        setNumberVisualizer(sharedPreferences.getInt(PlayerApplication.NOTE_VISUALIZER, PlayerApplication.VISUALIZER_LINE));
        if(getNumberVisualizer() < PlayerApplication.VISUALIZER_NONE || getNumberVisualizer() > PlayerApplication.VISUALIZER_RECT) {
            setNumberVisualizer(PlayerApplication.VISUALIZER_LINE);
        }

        loadAudioEffectsSettings();
    }

    public void savePreferences(){
        Log.i(LOG_APP, this.getClass().getName().toString() + " - savePreferences");

        sharedPreferences.edit().putBoolean(NOTE_EXPAND, isExpand()).commit();
        sharedPreferences.edit().putBoolean(NOTE_RANDOM, isRandom()).commit();
        sharedPreferences.edit().putLong(NOTE_PLAYLIST_ID, getPlaylistId()).commit();
        sharedPreferences.edit().putInt(NOTE_REPEAT, getRepeat()).commit();
        sharedPreferences.edit().putLong(NOTE_SONG_ID, getSongId()).commit();

        sharedPreferences.edit().putInt(NOTE_SEEK_POSITION, getSeekCurrentPosition()).commit();
        sharedPreferences.edit().putBoolean(NOTE_SAVE_POSITION, isSavePosition()).commit();
        sharedPreferences.edit().putBoolean(NOTE_HEADSET_ON, isPlayHeadsetOn()).commit();
        sharedPreferences.edit().putBoolean(NOTE_POWER_BUTTON, isPowerButton()).commit();
        sharedPreferences.edit().putBoolean(NOTE_RECEIVE_ALL_TIME, isReceiveAllTime()).commit();

        sharedPreferences.edit().putInt(NOTE_THEME, getNumberTheme()).commit();
        sharedPreferences.edit().putInt(NOTE_VISUALIZER, getNumberVisualizer()).commit();

        saveAudioEffectsSettings();
    }

    private void saveAudioEffectsSettings(){
        if(PlayService.getEqualizer() != null){
            sharedPreferences.edit().putInt(EQUALIZER + "_PRESENT", getEqualizerPresent()).commit();;

            if(getEqualizerBands() != null){
                sharedPreferences.edit().putInt(EQUALIZER + "_SIZE", getEqualizerBands().length).commit();;

                for(int i = 0; i < getEqualizerBands().length; i++){
                    sharedPreferences.edit().putInt(EQUALIZER + "_" + Integer.toString(i), (int) (getEqualizerBands()[i])).commit();;
                }
            }
        }

        if(PlayService.getBassBoost() != null) {
            try {
                sharedPreferences.edit().putInt(BASS_BOOST, (int) PlayService.getBassBoost().getRoundedStrength()).commit();
            } finally {

            }
        }
    }

    private void loadAudioEffectsSettings(){
        try {
            equalizerPresent = (short)sharedPreferences.getInt(EQUALIZER + "_PRESENT", 0);
            if(equalizerPresent < -1 || equalizerPresent > 20){
                equalizerPresent = 0;
            }

            int bandSize = sharedPreferences.getInt(EQUALIZER + "_SIZE", 0);
            if(bandSize > 0 && bandSize < 20){
                equalizerBands = new short[bandSize];
                for(int i = 0; i < bandSize; i++){
                    equalizerBands[i] = (short)sharedPreferences.getInt(EQUALIZER + "_" + Integer.toString(i), 0);
                }
            }

            bassBoostStrength = (short)sharedPreferences.getInt(BASS_BOOST, 0);
            if(bassBoostStrength < 0 || bassBoostStrength > 1000){
                bassBoostStrength = 0;
            }

        } catch(RuntimeException e){
            e.printStackTrace();
        }
    }

    public AudioManager registerActions(){
        if(actionReceiver != null){
            return null;
        }

        Log.i(LOG_APP, this.getClass().getName().toString() + " - registerActions");
        actionReceiver = new ActionReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);

        registerReceiver(actionReceiver, intentFilter);

        // Audio Manager
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // For button action receive
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP){
            componentName = new ComponentName(getPackageName(), ActionReceiver.class.getName());
            audioManager.registerMediaButtonEventReceiver(componentName);
        } else {
            mediaSession =  new MediaSession(this, getPackageName());
            Intent intent = new Intent(this, ActionReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mediaSession.setMediaButtonReceiver(pendingIntent);
            mediaSession.setActive(true);

            PlaybackState state = new PlaybackState.Builder()
                    .setActions(PlaybackStateCompat.ACTION_FAST_FORWARD |
                            PlaybackStateCompat.ACTION_PAUSE |
                            PlaybackStateCompat.ACTION_PLAY |
                            PlaybackStateCompat.ACTION_PLAY_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                            PlaybackStateCompat.ACTION_STOP)
                    .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1, SystemClock.elapsedRealtime())
                    .build();
            mediaSession.setPlaybackState(state);
        }

        return audioManager;
    }

    public void unregisterActions(){
        if(actionReceiver != null){
            Log.i(LOG_APP, this.getClass().getName().toString() + " - unregisterActions");

            unregisterReceiver(actionReceiver);
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP){
                audioManager.unregisterMediaButtonEventReceiver(componentName);
            } else {
                mediaSession.setActive(true);
                mediaSession.release();
            }

            actionReceiver = null;
        }
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public boolean setIsPlay(boolean isPlay) {
        return this.isPlay = isPlay;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public boolean setIsRandom(boolean isRandom) {
        return this.isRandom = isRandom;
    }

    public int getRepeat() {
        return repeat;
    }

    public int setRepeatAuto() {
        return repeat = (++repeat <= REPEAT_ALL)? repeat : REPEAT_NONE;
    }

    public void setRepeat(int repeat) {
        switch (repeat){
            case REPEAT_NONE:
            case REPEAT_ONE:
            case REPEAT_ALL: this.repeat = repeat; break;
            default: this.repeat = repeat; break;
        }
    }

    public long getSongId() {
        return songId;
    }

    public long setSongId(long songId) {
        return this.songId = songId;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public long setPlaylistId(long playlistId) {
        return this.playlistId = playlistId;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setIsExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public Cursor getActivePlaylist() {
        return activePlaylist;
    }

    public LinkedList<Integer> getPreviousQuene() {
        return previousQuene;
    }

    public short getEqualizerPresent() {
        return equalizerPresent;
    }

    public short[] getEqualizerBands() {
        return equalizerBands;
    }

    public short getBassBoostStrength() {
        return bassBoostStrength;
    }

    public void setEqualizerBands(short[] equalizerBands) {
        this.equalizerBands = equalizerBands;
    }

    public void setBassBoostStrength(short bassBoostStrength) {
        this.bassBoostStrength = bassBoostStrength;
    }

    public void setEqualizerPresent(short equalizerPresent) {
        this.equalizerPresent = equalizerPresent;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public Bundle getViewDataTimer() {
        return viewDataTimer;
    }

    public int getSeekCurrentPosition() {
        return seekCurrentPosition;
    }

    public void setSeekCurrentPosition(int seekCurrentPosition) {
        this.seekCurrentPosition = seekCurrentPosition;
    }

    public boolean isSavePosition() {
        return isSavePosition;
    }

    public void setIsSavePosition(boolean isSavePosition) {
        this.isSavePosition = isSavePosition;
    }

    public boolean isPlayHeadsetOn() {
        return isPlayHeadsetOn;
    }

    public void setIsPlayHeadsetOn(boolean isPlayHeadsetOn) {
        this.isPlayHeadsetOn = isPlayHeadsetOn;
    }

    public boolean isPowerButton() {
        return isPowerButton;
    }

    public void setIsPowerButton(boolean isPowerButton) {
        this.isPowerButton = isPowerButton;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public MediaSession getMediaSession() {
        return mediaSession;
    }

    public boolean isReceiveAllTime() {
        return isReceiveAllTime;
    }

    public void setIsActiveOnBoot(boolean isActiveOnBoot) {
        this.isReceiveAllTime = isActiveOnBoot;
    }

    public HashSet<Integer> getUniqueRandom() {
        return uniqueRandom;
    }

    public int getNumberTheme() {
        return numberTheme;
    }

    public int setNumberTheme(int numberTheme) {
        return this.numberTheme = numberTheme;
    }

    public int getNumberVisualizer() {
        return numberVisualizer;
    }

    public void setNumberVisualizer(int numberVisualizer) {
        this.numberVisualizer = numberVisualizer;
    }

    public static int getSelectionItemColor() {
        return SELECTION_ITEM_COLOR;
    }
}
