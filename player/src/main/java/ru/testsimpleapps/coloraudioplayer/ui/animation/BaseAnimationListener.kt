package ru.testsimpleapps.coloraudioplayer.ui.animation

import android.util.Log
import android.view.View
import android.view.animation.Animation

abstract class BaseAnimationListener : Animation.AnimationListener {

    protected lateinit var mView: View
    var isAnimating = false
        protected set

    constructor() {}

    constructor(view: View) {
        mView = view
    }

    override fun onAnimationStart(animation: Animation) {
        Log.d(TAG, "onAnimationStart()")
        isAnimating = true
    }

    override fun onAnimationEnd(animation: Animation) {
        Log.d(TAG, "onAnimationEnd()")
        isAnimating = false
    }

    override fun onAnimationRepeat(animation: Animation) {
        Log.d(TAG, "onAnimationRepeat()")
    }

    companion object {

        val TAG = BaseAnimationListener::class.java.simpleName
    }

}
