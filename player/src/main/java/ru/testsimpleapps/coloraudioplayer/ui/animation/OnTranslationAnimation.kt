package ru.testsimpleapps.coloraudioplayer.ui.animation


import android.graphics.Rect
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation

class OnTranslationAnimation(view: View, duration: Int) : BaseAnimationListener(view) {
    private var mIsTranslation = false
    private var mDuration = DEFAULT_DURATION

    init {
        mDuration = duration
    }

    override fun onAnimationEnd(animation: Animation) {
        super.onAnimationEnd(animation)
        if (mIsTranslation) {
            mView.visibility = View.INVISIBLE
        } else {
            mView.visibility = View.VISIBLE
        }
    }

    fun animate(isHide: Boolean) {
        if (isHide && mView.visibility == View.VISIBLE || !isHide && mView.visibility == View.INVISIBLE) {
            mIsTranslation = isHide

            // Get view position
            val rect = Rect()
            mView.getLocalVisibleRect(rect)

            val height = mView.height
            val fromTop: Float
            val toTop: Float

            // Get new coordinates
            if (isHide) {
                fromTop = rect.top.toFloat()
                toTop = (rect.top - height).toFloat()
            } else {
                fromTop = (rect.top - height).toFloat()
                toTop = rect.top.toFloat()
            }

            // Animate transition
            val translateAnimation = TranslateAnimation(rect.left.toFloat(), rect.left.toFloat(), fromTop, toTop)
            translateAnimation.duration = mDuration.toLong()
            translateAnimation.interpolator = LinearInterpolator()
            translateAnimation.setAnimationListener(this)
            mView.startAnimation(translateAnimation)
        }
    }

    companion object {

        val DEFAULT_DURATION = 200
    }

}
