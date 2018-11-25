package ru.testsimpleapps.coloraudioplayer.service


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.AudioManager
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Visualizer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.AsyncTask
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.media.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews

import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.app.App
import ru.testsimpleapps.coloraudioplayer.managers.player.AudioPlayer
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory
import ru.testsimpleapps.coloraudioplayer.receiver.MediaButtonsReceiver
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity


class PlayerService : Service(), Handler.Callback, AudioPlayer.OnEvents {

    /*
    * Player's objects
    * */
    private var mMediaPlayer: AudioPlayer? = null
    private var mVisualizerPlayer: Visualizer? = null
    private val mEqualizer: Equalizer? = null
    private val mBassBoost: BassBoost? = null

    private val mIBinder = LocalBinder()

    /*
    * Receivers
    * */
    private var mMediaButtonsReceiver: MediaButtonsReceiver? = null

    /*
    * Loop for commands
    * */
    private var playerThread: HandlerThread? = null
    private var queueHandler: Handler? = null
    private var playerLooper: Looper? = null

    /*
    * Seek bar updater
    * */
    private var mSeekBarUpdater: SeekBarUpdater? = null

    override fun onCreate() {
        super.onCreate()
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mIBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action != null) {
            Log.d(App.TAG, javaClass.simpleName + " - " + intent.action)

            // If it not command stop, then start service
            if (intent.action == ACTION_EXIT) {
                stopSelf()
            } else {
                startForeground(NOTIFICATION_ID, createNotification(getString(R.string.notification_message)))
                val command = Message.obtain()
                command.obj = intent
                command.target = queueHandler
                command.sendToTarget()
            }
        }

        return Service.START_NOT_STICKY
    }

    override fun handleMessage(msg: Message?): Boolean {
        var isHandled = false
        if (msg != null) {
            val intent = msg.obj as Intent
            val command = intent.action

            // Play/Pause action
            if (command == ACTION_PLAY) {
                if (intent.hasExtra(EXTRA_PLAY_POSITION)) {
                    if (CursorFactory.instance.goToId(intent.getLongExtra(EXTRA_PLAY_POSITION, IPlaylist.ERROR_CODE))) {
                        isHandled = mMediaPlayer!!.playNew()
                    }
                } else if (intent.hasExtra(EXTRA_PLAY_PAUSE)) {
                    isHandled = mMediaPlayer!!.playPause()
                } else if (intent.hasExtra(EXTRA_PLAY_EXPLORER)) {
                    val path = intent.getStringExtra(EXTRA_PLAY_EXPLORER)
                    mMediaPlayer!!.setTrackPath(path)
                    isHandled = mMediaPlayer!!.playNew()
                }
            }

            // Next action
            if (command == ACTION_NEXT) {
                isHandled = mMediaPlayer!!.next()
            }

            // Previous action
            if (command == ACTION_PREVIOUS) {
                isHandled = mMediaPlayer!!.previous()
            }

            // Seek action
            if (command == ACTION_SEEK) {
                if (intent.hasExtra(EXTRA_PLAY_PROGRESS)) {
                    isHandled = mMediaPlayer!!.seek(intent.getIntExtra(EXTRA_PLAY_PROGRESS, AudioPlayer.MIN_SEEK_POSITION))
                }
            }

            // Update control panel
            sendBroadcastPlayButton(mMediaPlayer!!.isPlaying)
        }


        return isHandled
    }

    override fun onPlay(track: String?) {
        sendBroadcastPlayButton(mMediaPlayer!!.isPlaying)
        sendBroadcastTrackName(track)
        sendBroadcastPlaylistPosition(CursorFactory.instance.position())
    }

    inner class LocalBinder : Binder() {
        val service: PlayerService
            get() = this@PlayerService
    }

    private fun init() {
        // Init handler
        playerThread = HandlerThread(NAME_PLAYBACK_SERVICE, android.os.Process.THREAD_PRIORITY_BACKGROUND)
        playerThread!!.start()
        playerLooper = playerThread!!.looper
        queueHandler = Handler(playerLooper, this)

        // Player initialisation
        PlayerConfig.instance.playlistId = 2691
        mMediaPlayer = AudioPlayer(applicationContext)
        mMediaPlayer!!.setOnEvents(this)
        PlayerConfig.instance.audioSession = mMediaPlayer!!.audioSessionId
        mSeekBarUpdater = SeekBarUpdater()
        mSeekBarUpdater!!.execute()

        // Player effects
        initEffects()

        // Receiver for media buttons
        //registerMediaButtonsReceiver();
    }

    private fun destroy() {
        mSeekBarUpdater!!.cancel(true)
        mMediaPlayer!!.release()
        playerLooper!!.quit()

        stopForegroundNotification()
        stopForeground(true)

        PlayerConfig.save()
        // Receiver for media buttons
        //unregisterMediaButtonsReceiver();
    }

    private fun updateSeekBarPosition() {

    }


    fun registerMediaButtonsReceiver() {
        Log.d(App.TAG, javaClass.simpleName + " - registerMediaButtonsReceiver()")

        // if receiver already init
        if (mMediaButtonsReceiver != null)
            return

        // Intent filter for action
        mMediaButtonsReceiver = MediaButtonsReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON)
        registerReceiver(mMediaButtonsReceiver, intentFilter)

        // Audio Manager
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // For buttons action receive
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            val componentName = ComponentName(packageName, MediaButtonsReceiver::class.java.name)
            audioManager.registerMediaButtonEventReceiver(componentName)
        } else {
            val mediaSession = MediaSession(this, packageName)
            val intent = Intent(this, MediaButtonsReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            mediaSession.setMediaButtonReceiver(pendingIntent)
            mediaSession.isActive = true

            val state = PlaybackState.Builder()
                    .setActions(PlaybackState.ACTION_FAST_FORWARD or
                            PlaybackState.ACTION_PAUSE or
                            PlaybackState.ACTION_PLAY or
                            PlaybackState.ACTION_PLAY_PAUSE or
                            PlaybackState.ACTION_SKIP_TO_NEXT or
                            PlaybackState.ACTION_SKIP_TO_PREVIOUS or
                            PlaybackState.ACTION_STOP)
                    .setState(PlaybackState.STATE_PLAYING, 0, 1f, SystemClock.elapsedRealtime())
                    .build()
            mediaSession.setPlaybackState(state)
        }
    }

    fun unregisterMediaButtonsReceiver() {
        Log.d(App.TAG, this.javaClass.name.toString() + " - unregisterActions()")

        // if receiver not init
        if (mMediaButtonsReceiver == null)
            return

        unregisterReceiver(mMediaButtonsReceiver)
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val componentName = ComponentName(packageName, MediaButtonsReceiver::class.java.name)
            audioManager.unregisterMediaButtonEventReceiver(componentName)
        } else {
            val mediaSession = MediaSession(this, packageName)
            mediaSession.isActive = false
            mediaSession.release()
        }

        mMediaButtonsReceiver = null
    }

    private fun stopForegroundNotification() {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(NOTIFICATION_ID)
    }

    /*
    * This method is used init image_audio effects for player.
    * First, init mEqualizer, then bass boost and visualizer
    * */
    private fun initEffects() {
        Log.d(App.TAG, javaClass.simpleName + " - initEffects()")

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
        visualizerInit()
    }

    private fun visualizerInit() {
        mVisualizerPlayer = Visualizer(mMediaPlayer!!.audioSessionId)
        mVisualizerPlayer!!.captureSize = Visualizer.getCaptureSizeRange()[1]
        mVisualizerPlayer!!.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
            override fun onWaveFormDataCapture(visualizer: Visualizer, bytes: ByteArray, samplingRate: Int) {

            }

            override fun onFftDataCapture(visualizer: Visualizer, bytes: ByteArray, samplingRate: Int) {}
        }, Visualizer.getMaxCaptureRate() / 2, true, false)

        mVisualizerPlayer!!.enabled = true
    }

    private fun updateNotification(trackName: String) {
        val notification = createNotification(trackName)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(trackName: String?): Notification? {
        Log.d(App.TAG, javaClass.simpleName + " - createNotification()")

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        // Intent image_play/image_pause
        val playIntent = Intent(this, PlayerService::class.java)
        playIntent.action = ACTION_PLAY
        playIntent.putExtra(KEY_PLAY_PAUSE, KEY_PLAY_PAUSE)
        val playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Intent image_next
        val nextIntent = Intent(this, PlayerService::class.java)
        nextIntent.action = ACTION_NEXT
        val nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, 0)

        // Intent exit
        val exitIntent = Intent(this, PlayerService::class.java)
        exitIntent.action = ACTION_EXIT
        val exitPendingIntent = PendingIntent.getService(this, 0, exitIntent, 0)

        // Set actions for notification buttons
        val remoteView = RemoteViews(packageName, R.layout.notification_player_control)
        remoteView.setOnClickPendingIntent(R.id.playPauseNotification, playPendingIntent)
        remoteView.setOnClickPendingIntent(R.id.nextNotification, nextPendingIntent)
        remoteView.setOnClickPendingIntent(R.id.exitNotification, exitPendingIntent)

        // Update track name
        if (trackName != null) {
            remoteView.setTextViewText(R.id.nameTrackNotification, trackName)
        } else if (CursorFactory.instance.trackName != null) {
            remoteView.setTextViewText(R.id.nameTrackNotification, CursorFactory.instance.trackName)
        }

        // Update image_play/image_pause icon
        if (mMediaPlayer!!.isPlaying) {
            remoteView.setImageViewResource(R.id.playPauseNotification, R.drawable.image_pause_notification)
        } else {
            remoteView.setImageViewResource(R.id.playPauseNotification, R.drawable.image_play_notification)
        }

        // Create custom notification

//        return NotificationCompat.Builder(this)
//                .setPriority(Notification.PRIORITY_HIGH)
//                .setColor(Color.RED)
//                .setContentIntent(pendingIntent)
//                .setOngoing(true)
//                .setSmallIcon(R.drawable.image_icon_notification_mini)
//                .setContentText(getString(R.string.notification_header))
//                .setTicker(getString(R.string.app_name))
////                .setContentText(getString(R.string.app_name))
////                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
////                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
//                .setCustomBigContentView(remoteView)
//                .build()
        return null
    }


    private inner class SeekBarUpdater : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void): Void? {
            try {
                while (true) {
                    if (mMediaPlayer!!.isPlaying) {
                        sendBroadcastProgress(mMediaPlayer!!.position, mMediaPlayer!!.duration)
                        sendBroadcastTrackPosition(CursorFactory.instance.position() + 1, CursorFactory.instance.size())
                    }

                    Thread.sleep(900)
                }
            } catch (e: InterruptedException) {
                Log.i(TAG, e.message)
            }

            return null
        }
    }

    companion object {

        val TAG = PlayerService::class.java.simpleName

        /*
    * Name for player service
    * */
        val NAME_PLAYBACK_SERVICE = "PLAYER_SERVICE"

        /*
    * Service commands
    * */
        val ACTION_PLAY = "ru.color_player.action.PLAY"
        val ACTION_NEXT = "ru.color_player.action.NEXT"
        val ACTION_PREVIOUS = "ru.color_player.action.PREVIOUS"
        val ACTION_SEEK = "ru.color_player.action.SEEK"
        val ACTION_TIMER_START = "ru.color_player.action.TIMER_START"
        val ACTION_TIMER_RESET = "ru.color_player.action.TIMER_STOP"
        val ACTION_AUDIO_EFFECTS = "ru.color_player.action.AUDIO_EFFECTS"
        val ACTION_EXIT = "ru.color_player.action.EXIT"

        /*
    * Service commands extra
    * */
        val EXTRA_PLAY_POSITION = "EXTRA_PLAY_POSITION"
        val EXTRA_PLAY_PAUSE = "EXTRA_PLAY_PAUSE"
        val EXTRA_PLAY_EXPLORER = "EXTRA_PLAY_EXPLORER"
        val EXTRA_PLAY_PROGRESS = "EXTRA_PLAY_PROGRESS"
        val EXTRA_PLAY_DURATION = "EXTRA_PLAY_DURATION"
        val EXTRA_PLAYLIST_POSITION = "EXTRA_PLAYLIST_POSITION"
        val EXTRA_PLAYLIST_TOTAL = "EXTRA_PLAYLIST_TOTAL"
        val EXTRA_PLAYLIST_NAME = "EXTRA_PLAYLIST_NAME"

        /*
    * State update
    * */
        val RECEIVER_PLAYLIST_CHANGE = "ru.color_player.action.CHANGE"
        val RECEIVER_PLAYLIST_TRACKS = "ru.color_player.action.TRACKS"
        val RECEIVER_PLAYLIST_NAME = "ru.color_player.action.NAME"
        val RECEIVER_PLAYLIST_POSITION = "ru.color_player.action.POSITION"
        val RECEIVER_PLAY_PAUSE = "ru.color_player.action.PLAY_PAUSE"
        val RECEIVER_PLAY_PROGRESS = "ru.color_player.action.PROGRESS"

        /*
    * Keys for extras
    * */
        val KEY_PLAY_PAUSE = "KEY_PLAY_PAUSE"
        val KEY_PLAY_NEW = "KEY_PLAY_NEW"
        val KEY_SEEK = "KEY_SEEK"

        /*
    * Timer types
    * */
        val TIMER_TYPE_WAKE = "WAKE"
        val TIMER_TYPE_PLAY = "PLAY"
        val TIMER_TYPE_PAUSE = "PAUSE"
        val TIMER_TYPE_NONE = "NONE"

        private val NOTIFICATION_ID = 1

        /*
    * For receiver
    * */
        fun sendBroadcastPlaylistChange() {
            val intent = Intent(RECEIVER_PLAYLIST_CHANGE)
            LocalBroadcastManager.getInstance(App.instance).sendBroadcast(intent)
        }

        fun sendBroadcastPlayButton(isPlay: Boolean) {
            val intent = Intent(RECEIVER_PLAY_PAUSE)
            intent.putExtra(EXTRA_PLAY_PAUSE, isPlay)
            LocalBroadcastManager.getInstance(App.instance).sendBroadcast(intent)
        }

        fun sendBroadcastProgress(progress: Int, duration: Int) {
            val intent = Intent(RECEIVER_PLAY_PROGRESS)
            intent.putExtra(EXTRA_PLAY_PROGRESS, progress)
            intent.putExtra(EXTRA_PLAY_DURATION, duration)
            LocalBroadcastManager.getInstance(App.instance).sendBroadcast(intent)
        }

        fun sendBroadcastTrackPosition(current: Long, total: Long) {
            val intent = Intent(RECEIVER_PLAYLIST_TRACKS)
            intent.putExtra(EXTRA_PLAYLIST_POSITION, current)
            intent.putExtra(EXTRA_PLAYLIST_TOTAL, total)
            LocalBroadcastManager.getInstance(App.instance).sendBroadcast(intent)
        }

        fun sendBroadcastTrackName(name: String?) {
            val intent = Intent(RECEIVER_PLAYLIST_NAME)
            intent.putExtra(EXTRA_PLAYLIST_NAME, name)
            LocalBroadcastManager.getInstance(App.instance).sendBroadcast(intent)
        }

        fun sendBroadcastPlaylistPosition(position: Long) {
            val intent = Intent(RECEIVER_PLAYLIST_POSITION)
            intent.putExtra(EXTRA_PLAYLIST_POSITION, position)
            LocalBroadcastManager.getInstance(App.instance).sendBroadcast(intent)
        }

        /*
    * For service command
    * */
        fun sendCommandTrackSelect(id: Long) {
            val intent = Intent(App.instance, PlayerService::class.java)
            intent.action = ACTION_PLAY
            intent.putExtra(EXTRA_PLAY_POSITION, id)
            App.instance.startService(intent)
        }

        fun sendCommandControlCheck() {
            val intent = Intent(App.instance, PlayerService::class.java)
            intent.action = ACTION_PLAY
            App.instance.startService(intent)
        }

        fun sendCommandPlayPause() {
            val intent = Intent(App.instance, PlayerService::class.java)
            intent.action = ACTION_PLAY
            intent.putExtra(EXTRA_PLAY_PAUSE, EXTRA_PLAY_PAUSE)
            App.instance.startService(intent)
        }

        fun sendCommandPlayTrack(path: String) {
            val intent = Intent(App.instance, PlayerService::class.java)
            intent.action = ACTION_PLAY
            intent.putExtra(EXTRA_PLAY_EXPLORER, path)
            App.instance.startService(intent)
        }

        fun sendCommandNext() {
            val intent = Intent(App.instance, PlayerService::class.java)
            intent.action = ACTION_NEXT
            App.instance.startService(intent)
        }

        fun sendCommandPrevious() {
            val intent = Intent(App.instance, PlayerService::class.java)
            intent.action = ACTION_PREVIOUS
            App.instance.startService(intent)
        }

        fun sendCommandSeek(progress: Int) {
            val intent = Intent(App.instance, PlayerService::class.java)
            intent.action = ACTION_SEEK
            intent.putExtra(EXTRA_PLAY_PROGRESS, progress)
            App.instance.startService(intent)
        }

        fun sendCommandExit() {
            val intent = Intent(App.instance, PlayerService::class.java)
            intent.action = ACTION_EXIT
            App.instance.startService(intent)
        }
    }

}