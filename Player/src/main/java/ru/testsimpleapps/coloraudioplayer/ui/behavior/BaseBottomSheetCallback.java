package ru.testsimpleapps.coloraudioplayer.ui.behavior;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.View;

public abstract class BaseBottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {

    public static final String TAG = BaseBottomSheetCallback.class.getSimpleName();
    protected float mDelta;
    protected int mState;

    @Override
    public void onStateChanged(@NonNull View view, int i) {
        Log.d(TAG, "onStateChanged() - state: " + i);
        mState = i;
    }

    @Override
    public void onSlide(@NonNull View view, float v) {
        Log.d(TAG, "onSlide() - delta: " + v);
        mDelta = v;
    }

}
