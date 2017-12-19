package ru.testsimpleapps.coloraudioplayer.service;


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
import android.os.Binder;
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

import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.app.App;
import ru.testsimpleapps.coloraudioplayer.managers.player.AudioPlayer;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory;
import ru.testsimpleapps.coloraudioplayer.receiver.MediaButtonsReceiver;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;


public class PlayerService extends Service implements Handler.Callback, AudioPlayer.OnEvents {

    public static final String TAG = PlayerService.class.getSimpleName();

    /*
    * Name for player service
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
    * Service commands extra
    * */
    public static final String EXTRA_PLAY_POSITION = "EXTRA_PLAY_POSITION";
    public static final String EXTRA_PLAY_PAUSE = "EXTRA_PLAY_PAUSE";
    public static final String EXTRA_PLAY_PROGRESS = "EXTRA_PLAY_PROGRESS";
    public static final String EXTRA_PLAY_DURATION = "EXTRA_PLAY_DURATION";
    public static final String EXTRA_PLAYLIST_POSITION = "EXTRA_PLAYLIST_POSITION";
    public static final String EXTRA_PLAYLIST_TOTAL = "EXTRA_PLAYLIST_TOTAL";
    public static final String EXTRA_PLAYLIST_NAME = "EXTRA_PLAYLIST_NAME";

    /*
    * State update
    * */
    public static final String RECEIVER_PLAYLIST_ADD = "ru.color_player.action.ADD";
    public static final String RECEIVER_PLAYLIST_TRACKS = "ru.color_player.action.TRACKS";
    public static final String RECEIVER_PLAYLIST_NAME = "ru.color_player.action.NAME";
    public static final String RECEIVER_PLAYLIST_POSITION = "ru.color_player.action.POSITION";
    public static final String RECEIVER_PLAY_PAUSE = "ru.color_player.action.PLAY_PAUSE";
    public static final String RECEIVER_PLAY_PROGRESS = "ru.color_player.action.PROGRESS";

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

    /*
    * Player's objects
    * */
    private AudioPlayer mMediaPlayer;
    private Visualizer mVisualizerPlayer;
    private Equalizer mEqualizer;
    private BassBoost mBassBoost;

    private IBinder mIBinder = new LocalBinder();

    /*
    * Receivers
    * */
    private MediaButtonsReceiver mMediaButtonsReceiver;

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
        Log.d(App.TAG, getClass().getSimpleName() + " - onCreate()");
        init();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(App.TAG, getClass().getSimpleName() + " - onDestroy()");
        destroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            Log.d(App.TAG, getClass().getSimpleName() + " - " + intent.getAction());

            // If it not command stop, then start service
            if (intent.getAction().equals(ACTION_EXIT)) {
                stopSelf();
            } else {
                startForeground(NOTIFICATION_ID, createNotification(getString(R.string.notification_message)));
                Message command = Message.obtain();
                command.obj = intent;
                command.setTarget(queueHandler);
                command.sendToTarget();
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public boolean handleMessage(Message msg) {
        boolean isHandled = false;
        if (msg != null) {
            synchronized (mMediaPlayer) {
                final Intent intent = (Intent) msg.obj;
                final String command = intent.getAction();

                // Play/Pause action
                if (command.equals(ACTION_PLAY)) {
                    if (intent.hasExtra(EXTRA_PLAY_POSITION)) {
                        if (CursorFactory.getInstance().goToId(intent.getLongExtra(EXTRA_PLAY_POSITION, IPlaylist.NOT_INIT))) {
                            isHandled = mMediaPlayer.playNew();
                        }
                    } else if (intent.hasExtra(EXTRA_PLAY_PAUSE)) {
                        isHandled = mMediaPlayer.playPause();
                    }
                }

                // Next action
                if (command.equals(ACTION_NEXT)) {
                    isHandled = mMediaPlayer.next();
                }

                // Previous action
                if (command.equals(ACTION_PREVIOUS)) {
                    isHandled = mMediaPlayer.previous();
                }

                // Seek action
                if (command.equals(ACTION_SEEK)) {
                    if (intent.hasExtra(EXTRA_PLAY_PROGRESS)) {
                        isHandled = mMediaPlayer.seek(intent.getIntExtra(EXTRA_PLAY_PROGRESS, AudioPlayer.MIN_SEEK_POSITION));
                    }
                }

                // Update views
                sendBroadcastPlayButton(mMediaPlayer.isPlaying());
            }
        }

        return isHandled;
    }

    @Override
    public void onPlay() {
        sendBroadcastTrackName(CursorFactory.getInstance().getTrackName());
        sendBroadcastPlaylistPosition();
    }

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }

    private void init() {
        // Init handler
        playerThread = new HandlerThread(NAME_PLAYBACK_SERVICE, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        playerThread.start();
        playerLooper = playerThread.getLooper();
        queueHandler = new Handler(playerLooper, this);

        // Player initialisation
        PlayerConfig.getInstance().setPlaylistId(2691);
        mMediaPlayer = new AudioPlayer(getApplicationContext());
        mMediaPlayer.setOnEvents(this);
        mSeekBarUpdater = new SeekBarUpdater();
        mSeekBarUpdater.execute();

        // Player effects
        initEffects();

        // Receiver for media buttons
        //registerMediaButtonsReceiver();
    }

    private void destroy() {
        mSeekBarUpdater.cancel(true);
        mMediaPlayer.release();
        playerLooper.quit();

        stopForegroundNotification();
        stopForeground(true);

        PlayerConfig.save();
        // Receiver for media buttons
        //unregisterMediaButtonsReceiver();
    }

    private void updateSeekBarPosition() {

    }


    public void registerMediaButtonsReceiver() {
        Log.d(App.TAG, getClass().getSimpleName() + " - registerMediaButtonsReceiver()");

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
        Log.d(App.TAG, this.getClass().getName().toString() + " - unregisterActions()");

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
    * This method is used init image_audio effects for player.
    * First, init mEqualizer, then bass boost and visualizer
    * */
    private void initEffects() {
        Log.d(App.TAG, getClass().getSimpleName() + " - initEffects()");

//        // Equalizer setup
//        short equalizerPresent = mPlayerConfig.getEqualizerPresent();
//        mEqualizer = new Equalizer(EqualizerDialog.NORMAL_PRIORITY, mMediaPlayer.getAudioSessionId());
//        mEqualizer.setEnabled(true);
//
//        // Config with pre-settings or custom
//        if (equalizerPresent > -1 && equalizerPresent < 10) {
//            mEqualizer.usePreset(equalizerPresent);
//        } else {
//            short[] equalizerBands = mPlayerConfig.getEqualizerBands();
//            for (int i = 0; i < equalizerBands.length; i++)
//                mEqualizer.setBandLevel((short) i, equalizerBands[i]);
//        }
//
//        // Bass boost setup
//        mBassBoost = new BassBoost(EqualizerDialog.NORMAL_PRIORITY, mMediaPlayer.getAudioSessionId());
//        mBassBoost.setStrength(mPlayerConfig.getBassBoostStrength());
//        mBassBoost.setEnabled(true);

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
        Log.d(App.TAG, getClass().getSimpleName() + " - createNotification()");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Intent image_play/image_pause
        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(ACTION_PLAY);
        playIntent.putExtra(KEY_PLAY_PAUSE, KEY_PLAY_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Intent image_next
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
        } else if (CursorFactory.getInstance().getTrackName() != null) {
            remoteView.setTextViewText(R.id.nameTrackNotification, CursorFactory.getInstance().getTrackName());
        }

        // Update image_play/image_pause icon
        if (mMediaPlayer.isPlaying()) {
            remoteView.setImageViewResource(R.id.playPauseNotification, R.drawable.image_pause_notification);
        } else {
            remoteView.setImageViewResource(R.id.playPauseNotification, R.drawable.image_play_notification);
        }

        // Create custom notification
        Notification notification = new NotificationCompat.Builder(this)
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(Color.RED)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.image_icon_notification_mini)
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
                    synchronized (mMediaPlayer) {
                        if (mMediaPlayer.isPlaying()) {
                            sendBroadcastProgress(mMediaPlayer.getPosition(), mMediaPlayer.getDuration());
                            sendBroadcastTrackPosition(CursorFactory.getInstance().position() + 1, CursorFactory.getInstance().size());
                        }
                    }

                    Thread.sleep(900);
                }
            } catch (InterruptedException e) {
                Log.i(TAG, e.getMessage());
            }

            return null;
        }
    }

    /*
    * For receiver
    * */
    public static void sendBroadcastPlaylistUpdate() {
        Intent intent = new Intent(RECEIVER_PLAYLIST_ADD);
        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
    }

    public static void sendBroadcastPlayButton(final boolean isPlay) {
        Intent intent = new Intent(RECEIVER_PLAY_PAUSE);
        intent.putExtra(EXTRA_PLAY_PAUSE, isPlay);
        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
    }

    public static void sendBroadcastProgress(final int progress, final int duration) {
        Intent intent = new Intent(RECEIVER_PLAY_PROGRESS);
        intent.putExtra(EXTRA_PLAY_PROGRESS, progress);
        intent.putExtra(EXTRA_PLAY_DURATION, duration);
        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
    }

    public static void sendBroadcastTrackPosition(final long current, final long total) {
        Intent intent = new Intent(RECEIVER_PLAYLIST_TRACKS);
        intent.putExtra(EXTRA_PLAYLIST_POSITION, current);
        intent.putExtra(EXTRA_PLAYLIST_TOTAL, total);
        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
    }

    public static void sendBroadcastTrackName(final String name) {
        Intent intent = new Intent(RECEIVER_PLAYLIST_NAME);
        intent.putExtra(EXTRA_PLAYLIST_NAME, name);
        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
    }

    public static void sendBroadcastPlaylistPosition() {
        Intent intent = new Intent(RECEIVER_PLAYLIST_POSITION);
        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
    }


    /*
    * For service command
    * */
    public static void sendCommandTrackSelect(final long id) {
        Intent intent = new Intent(App.getContext(), PlayerService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(EXTRA_PLAY_POSITION, id);
        App.getContext().startService(intent);
    }

    public static void sendCommandControlCheck() {
        Intent intent = new Intent(App.getContext(), PlayerService.class);
        intent.setAction(ACTION_PLAY);
        App.getContext().startService(intent);
    }

    public static void sendCommandPlayPause() {
        Intent intent = new Intent(App.getContext(), PlayerService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(EXTRA_PLAY_PAUSE, EXTRA_PLAY_PAUSE);
        App.getContext().startService(intent);
    }

    public static void sendCommandNext() {
        Intent intent = new Intent(App.getContext(), PlayerService.class);
        intent.setAction(ACTION_NEXT);
        App.getContext().startService(intent);
    }

    public static void sendCommandPrevious() {
        Intent intent = new Intent(App.getContext(), PlayerService.class);
        intent.setAction(ACTION_PREVIOUS);
        App.getContext().startService(intent);
    }

    public static void sendCommandSeek(final int progress) {
        Intent intent = new Intent(App.getContext(), PlayerService.class);
        intent.setAction(ACTION_SEEK);
        intent.putExtra(EXTRA_PLAY_PROGRESS, progress);
        App.getContext().startService(intent);
    }


}