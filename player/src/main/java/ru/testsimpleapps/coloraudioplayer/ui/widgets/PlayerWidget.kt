package ru.testsimpleapps.coloraudioplayer.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.app.App
import ru.testsimpleapps.coloraudioplayer.service.PlayerService


class PlayerWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (i in appWidgetIds) {
            updateWidget(context, appWidgetManager, i, "♫♪♭♩♫♪♭♩♫♪♭♩♫♪♭♩♫♪♭♩♫♪♭♩♫♪♭♩", false)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(App.TAG, javaClass.simpleName + " - PlayerWidget - action - " + intent.action)
    }

    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetID: Int, trackName: String, playPause: Boolean) {
        val playIntent = Intent(context, PlayerService::class.java)
        playIntent.action = PlayerService.ACTION_PLAY
        playIntent.putExtra(PlayerService.KEY_PLAY_PAUSE, PlayerService.KEY_PLAY_PAUSE)
        val pplayIntent = PendingIntent.getService(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(context, PlayerService::class.java)
        nextIntent.action = PlayerService.ACTION_NEXT
        val pnextIntent = PendingIntent.getService(context, 0, nextIntent, 0)

        val exitIntent = Intent(context, PlayerService::class.java)
        exitIntent.action = PlayerService.ACTION_PREVIOUS
        val ppreviousIntent = PendingIntent.getService(context, 0, exitIntent, 0)

        val remoteView = RemoteViews(context.packageName, R.layout.widget_control)
        remoteView.setOnClickPendingIntent(R.id.widgetPlayPause, pplayIntent)
        remoteView.setOnClickPendingIntent(R.id.widgetNext, pnextIntent)
        remoteView.setOnClickPendingIntent(R.id.widgetPrevious, ppreviousIntent)

        //        if(trackName != null){
        //            remoteView.setTextViewText(R.id.widgetCurrentTrackName, trackName);
        //        } else if(App.getAppContext().getTrackName() != null){
        //            remoteView.setTextViewText(R.id.widgetCurrentTrackName, App.getAppContext().getTrackName());
        //        }

        if (playPause) {
            remoteView.setImageViewResource(R.id.widgetPlayPause, R.drawable.image_pause)
        } else {
            remoteView.setImageViewResource(R.id.widgetPlayPause, R.drawable.image_play)
        }

        appWidgetManager.updateAppWidget(widgetID, remoteView)
    }
}
