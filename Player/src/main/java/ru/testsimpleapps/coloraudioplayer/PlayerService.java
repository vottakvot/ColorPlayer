package ru.testsimpleapps.coloraudioplayer;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import ru.testsimpleapps.coloraudioplayer.managers.player.AudioPlayer;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.managers.receivers.MediaButtonsReceiver;
import ru.testsimpleapps.coloraudioplayer.managers.receivers.ViewUpdaterReceiver;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.EqualizerDialog;

/**
 * <h1>PlayerService</h1>
 * The PlayerService controls the player in the background
 *
 * @author Egor
 * @version 1.0
 * @since 2016-03-31
 */

public class PlayerService
        extends Service
        implements Handler.Callback {

    /*
    * NameAz for player service
    * */
    public static final String NAME_PLAYBACK_SERVICE = "PLAYER_SERVICE";

    /*
    * Service commands
    * */
    public static final String ACTION_PLAY = "ru.color_player.action.PLAY";
    public static final String ACTION_NEXT = "ru.color_player.action.NEXT";
    public static final String ACTION_PREVIOUS = "ru.color_player.action.PREVIOUS";
    public static final String ACTION_SEEK = "ru.color_player.action.SEEK";
    public static final String ACTION_TIMER_START = "ru.color_player.action.TIMER_START";
    public static final String ACTION_TIMER_RESET = "ru.color_player.action.TIMER_STOP";
    public static final String ACTION_AUDIO_EFFECTS = "ru.color_player.action.AUDIO_EFFECTS";
    public static final String ACTION_EXIT = "ru.color_player.action.EXIT";

    /*
    * Keys for extras
    * */
    public static final String KEY_PLAY_PAUSE = "KEY_PLAY_PAUSE";
    public static final String KEY_PLAY_NEW = "KEY_PLAY_NEW";
    public static final String KEY_SEEK = "KEY_SEEK";

    /*
    * Timer types
    * */
    public static final String TIMER_TYPE_WAKE = "WAKE";
    public static final String TIMER_TYPE_PLAY = "PLAY";
    public static final String TIMER_TYPE_PAUSE = "PAUSE";
    public static final String TIMER_TYPE_NONE = "NONE";

    private static final int NOTIFICATION_ID = 1;
    private static PlayerService sAudioService;

    /*
    * Player's objects
    * */
    private AudioPlayer mMediaPlayer;
    private PlayerConfig mPlayerConfig;
    private Visualizer mVisualizerPlayer;
    private Equalizer mEqualizer;
    private BassBoost mBassBoost;

    /*
    * Receivers
    * */
    private MediaButtonsReceiver mMediaButtonsReceiver;

    /*
    * Timer - change to alarm manager
    * */
    private Thread timerThread;

    /*
    * Loop for commands
    * */
    private HandlerThread playerThread;
    private Handler queueHandler;
    private Looper playerLooper;

    /*
    * Seek bar updater
    * */
    private SeekBarUpdater mSeekBarUpdater;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(App.TAG_APP, getClass().getSimpleName() + " - onCreate()");
        sAudioService = this;

        // Init handler
        playerThread = new HandlerThread(NAME_PLAYBACK_SERVICE, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        playerThread.start();
        playerLooper = playerThread.getLooper();
        queueHandler = new Handler(playerLooper, this);

        // Player initialisation
        mPlayerConfig = App.getContext().getPlayerConfig();
        mMediaPlayer = new AudioPlayer(getApplicationContext(), null, mPlayerConfig);
        mSeekBarUpdater = new SeekBarUpdater();
        mSeekBarUpdater.execute();


        Equalizer.Settings settings;
        BassBoost.Settings settings1;
        // Player effects
        initEffects();

        // Receiver for media buttons
        //registerMediaButtonsReceiver();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(App.TAG_APP, getClass().getSimpleName() + " - onDestroy()");
        sAudioService = null;
        mMediaPlayer.release();
        playerLooper.quit();
        mSeekBarUpdater.cancel(true);

        stopForegroundNotification();
        stopForeground(true);
        stopTimer();

        App.getContext().saveSettings();

        // Receiver for media buttons
        //unregisterMediaButtonsReceiver();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(App.TAG_APP, getClass().getSimpleName() + " - onLowMemory()");
        App.getContext().saveSettings();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            Log.d(App.TAG_APP, getClass().getSimpleName() + " - " + intent.getAction());

            // If it not command stop, then start service
            if (intent.getAction().equals(ACTION_EXIT)) {
                updateActivityFinish();
                stopSelf();
            } else {
                startForeground(NOTIFICATION_ID, createNotification(getString(R.string.notification_message)));
                Message command = Message.obtain();
                command.obj = intent;
                command.setTarget(queueHandler);
                command.sendToTarget();
            }
        }

        return START_STICKY;
    }

    @Override
    public boolean handleMessage(Message msg) {
        boolean isHandled = false;
        if (msg != null) {
            Intent intent = (Intent) msg.obj;
            String command = intent.getAction();

            if (command.equals(ACTION_PLAY)) {
                if (intent.hasExtra(KEY_PLAY_PAUSE)) {
                    isHandled = mMediaPlayer.playPause();
                    updatePlayButton(mMediaPlayer.isPlaying());
                }

                if (intent.hasExtra(KEY_PLAY_NEW)) {
                    isHandled = mMediaPlayer.playNew();
                    updatePlayButton(mMediaPlayer.isPlaying());
                }
            }

            if (command.equals(ACTION_NEXT)) {
                isHandled = mMediaPlayer.next();
                updatePlayButton(mMediaPlayer.isPlaying());
            }

            if (command.equals(ACTION_PREVIOUS)) {
                isHandled = mMediaPlayer.previous();
                updatePlayButton(mMediaPlayer.isPlaying());
            }

            if (command.equals(ACTION_SEEK)) {
                int seek = intent.getIntExtra(KEY_SEEK, AudioPlayer.MIN_SEEK_POSITION);
                isHandled = mMediaPlayer.seek(seek);
            }

            if (command.equals(ACTION_AUDIO_EFFECTS)) {

            }

            if (command.equals(ACTION_TIMER_START)) {
            }

            if (command.equals(ACTION_TIMER_RESET)) {
            }
        }


        return isHandled;
    }

    private void updatePlayButton(boolean isPlay) {
        Intent intentView = new Intent(ViewUpdaterReceiver.UPDATE_PLAY_BUTTON);
        intentView.putExtra(ViewUpdaterReceiver.UPDATE_PLAY_BUTTON, isPlay);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentView);
    }

    private void updateSeekBarPosition() {
        Intent intentView = new Intent(ViewUpdaterReceiver.UPDATE_SEEK_BAR);
        intentView.putExtra(ViewUpdaterReceiver.UPDATE_SEEK_BAR, mMediaPlayer.getConfig().getLastSeekPosition());
        LocalBroadcastManager.getInstance(PlayerService.this).sendBroadcast(intentView);
    }

    private void updateActivityFinish() {
        Intent intentView = new Intent(ViewUpdaterReceiver.UPDATE_ACTIVITY_EXIT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentView);
    }

    public void registerMediaButtonsReceiver() {
        Log.d(App.TAG_APP, getClass().getSimpleName() + " - registerMediaButtonsReceiver()");

        // if receiver already init
        if (mMediaButtonsReceiver != null)
            return;

        // Intent filter for action
        mMediaButtonsReceiver = new MediaButtonsReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
        registerReceiver(mMediaButtonsReceiver, intentFilter);

        // Audio Manager
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // For buttons action receive
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            ComponentName componentName = new ComponentName(getPackageName(), MediaButtonsReceiver.class.getName());
            audioManager.registerMediaButtonEventReceiver(componentName);
        } else {
            MediaSession mediaSession = new MediaSession(this, getPackageName());
            Intent intent = new Intent(this, MediaButtonsReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mediaSession.setMediaButtonReceiver(pendingIntent);
            mediaSession.setActive(true);

            PlaybackState state = new PlaybackState.Builder()
                    .setActions(PlaybackState.ACTION_FAST_FORWARD |
                            PlaybackState.ACTION_PAUSE |
                            PlaybackState.ACTION_PLAY |
                            PlaybackState.ACTION_PLAY_PAUSE |
                            PlaybackState.ACTION_SKIP_TO_NEXT |
                            PlaybackState.ACTION_SKIP_TO_PREVIOUS |
                            PlaybackState.ACTION_STOP)
                    .setState(PlaybackState.STATE_PLAYING, 0, 1, SystemClock.elapsedRealtime())
                    .build();
            mediaSession.setPlaybackState(state);
        }
    }

    public void unregisterMediaButtonsReceiver() {
        Log.d(App.TAG_APP, this.getClass().getName().toString() + " - unregisterActions()");

        // if receiver not init
        if (mMediaButtonsReceiver == null)
            return;

        unregisterReceiver(mMediaButtonsReceiver);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            ComponentName componentName = new ComponentName(getPackageName(), MediaButtonsReceiver.class.getName());
            audioManager.unregisterMediaButtonEventReceiver(componentName);
        } else {
            MediaSession mediaSession = new MediaSession(this, getPackageName());
            mediaSession.setActive(false);
            mediaSession.release();
        }

        mMediaButtonsReceiver = null;
    }

    private void stopForegroundNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    /*
    * This method is used init audio effects for player.
    * First, init mEqualizer, then bass boost and visualizer
    * */
    private void initEffects() {
        Log.d(App.TAG_APP, getClass().getSimpleName() + " - initEffects()");

        // Equalizer setup
        short equalizerPresent = mPlayerConfig.getEqualizerPresent();
        mEqualizer = new Equalizer(EqualizerDialog.NORMAL_PRIORITY, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        // Config with pre-settings or custom
        if (equalizerPresent > -1 && equalizerPresent < 10) {
            mEqualizer.usePreset(equalizerPresent);
        } else {
            short[] equalizerBands = mPlayerConfig.getEqualizerBands();
            for (int i = 0; i < equalizerBands.length; i++)
                mEqualizer.setBandLevel((short) i, equalizerBands[i]);
        }

        // Bass boost setup
        mBassBoost = new BassBoost(EqualizerDialog.NORMAL_PRIORITY, mMediaPlayer.getAudioSessionId());
        mBassBoost.setStrength(mPlayerConfig.getBassBoostStrength());
        mBassBoost.setEnabled(true);

        // Visualizer setup
        visualizerInit();
    }

    private void visualizerInit() {
        mVisualizerPlayer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizerPlayer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizerPlayer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {

            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);

        mVisualizerPlayer.setEnabled(true);
    }

    private void updateNotification(String trackName) {
        Notification notification = createNotification(trackName);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification createNotification(String trackName) {
        Log.d(App.TAG_APP, getClass().getSimpleName() + " - createNotification()");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Intent play/pause
        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(ACTION_PLAY);
        playIntent.putExtra(KEY_PLAY_PAUSE, KEY_PLAY_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Intent next
        Intent nextIntent = new Intent(this, PlayerService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        // Intent exit
        Intent exitIntent = new Intent(this, PlayerService.class);
        exitIntent.setAction(ACTION_EXIT);
        PendingIntent exitPendingIntent = PendingIntent.getService(this, 0, exitIntent, 0);

        // Set actions for notification buttons
        RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.notification_player_control);
        remoteView.setOnClickPendingIntent(R.id.playPauseNotification, playPendingIntent);
        remoteView.setOnClickPendingIntent(R.id.nextNotification, nextPendingIntent);
        remoteView.setOnClickPendingIntent(R.id.exitNotification, exitPendingIntent);

        // Update track name
        if (trackName != null) {
            remoteView.setTextViewText(R.id.nameTrackNotification, trackName);
        } else if (mPlayerConfig.getPlaylist().getTrackName() != null) {
            remoteView.setTextViewText(R.id.nameTrackNotification, mPlayerConfig.getPlaylist().getTrackName());
        }

        // Update play/pause icon
        if (mMediaPlayer.isPlaying()) {
            remoteView.setImageViewResource(R.id.playPauseNotification, R.drawable.pause_notification);
        } else {
            remoteView.setImageViewResource(R.id.playPauseNotification, R.drawable.play_notification);
        }

        // Create custom notification
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
                .setCustomBigContentView(remoteView)
                .build();

        return notification;
    }


    private class SeekBarUpdater extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                while (true) {
                    if (mMediaPlayer.isPlaying())
                        updateSeekBarPosition();
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    /**
     * This method is used to start timer for play or pause music.
     *
     * @param timerType This is the first paramter for select timer type
     * @param time      This is the second parameter for timer interval
     * @return void
     */
    private void startTimer(final String timerType, final int time) {
//        // If previous timer is alive - stop it
//        if(timerThread != null && timerThread.isAlive()){
//            timerThread.interrupt();
//        }
//
//        // Run new timer
//        timerThread = new Thread(new Runnable() {
//            @SuppressWarnings("WrongConstant")
//            @Override
//            public void run() {
//                try {
//                    Log.d(App.TAG_APP, getClass().getSimpleName() + " - startTimer - " + Thread.currentThread().getId());
//
//                    int timeToPause = time;
//                    String typeTimer = timerType;
//                    Calendar calendar = null;
//                    Bundle viewData =  App.getContext().getViewDataTimer();
//                    viewData.putString(SUB_ACTION_TIMER_TYPE, typeTimer);
//                    viewData.putInt(SUB_ACTION_TIMER_VISIBILITY, View.VISIBLE);
//
//                    // Wait and update view
//                    do {
//                            viewData.putInt(SUB_ACTION_TIME, timeToPause);
//                            updaterViewsHandler.sendEmptyMessage(UPDATE_TIMER);
//                            Thread.currentThread().sleep(60000);
//                            calendar = Calendar.getInstance();
//
//                            // Wake type
//                            if(typeTimer.equals(TIMER_TYPE_WAKE)){
//                                if(calendar.get(Calendar.HOUR_OF_DAY) == (timeToPause / 60) && calendar.get(Calendar.MINUTE) >= (timeToPause % 60)){
//                                    mMediaPlayer.play();
//                                    break;
//                                }
//                            }
//
//                            // Play-pause type
//                            if(typeTimer.equals(TIMER_TYPE_PAUSE) || typeTimer.equals(TIMER_TYPE_PLAY)){
//                                if(--timeToPause <= 0 && mMediaPlayer != null){
//                                    if(typeTimer.equals(TIMER_TYPE_PAUSE) && mMediaPlayer.isPlaying()){
//                                        mMediaPlayer.pause();
//                                    } else if(typeTimer.equals(TIMER_TYPE_PLAY) && !mMediaPlayer.isPlaying()){
//                                                mMediaPlayer.play();
//                                            }
//                                    break;
//                                }
//                            }
//                    } while(!Thread.currentThread().isInterrupted());
//                } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (Exception e){
//                            e.printStackTrace();
//                        }   finally {
//                                App.getContext().initDefaultViewDataTimer();
//                                updaterViewsHandler.sendEmptyMessage(UPDATE_TIMER);
//                            }
//            }
//        });
//
//        timerThread.setDaemon(true);
//        timerThread.start();
    }

    private void stopTimer() {
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }
    }

    public static PlayerService getAudioService() {
        return sAudioService;
    }

    public Equalizer getEqualizer() {
        return mEqualizer;
    }

    public BassBoost getBassBoost() {
        return mBassBoost;
    }

}