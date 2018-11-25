package ru.testsimpleapps.coloraudioplayer.ui.behavior

import android.support.design.widget.BottomSheetBehavior
import android.util.Log
import android.view.View

abstract class BaseBottomSheetCallback : BottomSheetBehavior.BottomSheetCallback() {
    protected var mDelta: Float = 0.toFloat()
    protected var mState: Int = 0

    override fun onStateChanged(view: View, i: Int) {
        Log.d(TAG, "onStateChanged() - state: $i")
        mState = i
    }

    override fun onSlide(view: View, v: Float) {
        Log.d(TAG, "onSlide() - delta: $v")
        mDelta = v
    }

    companion object {

        val TAG = BaseBottomSheetCallback::class.java.simpleName
    }

}
