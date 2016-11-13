package ru.playme.color_player;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.*;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * <h1>PlayService</h1>
 * The PlayService controls the player in the background
 *
 * @author  Egor
 * @version 1.0
 * @since   2016-03-31
 */

public class PlayService
        extends Service
        implements  Handler.Callback {

    public static final String LOG_SERVICE = "PLAYER_LOG";

    public static final String ACTION_EXIT = "ru.color_player.action.EXIT";
    public static final String ACTION_PLAY_PAUSE = "ru.color_player.action.PLAY_PAUSE";
    public static final String ACTION_NEXT = "ru.color_player.action.NEXT";
    public static final String ACTION_PREVIOUS = "ru.color_player.action.PREVIOUS";
    public static final String ACTION_SEEK = "ru.color_player.action.SEEK";
    public static final String ACTION_TIMER_START = "ru.color_player.action.TIMER_START";
    public static final String ACTION_TIMER_RESET = "ru.color_player.action.TIMER_STOP";
    public static final String ACTION_UPDATE_WIDGET = "ru.color_player.action.UPDATE_WIDGET";

    public static final int JUST_DO_IT = 1;
    public static final int JUST_NEXT_IT = 2;
    public static final int JUST_PREVIOUS_IT = 3;
    public static final int JUST_SEEK_IT = 4;

    public static final int UPDATE_SEEK_BAR_DURATION = 1;
    public static final int UPDATE_SEEK_BAR_POSITION = 2;
    public static final int UPDATE_SEEK_BAR_RESET = 3;
    public static final int UPDATE_COUNT_TRACKS = 4;
    public static final int UPDATE_NAME_TRACK = 5;
    public static final int UPDATE_PLAY_PAUSE = 6;
    public static final int UPDATE_PLAYLIST_POSITION = 7;
    public static final int UPDATE_TIMER = 8;

    public static final String SUB_ACTION_PLAY_PAUSE = "PLAY_PAUSE";
    public static final String SUB_ACTION_NEW_PLAY = "NEW_PLAY";
    public static final String SUB_ACTION_HEADSET_PLAY = "HEADSET_PLAY";
    public static final String SUB_ACTION_HEADSET_PAUSE = "HEADSET_PAUSE";
    public static final String SUB_ACTION_TIMER_TYPE = "TIMER_TYPE";
    public static final String SUB_ACTION_TIME = "TIME";
    public static final String SUB_ACTION_TIMER_VISIBILITY = "VISIBILITY";
    public static final String SUB_ACTION_WIDGET_TRACK = "WIDGET_TRACK";

    public static final String TIMER_TYPE_WAKE = "WAKE";
    public static final String TIMER_TYPE_PLAY = "PLAY";
    public static final String TIMER_TYPE_PAUSE = "PAUSE";
    public static final String TIMER_TYPE_NONE = "NONE";

    private static final int NOTIFICATION_ID = 1;

    private static PlayService playService = null;

    private HandlerThread playerThread = null;
    private Handler queueHandler = null;
    private Looper playerLooper = null;

    private static CustMediaPlayer mediaPlayer = null;
    private static Visualizer visualizerPlayer = null;
    private static Equalizer equalizer = null;
    private static BassBoost bassBoost = null;

    // Timer
    private Thread timerThread = null;
    private boolean isForeground = false;

    // Update control view from thread
    private final Handler updaterViewsHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(mediaPlayer != null)
                synchronized(mediaPlayer){
                        switch(msg.what){
                            case UPDATE_SEEK_BAR_DURATION:
                                if(mediaPlayer.isPrepared()){
                                    PlayerControl.setViewsDuration(mediaPlayer.getDuration());
                                    PlayerControl.setViewsPosition(0);
                                }
                                break;

                            case UPDATE_SEEK_BAR_POSITION:
                                PlayerControl.setViewsDuration(mediaPlayer.getDuration());
                                PlayerControl.setViewsPosition(mediaPlayer.getCurrentPosition());
                                break;

                            case UPDATE_SEEK_BAR_RESET:
                                PlayerControl.setViewsDuration(1);
                                PlayerControl.setViewsPosition(0);
                                break;

                            case UPDATE_COUNT_TRACKS:
                                PlayerControl.setCountTracks();
                                break;

                            case UPDATE_NAME_TRACK:
                                if(msg.obj != null){
                                    PlayerControl.setTrackNameView(msg.obj.toString());
                                    updateNotification(msg.obj.toString());
                                    sendBroadcast(new Intent(getBaseContext(), PlayerWidget.class)
                                                        .setAction(ACTION_UPDATE_WIDGET)
                                                        .putExtra(SUB_ACTION_PLAY_PAUSE, mediaPlayer.isPlaying())
                                                        .putExtra(SUB_ACTION_WIDGET_TRACK, msg.obj.toString()));
                                }

                                break;

                            case UPDATE_PLAY_PAUSE:
                                if(msg.obj != null){
                                    PlayerControl.setPlayPauseImage((boolean) msg.obj);
                                    updateNotification(null);
                                    sendBroadcast(new Intent(getBaseContext(), PlayerWidget.class)
                                                        .setAction(ACTION_UPDATE_WIDGET)
                                                        .putExtra(SUB_ACTION_PLAY_PAUSE, (boolean) msg.obj)
                                                        .putExtra(SUB_ACTION_WIDGET_TRACK, (String)null));
                                }

                                break;

                            case UPDATE_PLAYLIST_POSITION:
                                MainPages.setPlaylistPosition();
                                break;

                            case UPDATE_TIMER:
                                MainActivity.setTimerButton(PlayerApplication.getPlayerApplication().getViewDataTimer());
                                break;
                        }
                }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_SERVICE, this.getClass().getName().toString() + " - onCreate");

        playService = this;
        playerThread = new HandlerThread("PlaybackService", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        playerThread.start();
        playerLooper = playerThread.getLooper();
        queueHandler = new Handler(playerLooper, this);
        PlayerApplication.getPlayerApplication().registerActions();
        mediaPlayer = new CustMediaPlayer(updaterViewsHandler, PlayerApplication.getPlayerApplication().getAudioManager());
        initAudioEffects();

        // Set last position
        if(PlayerApplication.getPlayerApplication().isSavePosition()){
            mediaPlayer.play(true, true);
        }
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_SERVICE, this.getClass().getName().toString() + " - onDestroy");
        mediaPlayer.release();
        playService = null;
        stopTimer();
        playerLooper.quit();
        stopForegroundNotification();

        PlayerApplication.getPlayerApplication().savePreferences();
        PlayerApplication.getPlayerApplication().closeActiveCursors();

        if(PlayerApplication.getPlayerApplication().isReceiveAllTime()){
            PlayerApplication.getPlayerApplication().unregisterActions();
        }

        super.onDestroy();

        // Stop activity instantly and clear all statics
        System.exit(0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            Log.i(LOG_SERVICE, this.getClass().getName().toString() + " - " + intent.getAction());

            if(intent.getAction().equals(ACTION_EXIT)) {
                MainActivity.stopActivity();
                stopForeground(true);
                stopSelf();
            }

            if(intent.getAction().equals(ACTION_PLAY_PAUSE)){
                startForeground(NOTIFICATION_ID, createNotification(getString(R.string.notification_message)));
                queueHandler.sendMessage(queueHandler.obtainMessage(JUST_DO_IT, intent.getExtras().getString(ACTION_PLAY_PAUSE)));
            }

            if(intent.getAction().equals(ACTION_NEXT)){
                startForeground(NOTIFICATION_ID, createNotification(getString(R.string.notification_message)));
                queueHandler.sendEmptyMessage(JUST_NEXT_IT);
            }

            if(intent.getAction().equals(ACTION_PREVIOUS)){
                startForeground(NOTIFICATION_ID, createNotification(getString(R.string.notification_message)));
                queueHandler.sendEmptyMessage(JUST_PREVIOUS_IT);
            }

            if(intent.getAction().equals(ACTION_SEEK)){
                queueHandler.sendMessage(queueHandler.obtainMessage(JUST_SEEK_IT, intent.getExtras().getInt(ACTION_SEEK)));
            }

            if(intent.getAction().equals(ACTION_TIMER_START)){
                startForeground(NOTIFICATION_ID, createNotification(getString(R.string.notification_message)));
                startTimer(intent.getExtras().getString(SUB_ACTION_TIMER_TYPE), intent.getExtras().getInt(SUB_ACTION_TIME));
            }

            if(intent.getAction().equals(ACTION_TIMER_RESET)){
                stopTimer();
            }
        }

        return START_NOT_STICKY;
    }

    private void stopTimer(){
        if(timerThread != null && timerThread.isAlive()){
            timerThread.interrupt();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(LOG_SERVICE, this.getClass().getName().toString() + " - onLowMemory");
        PlayerApplication.getPlayerApplication().savePreferences();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.i(LOG_SERVICE, this.getClass().getName().toString() + " - handleMessage");

        if(mediaPlayer != null) {
            synchronized (mediaPlayer) {
                switch (msg.what) {
                    case JUST_DO_IT:
                        justPlayPause((String) msg.obj);
                        return true;

                    case JUST_NEXT_IT:
                        mediaPlayer.next();
                        return true;

                    case JUST_PREVIOUS_IT:
                        mediaPlayer.previous();
                        return true;

                    case JUST_SEEK_IT:
                        if (mediaPlayer.isPrepared()) {
                            mediaPlayer.seekTo((int) msg.obj);
                        }
                        return true;
                }
            }
        }

        return false;
    }

    /**
     * This method is used to start timer for play or pause music.
     * @param timerType This is the first paramter for select timer type
     * @param time  This is the second parameter for timer interval
     * @return void
     */
    private void startTimer(final String timerType, final int time){
        // If previous timer is alive - stop it
        if(timerThread != null && timerThread.isAlive()){
            timerThread.interrupt();
        }

        // Run new timer
        timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(LOG_SERVICE, this.getClass().getName().toString() + " - startTimer - " + Thread.currentThread().getId());

                    int timeToPause = time;
                    String typeTimer = timerType;
                    Calendar calendar = null;
                    Bundle viewData =  PlayerApplication.getPlayerApplication().getViewDataTimer();
                    viewData.putString(SUB_ACTION_TIMER_TYPE, typeTimer);
                    viewData.putInt(SUB_ACTION_TIMER_VISIBILITY, View.VISIBLE);

                    // Wait and update view
                    do {
                            viewData.putInt(SUB_ACTION_TIME, timeToPause);
                            updaterViewsHandler.sendEmptyMessage(UPDATE_TIMER);
                            Thread.currentThread().sleep(60000);
                            calendar = Calendar.getInstance();

                            // Wake type
                            if(typeTimer.equals(TIMER_TYPE_WAKE)){
                                if(calendar.get(Calendar.HOUR_OF_DAY) == (timeToPause / 60) && calendar.get(Calendar.MINUTE) >= (timeToPause % 60)){
                                    mediaPlayer.play(true, false);
                                    break;
                                }
                            }

                            // Play-pause type
                            if(typeTimer.equals(TIMER_TYPE_PAUSE) || typeTimer.equals(TIMER_TYPE_PLAY)){
                                if(--timeToPause <= 0 && mediaPlayer != null){
                                    if(typeTimer.equals(TIMER_TYPE_PAUSE) && mediaPlayer.isPlaying()){
                                        mediaPlayer.pause();
                                    } else if(typeTimer.equals(TIMER_TYPE_PLAY) && !mediaPlayer.isPlaying()){
                                                mediaPlayer.play(true, false);
                                            }
                                    break;
                                }
                            }
                    } while(!Thread.currentThread().isInterrupted());
                } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                            e.printStackTrace();
                        }   finally {
                                PlayerApplication.getPlayerApplication().initDefaultViewDataTimer();
                                updaterViewsHandler.sendEmptyMessage(UPDATE_TIMER);
                            }
            }
        });

        timerThread.setDaemon(true);
        timerThread.start();
    }

    private void stopForegroundNotification(){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }


    /**
     * This method is used init audio effects for player.
     * First, init equalizer, then bassboost and visualizer
     */
    private void initAudioEffects(){
        Log.i(LOG_SERVICE, this.getClass().getName().toString() + " - initAudioEffects");

        try {
            // Equalizer setup
            equalizer = new Equalizer(EqualizerDialog.NORMAL_PRIORITY, mediaPlayer.getAudioSessionId());
            if(PlayerApplication.getPlayerApplication().getEqualizerPresent() != -1 &&
                    PlayerApplication.getPlayerApplication().getEqualizerPresent() < equalizer.getNumberOfPresets()){
                equalizer.usePreset(PlayerApplication.getPlayerApplication().getEqualizerPresent());
            } else {
                if(PlayerApplication.getPlayerApplication().getEqualizerBands() != null &&
                        PlayerApplication.getPlayerApplication().getEqualizerBands().length == equalizer.getNumberOfBands()){
                    for(int i = 0; i < equalizer.getNumberOfBands(); i++){
                        equalizer.setBandLevel((short)i, (short) PlayerApplication.getPlayerApplication().getEqualizerBands()[i]);
                    }
                }
            }
            equalizer.setEnabled(true);

            // Bassboost setup
            bassBoost = new BassBoost(EqualizerDialog.NORMAL_PRIORITY, mediaPlayer.getAudioSessionId());
            bassBoost.setStrength(PlayerApplication.getPlayerApplication().getBassBoostStrength());
            bassBoost.setEnabled(true);

            // Visualizer setup
            visualizerInit();
        } catch (RuntimeException e){
                e.printStackTrace();
            }
    }

    public static void visualizerInit(){
        try{
                visualizerPlayer = new Visualizer(mediaPlayer.getAudioSessionId());
                visualizerPlayer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
                visualizerPlayer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        PlaylistAdapter.updateVisualizer(bytes, mediaPlayer.isPlaying());
                    }

                    public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate){}
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
                visualizerPlayer.setEnabled(true);
        } catch(RuntimeException e){
                e.printStackTrace();
            }
    }

    private void updateNotification(String trackName){
        Notification notification = createNotification(trackName);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification createNotification(String trackName){
        Log.i(LOG_SERVICE, this.getClass().getName().toString() + " - createNotification");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent playIntent = new Intent(this, PlayService.class);
        playIntent.setAction(ACTION_PLAY_PAUSE).putExtra(PlayService.ACTION_PLAY_PAUSE, PlayService.SUB_ACTION_PLAY_PAUSE);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, PlayService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Intent exitIntent = new Intent(this, PlayService.class);
        exitIntent.setAction(ACTION_EXIT);
        PendingIntent pexitIntent = PendingIntent.getService(this, 0, exitIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(Color.RED)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.icon_notification_mini)
                .setContentText(getString(R.string.notification_header))
                .setTicker(getString(R.string.app_name))
                .setContentText(getString(R.string.app_name))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .build();

        RemoteViews remoteView = new RemoteViews(this.getPackageName(), R.layout.notification_player_control);
        remoteView.setOnClickPendingIntent(R.id.playPauseNotification, pplayIntent);
        remoteView.setOnClickPendingIntent(R.id.nextNotification, pnextIntent);
        remoteView.setOnClickPendingIntent(R.id.exitNotification, pexitIntent);

        // Update track name
        if(trackName != null){
            remoteView.setTextViewText(R.id.nameTrackNotification, trackName);
        } else if(PlayerApplication.getPlayerApplication().getTrackName() != null){
                    remoteView.setTextViewText(R.id.nameTrackNotification, PlayerApplication.getPlayerApplication().getTrackName());
                }
        // Update play/pause icon
        if(mediaPlayer.isPlaying()){
            remoteView.setImageViewResource(R.id.playPauseNotification, R.drawable.pause_notification);
        } else {
            remoteView.setImageViewResource(R.id.playPauseNotification, R.drawable.play_notification);
        }

        notification.contentView = remoteView;
        return notification;
    }

    /**
     * This method is used init audio effects for player.
     * First, init equalizer, then bassboost and visualizer
     * @param action this param for indicate type action
     * @return boolean return true, if action complete successfully
     */
    private boolean justPlayPause(String action){
        synchronized (mediaPlayer){
            // Play-Pause current song, if no song play first position
            if(action.equals(SUB_ACTION_PLAY_PAUSE) ){
                if(mediaPlayer.isPrepared()){
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    } else {
                        mediaPlayer.start();
                    }
                } else {
                    mediaPlayer.play(true,false);
                }
            }

            // Head set on
            if(action.equals(SUB_ACTION_HEADSET_PLAY)){
                if(mediaPlayer.isPrepared()){
                    if(!mediaPlayer.isPlaying()){
                        mediaPlayer.start();
                    }
                } else {
                    mediaPlayer.play(true, false);
                }
            }

            // Head set off
            if(action.equals(SUB_ACTION_HEADSET_PAUSE)){
                if(mediaPlayer.isPrepared()){
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }
                }
            }

            // Play new song
            if(action.equals(SUB_ACTION_NEW_PLAY)){
                mediaPlayer.play(true, false);
            }
        }
        return false;
    }

    public static void startService(Context context){
        if(PlayService.getPlayService() == null){
            context.startService(new Intent(context, PlayService.class));
        }
    }

    public static Equalizer getEqualizer() {
        return equalizer;
    }

    public static BassBoost getBassBoost() {
        return bassBoost;
    }

    public static final PlayService getPlayService() {
        return playService;
    }
}