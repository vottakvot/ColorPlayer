package ru.testsimpleapps.coloraudioplayer.app

import android.app.Application

import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory

class App : Application() {

    companion object {
        val TAG = App::class.java.simpleName
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onTerminate() {
        super.onTerminate()
        CursorFactory.close()
    }

}
