package ru.testsimpleapps.coloraudioplayer.ui.activities

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.widget.Toast

import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.ui.fragments.BaseFragment

abstract class BaseActivity : AppCompatActivity() {

    protected var mFragmentManager: FragmentManager? = null
    protected lateinit var mSnackBar: Snackbar
    protected lateinit var mToast: Toast

    val displayWidth: Int
        get() {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.widthPixels
        }

    val displayHeight: Int
        get() {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.heightPixels
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)
    }

    override fun onBackPressed() {
        var handled = false
        val fragmentList = supportFragmentManager.fragments
        if (fragmentList != null) {
            for (fragment in fragmentList) {
                if (fragment is BaseFragment) {
                    handled = fragment.onBackPressed()

                    // Check child fragments
                    // Inner back press has higher priority
                    val childFragmentList = fragment.getChildFragmentManager().fragments
                    if (childFragmentList != null) {
                        for (childFragment in childFragmentList) {
                            if (childFragment is BaseFragment) {
// Todo: add check for visibility
                                if (childFragment.onBackPressed()) {
                                    return
                                }
                            }
                        }
                    }

                    // Inner back press in fragment
                    if (handled) {
                        break
                    }
                }
            }
        }

        // Outer back press
        if (!handled) {
            super.onBackPressed()
        }
    }

    private fun init(savedInstanceState: Bundle?) {
        setSnackBar()
        setToast()
    }

    protected fun showFragment(fragment: Fragment, tag: String?) {

        if (mFragmentManager == null) {
            mFragmentManager = supportFragmentManager
        }

        if (fragment != null) {
            val fragmentTransaction = mFragmentManager!!.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_open,
                    R.anim.fragment_close,
                    R.anim.fragment_open,
                    R.anim.fragment_close).replace(R.id.frameContainer, fragment)

            if (tag != null) {
                fragmentTransaction.addToBackStack(tag)
            }

            fragmentTransaction.commit()
        }
    }

    private fun setSnackBar() {
        mSnackBar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_SHORT)
        mSnackBar.duration = Snackbar.LENGTH_SHORT
    }

    private fun setToast() {
        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG)
        mToast.duration = Toast.LENGTH_SHORT
    }

    protected fun showSnackBar(resource: Int) {
        mSnackBar.setText(resources.getString(resource))
        mSnackBar.show()
    }

    protected fun showSnackBar(string: String) {
        mSnackBar.setText(string)
        mSnackBar.show()
    }

    protected fun showToast(resource: Int) {
        mToast.setText(resources.getString(resource))
        mToast.show()
    }

    protected fun showToast(string: String) {
        mToast.setText(string)
        mToast.show()
    }

}
