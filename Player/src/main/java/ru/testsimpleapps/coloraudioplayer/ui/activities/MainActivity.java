package ru.testsimpleapps.coloraudioplayer.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.receivers.ViewUpdaterReceiver;
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

    private static Toast infoMessage;
    private static Button timerButton;
    private ViewUpdaterReceiver mViewUpdaterReceiver;

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mViewUpdaterReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mViewUpdaterReceiver, ViewUpdaterReceiver.getIntentFilter());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(MainActivity.LOG_ACTIVITY, this.getClass().getName().toString() + " - onCreate");

        setContentView(R.layout.activity_main);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(MainActivity.LOG_ACTIVITY, MainActivity.class + "- onRequestPermissionsResult - " + requestCode);
        switch (requestCode) {
            case REQUEST_PERMISSIONS_WRITE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MainActivity.restartActivity();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.permissions_write_warning), Toast.LENGTH_LONG).show();
                }

                break;
            }

            case REQUEST_PERMISSIONS_RECORD: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //PlayerService.visualizerInit();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.permissions_record_warning), Toast.LENGTH_LONG).show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }


    public static float[] getActivitySize() {
        if (context != null) {
            int resolution[] = getActivitySizeInt();
            if (resolution != null) {
                float density = ((Activity) context).getResources().getDisplayMetrics().density;
                float dpHeight = resolution[0] / density;
                float dpWidth = resolution[1] / density;

                return new float[]{dpHeight, dpWidth};
            }
        }

        return null;


    }

    public static int[] getActivitySizeInt() {
        if (context != null) {
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            return new int[]{outMetrics.heightPixels, outMetrics.widthPixels};
        }

        return null;
    }

    public static boolean getOrientation() {
        int displaySize[] = getActivitySizeInt();
        if (displaySize != null)
            return displaySize[0] > displaySize[1] ? true : false;
        return true;
    }

    public static float getTextWidth(String text) {
        return new Paint().measureText(text);
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

    private void setTimerButton() {
        timerButton = (Button) findViewById(R.id.action_bar_timer);
        //setTimerButton(App.getContext().getViewDataTimer());

        timerButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                configAdapter.getTimerDialog().show();
            }
        });
    }

    private void setFontsSizeAndGradient() {
        // Change text size programmatically
        // Instead this use various layouts and values
        int screenSize[] = getActivitySizeInt();
        float coefficient = (float) (screenSize[0] + screenSize[1]) / 1000.0f;
        TextView actionBarColor = (TextView) findViewById(R.id.action_bar_name_color);
        actionBarColor.setTextSize(actionBarColor.getTextSize() * coefficient);
        TextView actionBarAudio = (TextView) findViewById(R.id.action_bar_name_audio);
        actionBarAudio.setTextSize(actionBarAudio.getTextSize() * coefficient);
        TextView actionBarPlayer = (TextView) findViewById(R.id.action_bar_name_player);
        actionBarPlayer.setTextSize(actionBarPlayer.getTextSize() * coefficient);

        // Gradient set for text
        TextView textView = (TextView) findViewById(R.id.action_bar_name_color);
        textView.measure(0, 0);
        Shader textShader = new LinearGradient(0, 0, (float) textView.getMeasuredWidth() / 2.0f, 0,
                new int[]{Color.RED, Color.GREEN, Color.BLUE},
                null, Shader.TileMode.MIRROR);
        textView.getPaint().setShader(textShader);
    }

    public static void setTimerButton(Bundle viewData) {
        if (timerButton != null) {
//            timerButton.setText("Type: " + viewData.getString(PlayerService.SUB_ACTION_TIMER_TYPE) + "\n" +
//                    String.format("%02d", viewData.getInt(PlayerService.SUB_ACTION_TIME) / 60) +
//                    ":" + String.format("%02d", viewData.getInt(PlayerService.SUB_ACTION_TIME) % 60));
//
//            switch(viewData.getInt(PlayerService.SUB_ACTION_TIMER_VISIBILITY)){
//                case View.VISIBLE : timerButton.setVisibility(View.VISIBLE); break;
//                case View.INVISIBLE :  timerButton.setVisibility(View.INVISIBLE); break;
//                default: timerButton.setVisibility(View.VISIBLE); break;
//            }
        }
    }

    public static void stopActivity() {
        if (context != null) {
            Log.i(MainActivity.LOG_ACTIVITY, MainActivity.class + "- stopActivity");
            ((Activity) MainActivity.context).finish();
            infoMessage = null;
            context = null;
        }
    }

    public static void restartActivity() {
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 20032016;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        MainActivity.stopActivity();
    }

    public static void showInfoMessage(String message) {
        if (infoMessage != null) {
            infoMessage.setText(message);
            infoMessage.show();
        }
    }

    public static Context getContext() {
        return context;
    }

    public static final int getColor(Context context, int id) {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    private void customizeActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_bar);
    }

}
