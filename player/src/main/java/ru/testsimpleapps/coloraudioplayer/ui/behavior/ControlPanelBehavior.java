package ru.testsimpleapps.coloraudioplayer.ui.behavior;


import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class ControlPanelBehavior extends BottomSheetBehavior<LinearLayout> {

    private int mPreviousOffset = 0;

    public ControlPanelBehavior() {
        super();
    }

    public ControlPanelBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, LinearLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        if (target instanceof RecyclerView) {
            hideByOffset((RecyclerView) target);
        }

        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    private void hideByLastPosition(final RecyclerView recyclerView) {
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        final int visibleItemCount = layoutManager.getChildCount();
        final int totalItemCount = layoutManager.getItemCount();
        final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
            setState(STATE_COLLAPSED);
        }
    }

    private void hideByOffset(final RecyclerView recyclerView) {
        final int currentOffset = recyclerView.computeVerticalScrollOffset();
        if (currentOffset - mPreviousOffset > 0 && getState() != STATE_COLLAPSED) {
            setState(STATE_COLLAPSED);
        }

        mPreviousOffset = currentOffset;
    }

}
