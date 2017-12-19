package ru.testsimpleapps.coloraudioplayer.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.service.PlayerService;
import ru.testsimpleapps.coloraudioplayer.ui.fragments.PagerFragment;

public class MainActivity extends BaseActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_PERMISSIONS_WRITE = 1;
    public static final int REQUEST_PERMISSIONS_RECORD = 2;

    private Unbinder mUnbinder;
    @BindView(R.id.app_bar_toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawer;
    @BindView(R.id.drawer_navigation_view)
    protected NavigationView mNavigationView;
    protected ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        mUnbinder = ButterKnife.bind(this);
        init(savedInstanceState);

        if (savedInstanceState == null) {
            showFragment(PagerFragment.newInstance(), null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(MainActivity.TAG, MainActivity.class + "- onRequestPermissionsResult - " + requestCode);
        switch (requestCode) {
            case REQUEST_PERMISSIONS_WRITE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
    protected void onStop() {
        super.onStop();
        PlayerConfig.save();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    private void init(final Bundle savedInstanceState) {
        startService(new Intent(this, PlayerService.class));
        getPermissionMarshmallow();
        setActionBarActionDrawer();
    }

    private void getPermissionMarshmallow() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(MainActivity.TAG, MainActivity.class + "- getPermissionMarshmallow");

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
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mDrawer.addDrawerListener(mToggle);
        mToggle.setDrawerSlideAnimationEnabled(true);
        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.syncState();
    }


}
