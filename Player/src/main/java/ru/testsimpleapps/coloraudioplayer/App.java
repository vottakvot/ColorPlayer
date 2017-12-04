package ru.testsimpleapps.coloraudioplayer;

import android.app.Application;

import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory;

public class App extends Application {

    public static final String TAG = App.class.getSimpleName();

    private static App sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        CursorFactory.close();
    }

    public static App getContext() {
        return sAppContext;
    }

}
