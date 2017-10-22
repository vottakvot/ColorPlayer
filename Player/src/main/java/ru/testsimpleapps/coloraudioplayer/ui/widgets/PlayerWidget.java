package ru.testsimpleapps.coloraudioplayer.ui.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.PlayerService;
import ru.testsimpleapps.coloraudioplayer.R;


public class PlayerWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i, "♫♪♭♩♫♪♭♩♫♪♭♩♫♪♭♩♫♪♭♩♫♪♭♩♫♪♭♩", false);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(App.TAG_APP, getClass().getSimpleName() + " - PlayerWidget - action - " + intent.getAction());

    }


    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetID, String trackName, boolean playPause) {
        Intent playIntent = new Intent(context, PlayerService.class);
        playIntent.setAction(PlayerService.ACTION_PLAY);
        playIntent.putExtra(PlayerService.KEY_PLAY_PAUSE, PlayerService.KEY_PLAY_PAUSE);
        PendingIntent pplayIntent = PendingIntent.getService(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(context, PlayerService.class);
        nextIntent.setAction(PlayerService.ACTION_NEXT);
        PendingIntent pnextIntent = PendingIntent.getService(context, 0, nextIntent, 0);

        Intent exitIntent = new Intent(context, PlayerService.class);
        exitIntent.setAction(PlayerService.ACTION_PREVIOUS);
        PendingIntent ppreviousIntent = PendingIntent.getService(context, 0, exitIntent, 0);

        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_control);
        remoteView.setOnClickPendingIntent(R.id.widgetPlayPause, pplayIntent);
        remoteView.setOnClickPendingIntent(R.id.widgetNext, pnextIntent);
        remoteView.setOnClickPendingIntent(R.id.widgetPrevious, ppreviousIntent);

//        if(trackName != null){
//            remoteView.setTextViewText(R.id.widgetCurrentTrackName, trackName);
//        } else if(App.getAppContext().getTrackName() != null){
//            remoteView.setTextViewText(R.id.widgetCurrentTrackName, App.getAppContext().getTrackName());
//        }

        if (playPause) {
            remoteView.setImageViewResource(R.id.widgetPlayPause, R.drawable.pause);
        } else {
            remoteView.setImageViewResource(R.id.widgetPlayPause, R.drawable.play);
        }

        appWidgetManager.updateAppWidget(widgetID, remoteView);
    }
}
