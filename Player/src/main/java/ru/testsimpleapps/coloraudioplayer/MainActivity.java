package ru.testsimpleapps.coloraudioplayer;

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
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_ACTIVITY = "PLAYER_LOG";
    public static final int REQUEST_PERMISSIONS_WRITE = 1;
    public static final int REQUEST_PERMISSIONS_RECORD = 2;

    private static Context context;
    public MainPages mMainPages = null;

    private String[] mDrawerItems = null;
    private DrawerLayout mDrawerLayout = null;
    private ListView mDrawerList = null;
    private ActionBarDrawerToggle mDrawerToggle = null;

    private PlayerControl playerControl = null;
    private ConfigAdapter configAdapter = null;

    private static Toast infoMessage = null;
    private static Button timerButton = null;

    public MainActivity(){
        super();
        Log.i(MainActivity.LOG_ACTIVITY, this.getClass().getName().toString() + " - MainActivity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(MainActivity.LOG_ACTIVITY, this.getClass().getName().toString() + " - onCreate");
        PlayerApplication.getPlayerApplication().setCustomTheme(this);
        getPermissionMarshmallow();
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.main_activity);

        infoMessage = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mMainPages = new MainPages(MainActivity.this, (ViewPager)findViewById(R.id.pager), getSupportFragmentManager());
        playerControl = new PlayerControl(MainActivity.this);

        setActionBarActionDrawer();
        customizeActionBar();
        setTimerButton();
        PlayService.startService(this);
    }

    private void getPermissionMarshmallow(){
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Log.i(MainActivity.LOG_ACTIVITY, MainActivity.class + "- getPermissionMarshmallow");

            // For work with SD_CARD
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_WRITE);
            }

            // For work with visualizer
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSIONS_RECORD);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(MainActivity.LOG_ACTIVITY, MainActivity.class + "- onRequestPermissionsResult - " + requestCode);
        switch (requestCode){
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
                    PlayService.visualizerInit();
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
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v.getId() == R.id.searchTrackInput) {
                Rect outRectButton = new Rect();
                findViewById(R.id.searchTrackButton).getGlobalVisibleRect(outRectButton);
                Rect outRectEdit = new Rect();
                v.getGlobalVisibleRect(outRectEdit);
                if (!outRectEdit.contains((int)event.getRawX(), (int)event.getRawY()) &&
                    !outRectButton.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    v.setVisibility(View.INVISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public static float[] getActivitySize(){
        if(context != null){
            int resolution[] = getActivitySizeInt();
            if(resolution != null){
                float density  = ((Activity)context).getResources().getDisplayMetrics().density;
                float dpHeight = resolution[0] / density;
                float dpWidth  = resolution[1] / density;

                return new float[] {dpHeight, dpWidth};
            }
        }

        return null;
    }

    public static int[] getActivitySizeInt() {
        if(context != null){
            Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics ();
            display.getMetrics(outMetrics);
            return new int[] {outMetrics.heightPixels, outMetrics.widthPixels};
        }

        return null;
    }

    public static boolean getOrientation(){
        int displaySize[] = getActivitySizeInt();
        if(displaySize != null)
            return displaySize[0] > displaySize[1]? true : false;
        return true;
    }

    public static float getTextWidth(String text){
        return new Paint().measureText(text);
    }

    private void setActionBarActionDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        configAdapter = new ConfigAdapter(MainActivity.this, mDrawerLayout);

        mDrawerList = (ListView) findViewById(R.id.drawer_list_config);
        mDrawerList.setAdapter(configAdapter);
        mDrawerList.setOnItemClickListener(configAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Drawer button
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  // host Activity
                mDrawerLayout,         // DrawerLayout object
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

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void setTimerButton(){
        timerButton = (Button) findViewById(R.id.action_bar_timer);
        setTimerButton(PlayerApplication.getPlayerApplication().getViewDataTimer());

        timerButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                configAdapter.getTimerDialog().show();
            }
        });
    }

    private void setFontsSizeAndGradient(){
        // Change text size programmatically
        // Instead this use various layouts and values
        int screenSize[] = getActivitySizeInt();
        float coefficient = (float)(screenSize[0] + screenSize[1]) / 1000.0f;
        TextView actionBarColor = (TextView) findViewById(R.id.action_bar_name_color);
        actionBarColor.setTextSize(actionBarColor.getTextSize() * coefficient);
        TextView actionBarAudio = (TextView) findViewById(R.id.action_bar_name_audio);
        actionBarAudio.setTextSize(actionBarAudio.getTextSize() * coefficient);
        TextView actionBarPlayer = (TextView) findViewById(R.id.action_bar_name_player);
        actionBarPlayer.setTextSize(actionBarPlayer.getTextSize() * coefficient);

        // Gradient set for text
        TextView textView = (TextView) findViewById(R.id.action_bar_name_color);
        textView.measure(0, 0);
        Shader textShader = new LinearGradient(0, 0, (float)textView.getMeasuredWidth() / 2.0f, 0,
                new int[]{Color.RED, Color.GREEN, Color.BLUE},
                null, Shader.TileMode.MIRROR);
        textView.getPaint().setShader(textShader);
    }

    public static void setTimerButton(Bundle viewData) {
        if(timerButton != null){
            timerButton.setText("Type: " + viewData.getString(PlayService.SUB_ACTION_TIMER_TYPE) + "\n" +
                    String.format("%02d", viewData.getInt(PlayService.SUB_ACTION_TIME) / 60) +
                    ":" + String.format("%02d", viewData.getInt(PlayService.SUB_ACTION_TIME) % 60));

            switch(viewData.getInt(PlayService.SUB_ACTION_TIMER_VISIBILITY)){
                case View.VISIBLE : timerButton.setVisibility(View.VISIBLE); break;
                case View.INVISIBLE :  timerButton.setVisibility(View.INVISIBLE); break;
                default: timerButton.setVisibility(View.VISIBLE); break;
            }
        }
    }

    public static void stopActivity() {
        if(context != null){
            Log.i(MainActivity.LOG_ACTIVITY, MainActivity.class + "- stopActivity");
            ((Activity)MainActivity.context).finish();
            infoMessage = null;
            context = null;
        }
    }

    public static void restartActivity(){
        try {
            Intent mStartActivity = new Intent(context, MainActivity.class);
            int mPendingIntentId = 20032016;
            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            MainActivity.stopActivity();
        } catch(Exception e){
                e.printStackTrace();
            }
    }

    public static void showInfoMessage(String message) {
        if(infoMessage != null){
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

    private void customizeActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_bar);
    }
}
