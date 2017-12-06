package ru.testsimpleapps.coloraudioplayer.ui.animation;


import android.graphics.Rect;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

public class OnTranslationAnimation extends BaseAnimationListener {

    private static final int DEFAULT_DURATION = 200;
    private boolean mIsTranslation = false;
    private int mDuration = DEFAULT_DURATION;

    public OnTranslationAnimation(final View view, final int duration) {
        super(view);
        mDuration = duration;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        super.onAnimationEnd(animation);
        if (mIsTranslation) {
            mView.setVisibility(View.INVISIBLE);
        } else {
            mView.setVisibility(View.VISIBLE);
        }
    }

    public void animate(final boolean isHide) {
        if ((isHide && mView.getVisibility() == View.VISIBLE) ||
                (!isHide && mView.getVisibility() == View.INVISIBLE)) {

            mIsTranslation = isHide;

            // Get view position
            final Rect rect = new Rect();
            mView.getLocalVisibleRect(rect);

            final int height = mView.getHeight();
            float fromTop;
            float toTop;

            // Get new coordinates
            if (isHide) {
                fromTop = rect.top;
                toTop = rect.top - height;
            } else {
                fromTop = rect.top - height;
                toTop = rect.top;
            }

            // Animate transition
            final TranslateAnimation translateAnimation = new TranslateAnimation(rect.left, rect.left, fromTop, toTop);
            translateAnimation.setDuration(mDuration);
            translateAnimation.setInterpolator(new LinearInterpolator());
            translateAnimation.setAnimationListener(this);
            mView.startAnimation(translateAnimation);
        }
    }

}
