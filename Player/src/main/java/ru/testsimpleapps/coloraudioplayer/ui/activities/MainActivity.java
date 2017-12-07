package ru.testsimpleapps.coloraudioplayer.ui.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.service.PlayerService;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.ConfigAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.fragments.PagerFragment;

public class MainActivity extends BaseActivity {

    public static final String LOG_ACTIVITY = "PLAYER_LOG";
    public static final int REQUEST_PERMISSIONS_WRITE = 1;
    public static final int REQUEST_PERMISSIONS_RECORD = 2;

    private static Context context;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private ConfigAdapter configAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(MainActivity.LOG_ACTIVITY, MainActivity.class + "- onRequestPermissionsResult - " + requestCode);
        switch (requestCode) {
            case REQUEST_PERMISSIONS_WRITE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    restartActivity();
                } else {
                    showToast(getResources().getString(R.string.permissions_write_warning));
                }

                break;
            }

            case REQUEST_PERMISSIONS_RECORD: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //PlayerService.visualizerInit();
                } else {
                    showToast(getResources().getString(R.string.permissions_record_warning));
                }

                break;
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PlayerConfig.save();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    private void init(final Bundle savedInstanceState) {
        startService(new Intent(this, PlayerService.class));
        getPermissionMarshmallow();

        if (savedInstanceState == null) {
            showFragment(PagerFragment.newInstance(), null);
        }

        setActionBarActionDrawer();
        customizeActionBar();
    }

    private void getPermissionMarshmallow() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(MainActivity.LOG_ACTIVITY, MainActivity.class + "- getPermissionMarshmallow");

            // For work with SD_CARD
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_WRITE);
            }

            // For work with visualizer
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSIONS_RECORD);
            }
        }
    }

    private void setActionBarActionDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        configAdapter = new ConfigAdapter(MainActivity.this, drawerLayout);

        drawerList = (ListView) findViewById(R.id.drawer_list_config);
        drawerList.setAdapter(configAdapter);
        drawerList.setOnItemClickListener(configAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Drawer button
        drawerToggle = new ActionBarDrawerToggle(
                this,                  // host Activity
                drawerLayout,         // DrawerLayout object
                R.string.drawer_open,  // "open drawer" description
                R.string.drawer_close  // "close drawer" description
        ) {

            // Called when a drawer has settled in a completely closed state
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("onDrawerClosed");
                supportInvalidateOptionsMenu();
            }

            // Called when a drawer has settled in a completely open state
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("onDrawerOpened");
                supportInvalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
    }

    public void restartActivity() {
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 20032016;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
    }

    private void customizeActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_bar);
    }

}
