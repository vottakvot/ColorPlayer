package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class BaseListenerEndlessAdapter extends RecyclerView.OnScrollListener {

    private static final int VISIBLE_THRESHOLD = 5;
    private int mPreviousTotalItemCount = 0;
    private boolean mLoadingMode = true;

    public void reset() {
        mPreviousTotalItemCount = 0;
        mLoadingMode = true;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            final int visibleItemCount = layoutManager.getChildCount();
            final int totalItemCount = layoutManager.getItemCount();
            final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (mLoadingMode) {
                if (totalItemCount > mPreviousTotalItemCount) {
                    mLoadingMode = false;
                    mPreviousTotalItemCount = totalItemCount;
                }
            }

            if (!mLoadingMode && visibleItemCount + firstVisibleItemPosition >= totalItemCount - VISIBLE_THRESHOLD) {
                mLoadingMode = true;
                onEndList();
            } else if (!mLoadingMode && firstVisibleItemPosition <= VISIBLE_THRESHOLD) {
                mLoadingMode = true;
                onStartList();
            }
        }
    }

    public abstract void onStartList();

    public abstract void onEndList();

}