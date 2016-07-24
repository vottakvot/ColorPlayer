package ru.playme.color_player;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

public class PlayerPreferences
        extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static AboutDialog aboutDialog;
    private static Preference aboutPref;
    private static final Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener(){
        @Override
        public boolean onPreferenceClick(Preference preference) {
            aboutDialog.show();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PlayerApplication.getPlayerApplication().setCustomTheme(this);
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

        if(key.equals(getString(R.string.ButtonsReceive))){
            PlayerApplication.getPlayerApplication().setIsActiveOnBoot(sharedPreferences.getBoolean(key, false));
        }

        if(key.equals(getString(R.string.LastTrackPosition))){
            PlayerApplication.getPlayerApplication().setIsSavePosition(sharedPreferences.getBoolean(key, false));
        }

        if(key.equals(getString(R.string.HeadsetPlug))){
            PlayerApplication.getPlayerApplication().setIsPlayHeadsetOn(sharedPreferences.getBoolean(key, false));
        }

        if(key.equals(getString(R.string.DoublePower))){
            PlayerApplication.getPlayerApplication().setIsPowerButton(sharedPreferences.getBoolean(key, false));
        }

        if(key.equals(getString(R.string.Visualizer))){
            PlayerApplication.getPlayerApplication().setNumberVisualizer(Integer.parseInt(sharedPreferences.getString(key, Integer.toString(PlayerApplication.VISUALIZER_LINE))));
        }
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

        public PlayerPreferenceFragment setListeners(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener){
            this.sharedPreferenceChangeListener = sharedPreferenceChangeListener;
            return this;
        }
    }

}
