package ru.testsimpleapps.coloraudioplayer.control.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import ru.testsimpleapps.coloraudioplayer.App;


public class PreferenceTool {

    public static final String TAG = PreferenceTool.class.getSimpleName();

    /*
    * Keys
    * */
    private final String KEY_IS_EXPAND = "KEY_IS_EXPAND";

    private static PreferenceTool sPreferenceTool;
    private SharedPreferences mSharedPreferences;

    private PreferenceTool(@NonNull Context context) {
        mSharedPreferences = context.getSharedPreferences(TAG, context.MODE_PRIVATE);
    }

    public static PreferenceTool getInstance() {
        if (sPreferenceTool == null) {
            sPreferenceTool = new PreferenceTool(App.getAppContext());
        }

        return sPreferenceTool;
    }

    public boolean isExpand() {
        return mSharedPreferences.getBoolean(KEY_IS_EXPAND, false);
    }

    public void setExpand(boolean value) {
        mSharedPreferences.edit().putBoolean(KEY_IS_EXPAND, value).commit();
    }


}
