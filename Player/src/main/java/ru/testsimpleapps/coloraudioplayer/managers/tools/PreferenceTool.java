package ru.testsimpleapps.coloraudioplayer.managers.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ConfigData;


public class PreferenceTool {

    public static final String TAG = PreferenceTool.class.getSimpleName();

    /*
    * Keys
    * */
    private final String KEY_CONTROL_IS_EXPAND = "KEY_CONTROL_IS_EXPAND";
    private final String KEY_EXPLORER_GROUP = "KEY_EXPLORER_GROUP";
    private final String KEY_EXPLORER_SORT = "KEY_EXPLORER_SORT";
    private final String KEY_EXPLORER_SORT_ORDER = "KEY_EXPLORER_SORT_ORDER";
    private final String KEY_CONTROL_PANEL = "KEY_CONTROL_PANEL";
    private final String KEY_CONTROL_INFO = "KEY_CONTROL_INFO";
    private final String KEY_PLAYLIST_EXPAND = "KEY_PLAYLIST_EXPAND";

    private static volatile PreferenceTool sPreferenceTool;
    private SharedPreferences mSharedPreferences;

    private PreferenceTool(@NonNull Context context) {
        mSharedPreferences = context.getSharedPreferences(TAG, context.MODE_PRIVATE);
    }

    public static PreferenceTool getInstance() {
        PreferenceTool preferenceTool = sPreferenceTool;
        if (preferenceTool == null) {
            synchronized (PreferenceTool.class) {
                preferenceTool = sPreferenceTool;
                if (preferenceTool == null) {
                    sPreferenceTool = preferenceTool = new PreferenceTool(App.getContext());
                }
            }
        }

        return preferenceTool;
    }

    public boolean getControlPanelExpand() {
        return mSharedPreferences.getBoolean(KEY_CONTROL_IS_EXPAND, false);
    }

    public void setControlPanelExpand(boolean value) {
        mSharedPreferences.edit().putBoolean(KEY_CONTROL_IS_EXPAND, value).commit();
    }

    public void setExplorerGroupType(final int value) {
        mSharedPreferences.edit().putInt(KEY_EXPLORER_GROUP, value).commit();
    }

    public int getExplorerGroupType() {
        return mSharedPreferences.getInt(KEY_EXPLORER_GROUP, ConfigData.GROUP_TYPE_FOLDERS);
    }

    public void setExplorerSortType(final int value) {
        mSharedPreferences.edit().putInt(KEY_EXPLORER_SORT, value).commit();
    }

    public int getExplorerSortType() {
        return mSharedPreferences.getInt(KEY_EXPLORER_SORT, ConfigData.SORT_TYPE_NAME);
    }

    public void setExplorerSortOrder(final int value) {
        mSharedPreferences.edit().putInt(KEY_EXPLORER_SORT_ORDER, value).commit();
    }

    public int getExplorerSortOrder() {
        return mSharedPreferences.getInt(KEY_EXPLORER_SORT_ORDER, ConfigData.SORT_ORDER_ASC);
    }

    public void setControlPanel(final boolean value) {
        mSharedPreferences.edit().putBoolean(KEY_CONTROL_PANEL, value).commit();
    }

    public boolean getControlPanel() {
        return mSharedPreferences.getBoolean(KEY_CONTROL_PANEL, true);
    }

    public void setControlInfo(final boolean value) {
        mSharedPreferences.edit().putBoolean(KEY_CONTROL_INFO, value).commit();
    }

    public boolean getControlInfo() {
        return mSharedPreferences.getBoolean(KEY_CONTROL_INFO, true);
    }

    public boolean getPlaylistViewExpand() {
        return mSharedPreferences.getBoolean(KEY_PLAYLIST_EXPAND, true);
    }

    public void setPlaylistViewExpand(final boolean value) {
        mSharedPreferences.edit().putBoolean(KEY_PLAYLIST_EXPAND, value);
    }

}
