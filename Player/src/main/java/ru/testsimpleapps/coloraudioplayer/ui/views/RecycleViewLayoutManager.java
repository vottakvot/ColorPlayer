package ru.testsimpleapps.coloraudioplayer.ui.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecycleViewLayoutManager extends LinearLayoutManager {

    private float mShrinkAmount = 0.5f;
    private float mShrinkDistance = 0.9f;
    private float mCenter = 1.0f;

    public RecycleViewLayoutManager(Context context) {
        super(context);
    }

    public RecycleViewLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int orientation = getOrientation();
        if (orientation == VERTICAL) {
            final int scrolled = super.scrollVerticallyBy(dy, recycler, state);
            final float midpoint = getHeight() / 2.f;
            final float d0 = 0.f;
            final float d1 = mShrinkDistance * midpoint;
            final float s0 = 1.f;
            final float s1 = 1.f - mShrinkAmount;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                final float childMidpoint = (getDecoratedBottom(child) + getDecoratedTop(child)) / 2.f;
                final float d = Math.min(d1, Math.abs(midpoint * mCenter - childMidpoint));
                final float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                child.setScaleX(scale);
                child.setScaleY(scale);
            }
            return scrolled;
        } else {
            return 0;
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int orientation = getOrientation();
        if (orientation == HORIZONTAL) {
            final int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
            final float midpoint = getWidth() / 2.f;
            final float d0 = 0.f;
            final float d1 = mShrinkDistance * midpoint;
            final float s0 = 1.f;
            final float s1 = 1.f - mShrinkAmount;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                final float childMidpoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2.f;
                final float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                final float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                child.setScaleX(scale);
                child.setScaleY(scale);
            }
            return scrolled;
        } else {
            return 0;
        }
    }

    public float getCenter() {
        return mCenter;
    }

    public void setCenter(float center) {
        mCenter = center;
    }

    public float getShrinkAmount() {
        return mShrinkAmount;
    }

    public float getShrinkDistance() {
        return mShrinkDistance;
    }

    public void setShrinkAmount(float shrinkAmount) {
        mShrinkAmount = shrinkAmount;
    }

    public void setShrinkDistance(float shrinkDistance) {
        mShrinkDistance = shrinkDistance;
    }

}