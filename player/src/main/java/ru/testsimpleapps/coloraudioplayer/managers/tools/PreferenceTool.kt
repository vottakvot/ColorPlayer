package ru.testsimpleapps.coloraudioplayer.managers.tools

import android.content.Context
import android.content.SharedPreferences

import ru.testsimpleapps.coloraudioplayer.app.App
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ConfigData


class PreferenceTool private constructor(context: Context) {

    /*
    * Keys
    * */
    private val KEY_CONTROL_IS_EXPAND = "KEY_CONTROL_IS_EXPAND"
    private val KEY_EXPLORER_GROUP = "KEY_EXPLORER_GROUP"
    private val KEY_EXPLORER_SORT = "KEY_EXPLORER_SORT"
    private val KEY_EXPLORER_SORT_ORDER = "KEY_EXPLORER_SORT_ORDER"
    private val KEY_CONTROL_PANEL = "KEY_CONTROL_PANEL"
    private val KEY_CONTROL_INFO = "KEY_CONTROL_INFO"
    private val KEY_PLAYLIST_EXPAND = "KEY_PLAYLIST_EXPAND"
    private val mSharedPreferences: SharedPreferences

    var controlPanelExpand: Boolean
        get() = mSharedPreferences.getBoolean(KEY_CONTROL_IS_EXPAND, false)
        set(value) {
            mSharedPreferences.edit().putBoolean(KEY_CONTROL_IS_EXPAND, value).commit()
        }

    var explorerGroupType: Int
        get() = mSharedPreferences.getInt(KEY_EXPLORER_GROUP, ConfigData.GROUP_TYPE_FOLDERS)
        set(value) {
            mSharedPreferences.edit().putInt(KEY_EXPLORER_GROUP, value).commit()
        }

    var explorerSortType: Int
        get() = mSharedPreferences.getInt(KEY_EXPLORER_SORT, ConfigData.SORT_TYPE_NAME)
        set(value) {
            mSharedPreferences.edit().putInt(KEY_EXPLORER_SORT, value).commit()
        }

    var explorerSortOrder: Int
        get() = mSharedPreferences.getInt(KEY_EXPLORER_SORT_ORDER, ConfigData.SORT_ORDER_ASC)
        set(value) {
            mSharedPreferences.edit().putInt(KEY_EXPLORER_SORT_ORDER, value).commit()
        }

    var controlPanel: Boolean
        get() = mSharedPreferences.getBoolean(KEY_CONTROL_PANEL, true)
        set(value) {
            mSharedPreferences.edit().putBoolean(KEY_CONTROL_PANEL, value).commit()
        }

    var controlInfo: Boolean
        get() = mSharedPreferences.getBoolean(KEY_CONTROL_INFO, true)
        set(value) {
            mSharedPreferences.edit().putBoolean(KEY_CONTROL_INFO, value).commit()
        }

    var playlistViewExpand: Boolean
        get() = mSharedPreferences.getBoolean(KEY_PLAYLIST_EXPAND, true)
        set(value) {
            mSharedPreferences.edit().putBoolean(KEY_PLAYLIST_EXPAND, value).commit()
        }

    init {
        mSharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
    }

    companion object {

        val TAG = PreferenceTool::class.java.simpleName

        @Volatile
        private var sPreferenceTool: PreferenceTool? = null

        val instance: PreferenceTool
            get() {
                var preferenceTool = sPreferenceTool
                if (preferenceTool == null) {
                    synchronized(PreferenceTool::class.java) {
                        preferenceTool = sPreferenceTool
                        if (preferenceTool == null) {
                            preferenceTool = PreferenceTool(App.instance)
                            sPreferenceTool = preferenceTool
                        }
                    }
                }

                return preferenceTool!!
            }
    }

}
