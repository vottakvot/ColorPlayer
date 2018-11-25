package ru.testsimpleapps.coloraudioplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Parcelable
import android.util.Log
import android.view.KeyEvent

import ru.testsimpleapps.coloraudioplayer.app.App
import ru.testsimpleapps.coloraudioplayer.service.PlayerService

class MediaButtonsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && intent.action != null) {
            Log.d(App.TAG, javaClass.simpleName + " - MediaButtonsReceiver - action - " + intent.action)
            val event = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_KEY_EVENT) as KeyEvent

            // If this headset on/off
            if (Intent.ACTION_HEADSET_PLUG == intent.action) {
                headsetPlugAutoPlay(context, intent)
            }

            // If this headset off - NOISY
            if (sIsHeadsetOn && AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                noisyPause(context)
            }

            // If this power-button
            if (Intent.ACTION_SCREEN_ON == intent.action || Intent.ACTION_SCREEN_OFF == intent.action) {
                doublePowerButton(context, intent)
            }

            // If this media button
            if (Intent.ACTION_MEDIA_BUTTON == intent.action) {
                if (!mediaKeyPress(context, event)) {
                    abortBroadcast()
                }
            }
        }
    }

    private fun mediaKeyPress(context: Context, event: KeyEvent): Boolean {
        val action = event.action
        when (event.keyCode) {
            KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> if (action == KeyEvent.ACTION_DOWN) {
                if (System.currentTimeMillis() - sIntervalHandset < INTERVAL_ACTION) {
                    context.startService(Intent(PlayerService.ACTION_NEXT)
                            .setPackage(context.packageName))
                } else {
                    context.startService(Intent(PlayerService.ACTION_PLAY)
                            .putExtra(PlayerService.KEY_PLAY_PAUSE, PlayerService.KEY_PLAY_PAUSE)
                            .setPackage(context.packageName))
                }

                sIntervalHandset = System.currentTimeMillis()
            }

            KeyEvent.KEYCODE_MEDIA_NEXT -> if (action == KeyEvent.ACTION_DOWN) {
                context.startService(Intent(PlayerService.ACTION_NEXT)
                        .setPackage(context.packageName))
            }

            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> if (action == KeyEvent.ACTION_DOWN) {
                context.startService(Intent(PlayerService.ACTION_PREVIOUS)
                        .setPackage(context.packageName))
            }

            KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PAUSE -> if (action == KeyEvent.ACTION_DOWN) {
                context.startService(Intent(PlayerService.ACTION_PLAY)
                        .putExtra(PlayerService.KEY_PLAY_PAUSE, PlayerService.KEY_PLAY_PAUSE)
                        .setPackage(context.packageName))
            }

            else -> return false
        }

        return true
    }

    private fun headsetPlugAutoPlay(context: Context, intent: Intent) {
        //        if(App.getPlayerApplication().isPlayHeadsetOn()){
        //            if(intent.hasExtra(STATE)){
        //                instance.startService(new Intent(PlayerService.ACTION_PLAY)
        //                        .putExtra(PlayerService.KEY_PLAY_NEW, PlayerService.KEY_PLAY_NEW)
        //                        .setPackage(instance.getPackageName()));
        //
        //                if(intent.getIntExtra("STATE", 0) == 1)
        //                    sIsHeadsetOn = true;
        //            }
        //        }
    }

    private fun noisyPause(context: Context) {
        //        if(App.getPlayerApplication().isPlayHeadsetOn()){
        //            instance.startService(new Intent(PlayerService.ACTION_PLAY)
        //                    .putExtra(PlayerService.KEY_PLAY_NEW, PlayerService.KEY_PLAY_NEW)
        //                    .setPackage(instance.getPackageName()));
        //            sIsHeadsetOn = false;
        //        }
    }

    private fun doublePowerButton(context: Context, intent: Intent) {
        //        if(App.getPlayerApplication().isPowerButton()){
        //            if(System.currentTimeMillis() - sIntervalPower < INTERVAL_ACTION){
        //                instance.startService(new Intent(PlayerService.ACTION_PLAY)
        //                        .putExtra(PlayerService.KEY_PLAY_PAUSE, PlayerService.KEY_PLAY_PAUSE)
        //                        .setPackage(instance.getPackageName()));
        //            }
        //
        //            sIntervalPower = System.currentTimeMillis();
        //        }
    }

    companion object {

        private val STATE = "STATE"
        private val INTERVAL_ACTION = 400

        private var sIntervalHandset: Long = 0
        private val sIntervalPower: Long = 0
        private val sIsHeadsetOn = false
    }
}
