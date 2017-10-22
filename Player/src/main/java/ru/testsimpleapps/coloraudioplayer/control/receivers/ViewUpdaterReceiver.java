package ru.testsimpleapps.coloraudioplayer.control.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import ru.testsimpleapps.coloraudioplayer.PlayerApplication;
import ru.testsimpleapps.coloraudioplayer.ui.PlayerControl;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;
import ru.testsimpleapps.coloraudioplayer.control.player.AudioPlayer;

public class ViewUpdaterReceiver extends BroadcastReceiver {

    public static final String UPDATE_ACTIVITY_EXIT = "UPDATE_ACTIVITY_EXIT";
    public static final String UPDATE_PLAY_BUTTON = "UPDATE_PLAY_BUTTON";
    public static final String UPDATE_SEEK_BAR = "UPDATE_SEEK_BAR";
    public static final String UPDATE_EQUALIZER_DIALOG = "UPDATE_EQUALIZER_DIALOG";

    private final MainActivity mAppCompatActivity;

    public ViewUpdaterReceiver(MainActivity appCompatActivity){
        mAppCompatActivity = appCompatActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(PlayerApplication.TAG_APP, getClass().getSimpleName() + " - onReceive()");
        if(intent != null && intent.getAction() != null) {
            String command = intent.getAction();

            if (command.equals(UPDATE_ACTIVITY_EXIT)) {
                mAppCompatActivity.finish();
            }

            if (command.equals(UPDATE_PLAY_BUTTON)) {
                PlayerControl playerControl = mAppCompatActivity.getPlayerControl();
                playerControl.setPlayPauseImage(intent.getBooleanExtra(UPDATE_PLAY_BUTTON, false));
            }

            if (command.equals(UPDATE_SEEK_BAR)) {
                PlayerControl playerControl = mAppCompatActivity.getPlayerControl();
                playerControl.setViewsPosition(intent.getIntExtra(UPDATE_SEEK_BAR, AudioPlayer.MIN_SEEK_POSITION));
            }

            if (command.equals(UPDATE_EQUALIZER_DIALOG)) {
                if (intent.hasExtra(UPDATE_EQUALIZER_DIALOG)) {

                }
            }

        }
    }

    public static IntentFilter getIntentFilter(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_PLAY_BUTTON);
        intentFilter.addAction(UPDATE_SEEK_BAR);
        intentFilter.addAction(UPDATE_ACTIVITY_EXIT);
        intentFilter.addAction(UPDATE_EQUALIZER_DIALOG);
        return intentFilter;
    }
}
