package ru.testsimpleapps.coloraudioplayer.ui.activities

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager

import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.AboutDialog

class PreferencesActivity : PreferenceActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aboutDialog = AboutDialog(this)

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.preferences)
            PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
            aboutPref = findPreference(getString(R.string.AboutPreferences))
            aboutPref!!.onPreferenceClickListener = onPreferenceClickListener
        } else {
            fragmentManager.beginTransaction().replace(android.R.id.content, PlayerPreferenceFragment().setListeners(this)).commit()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {

        if (key == getString(R.string.ButtonsReceive)) {
            //App.getInstance().setIsActiveOnBoot(sharedPreferences.getBoolean(key, false));
        }

        if (key == getString(R.string.LastTrackPosition)) {
            // App.getInstance().setIsSavePosition(sharedPreferences.getBoolean(key, false));
        }

        //        if(key.equals(getString(R.string.HeadsetPlug))){
        //            App.getInstance().setIsPlayHeadsetOn(sharedPreferences.getBoolean(key, false));
        //        }
        //
        //        if(key.equals(getString(R.string.DoublePower))){
        //            App.getInstance().setIsPowerButton(sharedPreferences.getBoolean(key, false));
        //        }

        //        if(key.equals(getString(R.string.Visualizer))){
        //            App.getInstance().setNumberVisualizer(Integer.parseInt(sharedPreferences.getString(key, Integer.toString(App.VISUALIZER_LINE))));
        //        }
    }

    @TargetApi(11)
    class PlayerPreferenceFragment : PreferenceFragment() {

        private var sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
            aboutPref = findPreference(getString(R.string.AboutPreferences))
            aboutPref!!.onPreferenceClickListener = onPreferenceClickListener
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        }

        fun setListeners(sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener): PlayerPreferenceFragment {
            this.sharedPreferenceChangeListener = sharedPreferenceChangeListener
            return this
        }
    }

    companion object {

        private var aboutDialog: AboutDialog? = null
        private var aboutPref: Preference? = null
        private val onPreferenceClickListener = Preference.OnPreferenceClickListener {
            aboutDialog!!.show()
            true
        }
    }

}
