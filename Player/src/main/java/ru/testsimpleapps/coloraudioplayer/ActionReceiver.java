package ru.testsimpleapps.coloraudioplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

public class ActionReceiver extends BroadcastReceiver {
    private static final String state = "state";
    private static final int intervalAction = 400;

    private static long intervalHandset = 0;
    private static long intervalPower = 0;
    private static boolean isHeadsetOn = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(PlayService.LOG_SERVICE, this.getClass().getName().toString() + " - ActionReceiver - action - " + intent.getAction());
        KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

        // If this headset on/off
        if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
            headsetPlugAutoPlay(context, intent);
        }

        // If this headset off - NOISY
        if (isHeadsetOn && AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            noisyPause(context);
        }

        // If this power-button
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction()) ||
            Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                doublePowerButton(context, intent);
        }

        // If this media button
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            if(!mediaKeyPress(context, event)){
                abortBroadcast();
            }
        }
    }

    private boolean mediaKeyPress(Context context, KeyEvent event){
        int action = event.getAction();
        switch(event.getKeyCode()){
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if (action == KeyEvent.ACTION_DOWN) {
                        if (System.currentTimeMillis() - intervalHandset < intervalAction) {
                            context.startService(new Intent(PlayService.ACTION_NEXT)
                                    .setPackage(context.getPackageName()));
                        } else {
                            context.startService(new Intent(PlayService.ACTION_PLAY_PAUSE)
                                    .putExtra(PlayService.ACTION_PLAY_PAUSE, PlayService.SUB_ACTION_PLAY_PAUSE)
                                    .setPackage(context.getPackageName()));
                        }

                        intervalHandset = System.currentTimeMillis();
                    }
                break;

            case KeyEvent.KEYCODE_MEDIA_NEXT:
                if (action == KeyEvent.ACTION_DOWN){
                    context.startService(new Intent(PlayService.ACTION_NEXT)
                            .setPackage(context.getPackageName()));
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                if (action == KeyEvent.ACTION_DOWN){
                    context.startService(new Intent(PlayService.ACTION_PREVIOUS)
                            .setPackage(context.getPackageName()));
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (action == KeyEvent.ACTION_DOWN){
                    context.startService(new Intent(PlayService.ACTION_PLAY_PAUSE)
                            .putExtra(PlayService.ACTION_PLAY_PAUSE, PlayService.SUB_ACTION_HEADSET_PLAY)
                            .setPackage(context.getPackageName()));
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (action == KeyEvent.ACTION_DOWN){
                    context.startService(new Intent(PlayService.ACTION_PLAY_PAUSE)
                            .putExtra(PlayService.ACTION_PLAY_PAUSE, PlayService.SUB_ACTION_HEADSET_PAUSE)
                            .setPackage(context.getPackageName()));
                }
                break;

            default:
                return false;
        }

        return true;
    }

    private void headsetPlugAutoPlay(Context context, Intent intent){
        if(PlayerApplication.getPlayerApplication().isPlayHeadsetOn()){
            if(intent.hasExtra(state)){
                switch(intent.getIntExtra("state", 0)){
                    case 0:
                        context.startService(new Intent(PlayService.ACTION_PLAY_PAUSE)
                                .putExtra(PlayService.ACTION_PLAY_PAUSE, PlayService.SUB_ACTION_HEADSET_PAUSE)
                                .setPackage(context.getPackageName()));
                        break;
                    case 1:
                        context.startService(new Intent(PlayService.ACTION_PLAY_PAUSE)
                                .putExtra(PlayService.ACTION_PLAY_PAUSE, PlayService.SUB_ACTION_HEADSET_PLAY)
                                .setPackage(context.getPackageName()));
                        isHeadsetOn = true;
                        break;
                }
            }
        }
    }

    private void noisyPause(Context context){
        if(PlayerApplication.getPlayerApplication().isPlayHeadsetOn()){
            context.startService(new Intent(PlayService.ACTION_PLAY_PAUSE)
                    .putExtra(PlayService.ACTION_PLAY_PAUSE, PlayService.SUB_ACTION_HEADSET_PAUSE)
                    .setPackage(context.getPackageName()));
            isHeadsetOn = false;
        }
    }

    private void doublePowerButton(Context context, Intent intent){
        if(PlayerApplication.getPlayerApplication().isPowerButton()){
            if(System.currentTimeMillis() - intervalPower < intervalAction){
                context.startService(new Intent(PlayService.ACTION_PLAY_PAUSE)
                        .putExtra(PlayService.ACTION_PLAY_PAUSE, PlayService.SUB_ACTION_PLAY_PAUSE)
                        .setPackage(context.getPackageName()));
            }

            intervalPower = System.currentTimeMillis();
        }
    }
}
