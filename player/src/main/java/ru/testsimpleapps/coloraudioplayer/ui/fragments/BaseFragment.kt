package ru.testsimpleapps.coloraudioplayer.ui.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import ru.testsimpleapps.coloraudioplayer.R


abstract class BaseFragment : Fragment() {

    protected var mFragmentManager: FragmentManager? = null
    protected var mParentFragmentManager: FragmentManager? = null
    protected var mSnackBar: Snackbar? = null
    protected lateinit var mToast: Toast

    val displayWidth: Int
        get() {
            val metrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.widthPixels
        }

    val displayHeight: Int
        get() {
            val metrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.heightPixels
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        init(container, savedInstanceState)
        return view
    }

    private fun init(container: ViewGroup?, savedInstanceState: Bundle?) {
        setSnackBar(container)
        setToast()
    }

    private fun setSnackBar(container: ViewGroup?) {
        if (container != null) {
            mSnackBar = Snackbar.make(container, "", Snackbar.LENGTH_SHORT)
            mSnackBar!!.duration = Snackbar.LENGTH_SHORT
        }
    }

    private fun setToast() {
        mToast = Toast.makeText(context, "", Toast.LENGTH_LONG)
        mToast.duration = Toast.LENGTH_SHORT
    }

    protected fun showSnackBar(resource: Int) {
        showSnackBar(getString(resource))
    }

    protected fun showSnackBar(string: String) {
        if (mSnackBar != null) {
            mSnackBar!!.setText(string)
            mSnackBar!!.show()
        }
    }

    protected fun showToast(resource: Int) {
        mToast.setText(resources.getString(resource))
        mToast.show()
    }

    protected fun showToast(string: String) {
        mToast.setText(string)
        mToast.show()
    }

    protected fun showFragment(fragment: Fragment, tag: String?, isAdd: Boolean) {
        if (mFragmentManager == null) {
            mFragmentManager = fragmentManager
        }
        changeFragment(mFragmentManager!!, fragment, tag, isAdd)
    }

    protected fun showParentFragment(fragment: Fragment, tag: String?) {
        if (mParentFragmentManager == null) {
            if (parentFragment != null) {
                mParentFragmentManager = parentFragment!!.fragmentManager
            }
        }
        changeFragment(mParentFragmentManager!!, fragment, tag, true)
    }

    private fun changeFragment(fragmentManager: FragmentManager,
                               fragment: Fragment,
                               tag: String?,
                               isAdd: Boolean) {
        if (fragment != null) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(R.anim.fragment_open,
                    R.anim.fragment_close,
                    R.anim.fragment_open,
                    R.anim.fragment_close)

            if (isAdd) {
                fragmentTransaction.add(R.id.frameContainer, fragment)
            } else {
                fragmentTransaction.replace(R.id.frameContainer, fragment)
            }

            if (tag != null) {
                fragmentTransaction.addToBackStack(tag)
            }

            fragmentTransaction.commit()
        }
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    companion object {

        val TAG = BaseFragment::class.java.simpleName
    }


}
