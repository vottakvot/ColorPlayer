package ru.testsimpleapps.coloraudioplayer.ui.animation;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

public abstract class BaseAnimationListener implements Animation.AnimationListener {

    public static final String TAG = BaseAnimationListener.class.getSimpleName();

    protected View mView;
    protected boolean mIsAnimating = false;

    public BaseAnimationListener() {
    }

    public BaseAnimationListener(final View view) {
        mView = view;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        Log.d(TAG, "onAnimationStart()");
        mIsAnimating = true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Log.d(TAG, "onAnimationEnd()");
        mIsAnimating = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        Log.d(TAG, "onAnimationRepeat()");
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }

}
