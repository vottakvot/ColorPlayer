package ru.testsimpleapps.coloraudioplayer.managers.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import ru.testsimpleapps.coloraudioplayer.App;


public class PreferenceTool {

    public static final String TAG = PreferenceTool.class.getSimpleName();

    public static final int GROUP_TYPE_FOLDERS = 0;
    public static final int GROUP_TYPE_ALBUMS = 1;
    public static final int GROUP_TYPE_ARTISTS = 2;

    public static final int SORT_TYPE_AZ = 0;
    public static final int SORT_TYPE_ZA = 1;
    public static final int SORT_TYPE_VALUE = 2;
    public static final int SORT_TYPE_DATE = 3;

    /*
    * Keys
    * */
    private final String KEY_CONTROL_IS_EXPAND = "KEY_CONTROL_IS_EXPAND";
    private final String KEY_EXPLORER_GROUP = "KEY_EXPLORER_GROUP";
    private final String KEY_EXPLORER_SORT = "KEY_EXPLORER_SORT";
    private final String KEY_CONTROL_INFO = "KEY_CONTROL_INFO";

    private static PreferenceTool sPreferenceTool;
    private SharedPreferences mSharedPreferences;

    private PreferenceTool(@NonNull Context context) {
        mSharedPreferences = context.getSharedPreferences(TAG, context.MODE_PRIVATE);
    }

    public static PreferenceTool getInstance() {
        if (sPreferenceTool == null) {
            sPreferenceTool = new PreferenceTool(App.getContext());
        }

        return sPreferenceTool;
    }

    public boolean isExpand() {
        return mSharedPreferences.getBoolean(KEY_CONTROL_IS_EXPAND, false);
    }

    public void setExpand(boolean value) {
        mSharedPreferences.edit().putBoolean(KEY_CONTROL_IS_EXPAND, value).commit();
    }

    public void setGroupType(final int value) {
        mSharedPreferences.edit().putInt(KEY_EXPLORER_GROUP, value).commit();
    }

    public int getGroupType() {
        return mSharedPreferences.getInt(KEY_EXPLORER_GROUP, GROUP_TYPE_FOLDERS);
    }

    public void setSortType(final int value) {
        mSharedPreferences.edit().putInt(KEY_EXPLORER_SORT, value).commit();
    }

    public int getSortType() {
        return mSharedPreferences.getInt(KEY_EXPLORER_SORT, SORT_TYPE_AZ);
    }

    public void setControlInfo(final boolean value) {
        mSharedPreferences.edit().putBoolean(KEY_CONTROL_INFO, value).commit();
    }

    public boolean getControlInfo() {
        return mSharedPreferences.getBoolean(KEY_CONTROL_INFO, true);
    }

}
