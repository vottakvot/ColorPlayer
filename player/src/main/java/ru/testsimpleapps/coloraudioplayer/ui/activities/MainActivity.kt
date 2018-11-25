package ru.testsimpleapps.coloraudioplayer.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import kotlinx.android.synthetic.main.activity_layout.*

import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig
import ru.testsimpleapps.coloraudioplayer.service.PlayerService
import ru.testsimpleapps.coloraudioplayer.ui.fragments.PagerFragment

class MainActivity : BaseActivity() {

    protected lateinit var mToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout)
        init(savedInstanceState)

        if (savedInstanceState == null) {
            showFragment(PagerFragment.newInstance(), null)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(MainActivity.TAG, MainActivity::class.java.toString() + "- onRequestPermissionsResult - " + requestCode)
        when (requestCode) {
            REQUEST_PERMISSIONS_WRITE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    showToast(resources.getString(R.string.permissions_write_warning))
                }
            }

            REQUEST_PERMISSIONS_RECORD -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //PlayerService.visualizerInit();
                } else {
                    showToast(resources.getString(R.string.permissions_record_warning))
                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        PlayerConfig.save()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun init(savedInstanceState: Bundle?) {
        startService(Intent(this, PlayerService::class.java))
        getPermissionMarshmallow()
        setActionBarActionDrawer()
    }

    private fun getPermissionMarshmallow() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(MainActivity.TAG, MainActivity::class.java.toString() + "- getPermissionMarshmallow")

            // For work with SD_CARD
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS_WRITE)
            }

            // For work with visualizer
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSIONS_RECORD)
            }
        }
    }

    private fun setActionBarActionDrawer() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        drawerLayout!!.addDrawerListener(mToggle)
        mToggle.isDrawerSlideAnimationEnabled = true
        mToggle.isDrawerIndicatorEnabled = true
        mToggle.syncState()
    }

    fun closeDrawer() {
        drawerLayout!!.closeDrawer(Gravity.LEFT)
    }

    companion object {

        val TAG = MainActivity::class.java.simpleName
        val REQUEST_PERMISSIONS_WRITE = 1
        val REQUEST_PERMISSIONS_RECORD = 2
    }

}
