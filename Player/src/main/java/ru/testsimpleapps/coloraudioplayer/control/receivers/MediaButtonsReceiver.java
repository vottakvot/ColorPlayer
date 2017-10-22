package ru.testsimpleapps.coloraudioplayer.control.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.PlayerService;

public class MediaButtonsReceiver extends BroadcastReceiver {

    private static final String STATE = "STATE";
    private static final int INTERVAL_ACTION = 400;

    private static long sIntervalHandset = 0;
    private static long sIntervalPower = 0;
    private static boolean sIsHeadsetOn = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            Log.d(App.TAG_APP, getClass().getSimpleName() + " - MediaButtonsReceiver - action - " + intent.getAction());
            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            // If this headset on/off
            if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
                headsetPlugAutoPlay(context, intent);
            }

            // If this headset off - NOISY
            if (sIsHeadsetOn && AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                noisyPause(context);
            }

            // If this power-button
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction()) ||
                    Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                doublePowerButton(context, intent);
            }

            // If this media button
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                if (!mediaKeyPress(context, event)) {
                    abortBroadcast();
                }
            }
        }
    }

    private boolean mediaKeyPress(Context context, KeyEvent event) {
        int action = event.getAction();
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (System.currentTimeMillis() - sIntervalHandset < INTERVAL_ACTION) {
                        context.startService(new Intent(PlayerService.ACTION_NEXT)
                                .setPackage(context.getPackageName()));
                    } else {
                        context.startService(new Intent(PlayerService.ACTION_PLAY)
                                .putExtra(PlayerService.KEY_PLAY_PAUSE, PlayerService.KEY_PLAY_PAUSE)
                                .setPackage(context.getPackageName()));
                    }

                    sIntervalHandset = System.currentTimeMillis();
                }
                break;

            case KeyEvent.KEYCODE_MEDIA_NEXT:
                if (action == KeyEvent.ACTION_DOWN) {
                    context.startService(new Intent(PlayerService.ACTION_NEXT)
                            .setPackage(context.getPackageName()));
                }
                break;

            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                if (action == KeyEvent.ACTION_DOWN) {
                    context.startService(new Intent(PlayerService.ACTION_PREVIOUS)
                            .setPackage(context.getPackageName()));
                }
                break;

            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (action == KeyEvent.ACTION_DOWN) {
                    context.startService(new Intent(PlayerService.ACTION_PLAY)
                            .putExtra(PlayerService.KEY_PLAY_PAUSE, PlayerService.KEY_PLAY_PAUSE)
                            .setPackage(context.getPackageName()));
                }
                break;

            default:
                return false;
        }

        return true;
    }

    private void headsetPlugAutoPlay(Context context, Intent intent) {
//        if(App.getPlayerApplication().isPlayHeadsetOn()){
//            if(intent.hasExtra(STATE)){
//                context.startService(new Intent(PlayerService.ACTION_PLAY)
//                        .putExtra(PlayerService.KEY_PLAY_NEW, PlayerService.KEY_PLAY_NEW)
//                        .setPackage(context.getPackageName()));
//
//                if(intent.getIntExtra("STATE", 0) == 1)
//                    sIsHeadsetOn = true;
//            }
//        }
    }

    private void noisyPause(Context context) {
//        if(App.getPlayerApplication().isPlayHeadsetOn()){
//            context.startService(new Intent(PlayerService.ACTION_PLAY)
//                    .putExtra(PlayerService.KEY_PLAY_NEW, PlayerService.KEY_PLAY_NEW)
//                    .setPackage(context.getPackageName()));
//            sIsHeadsetOn = false;
//        }
    }

    private void doublePowerButton(Context context, Intent intent) {
//        if(App.getPlayerApplication().isPowerButton()){
//            if(System.currentTimeMillis() - sIntervalPower < INTERVAL_ACTION){
//                context.startService(new Intent(PlayerService.ACTION_PLAY)
//                        .putExtra(PlayerService.KEY_PLAY_PAUSE, PlayerService.KEY_PLAY_PAUSE)
//                        .setPackage(context.getPackageName()));
//            }
//
//            sIntervalPower = System.currentTimeMillis();
//        }
    }
}
