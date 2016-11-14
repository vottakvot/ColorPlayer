package ru.testsimpleapps.coloraudioplayer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class PlayerWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i, "♫♪♭♩♫♪♭♩♫♪♭♩♫♪♭♩♫♪♭♩♫♪♭♩♫♪♭♩", false);
        }
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetID, String trackName, boolean playPause) {
        Intent playIntent = new Intent(context, PlayService.class);
        playIntent.setAction(PlayService.ACTION_PLAY_PAUSE).putExtra(PlayService.ACTION_PLAY_PAUSE, PlayService.SUB_ACTION_PLAY_PAUSE);
        PendingIntent pplayIntent = PendingIntent.getService(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(context, PlayService.class);
        nextIntent.setAction(PlayService.ACTION_NEXT);
        PendingIntent pnextIntent = PendingIntent.getService(context, 0, nextIntent, 0);

        Intent exitIntent = new Intent(context, PlayService.class);
        exitIntent.setAction(PlayService.ACTION_PREVIOUS);
        PendingIntent ppreviousIntent = PendingIntent.getService(context, 0, exitIntent, 0);

        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_control);
        remoteView.setOnClickPendingIntent(R.id.widgetPlayPause, pplayIntent);
        remoteView.setOnClickPendingIntent(R.id.widgetNext, pnextIntent);
        remoteView.setOnClickPendingIntent(R.id.widgetPrevious, ppreviousIntent);

        if(trackName != null){
            remoteView.setTextViewText(R.id.widgetCurrentTrackName, trackName);
        } else if(PlayerApplication.getPlayerApplication().getTrackName() != null){
                remoteView.setTextViewText(R.id.widgetCurrentTrackName, PlayerApplication.getPlayerApplication().getTrackName());
            }

        if(playPause){
            remoteView.setImageViewResource(R.id.widgetPlayPause, R.drawable.pause);
        } else {
            remoteView.setImageViewResource(R.id.widgetPlayPause, R.drawable.play);
        }

        appWidgetManager.updateAppWidget(widgetID, remoteView);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i(PlayService.LOG_SERVICE, this.getClass().getPackage().toString() + " - PlayerWidget - action - " + intent.getAction());

        if (intent.getAction().equalsIgnoreCase(PlayService.ACTION_UPDATE_WIDGET)){
            if(intent.hasExtra(PlayService.SUB_ACTION_PLAY_PAUSE) &&
                intent.hasExtra(PlayService.SUB_ACTION_WIDGET_TRACK)){

                AppWidgetManager man = AppWidgetManager.getInstance(context);
                int[] ids = man.getAppWidgetIds(new ComponentName(context, PlayerWidget.class));

                for(int i : ids){
                    updateWidget(context, AppWidgetManager.getInstance(context), i,
                            intent.getExtras().getString(PlayService.SUB_ACTION_WIDGET_TRACK),
                            intent.getExtras().getBoolean(PlayService.SUB_ACTION_PLAY_PAUSE));
                }
            }
        }
    }
}
