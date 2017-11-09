package ru.testsimpleapps.coloraudioplayer.ui.activities;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.AboutDialog;

public class PreferencesActivity
        extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static AboutDialog aboutDialog;
    private static Preference aboutPref;
    private static final Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            aboutDialog.show();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getContext().setCustomTheme(this);
        super.onCreate(savedInstanceState);
        aboutDialog = new AboutDialog(this);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.preferences);
            PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
            aboutPref = findPreference(getString(R.string.AboutPreferences));
            aboutPref.setOnPreferenceClickListener(onPreferenceClickListener);
        } else {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new PlayerPreferenceFragment().setListeners(this)).commit();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.ButtonsReceive))) {
            //App.getContext().setIsActiveOnBoot(sharedPreferences.getBoolean(key, false));
        }

        if (key.equals(getString(R.string.LastTrackPosition))) {
            // App.getContext().setIsSavePosition(sharedPreferences.getBoolean(key, false));
        }

//        if(key.equals(getString(R.string.HeadsetPlug))){
//            App.getContext().setIsPlayHeadsetOn(sharedPreferences.getBoolean(key, false));
//        }
//
//        if(key.equals(getString(R.string.DoublePower))){
//            App.getContext().setIsPowerButton(sharedPreferences.getBoolean(key, false));
//        }

//        if(key.equals(getString(R.string.Visualizer))){
//            App.getContext().setNumberVisualizer(Integer.parseInt(sharedPreferences.getString(key, Integer.toString(App.VISUALIZER_LINE))));
//        }
    }

    @TargetApi(11)
    public static class PlayerPreferenceFragment
            extends PreferenceFragment {

        private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = null;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            aboutPref = findPreference(getString(R.string.AboutPreferences));
            aboutPref.setOnPreferenceClickListener(onPreferenceClickListener);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        }

        public PlayerPreferenceFragment setListeners(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
            this.sharedPreferenceChangeListener = sharedPreferenceChangeListener;
            return this;
        }
    }

}
