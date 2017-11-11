package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class BaseListenerEndlessAdapter extends RecyclerView.OnScrollListener {

    private static final int VISIBLE_THRESHOLD = 5;
    private int previousTotalItemCount = 0;
    private boolean loadingMode = true;

    public void reset() {
        previousTotalItemCount = 0;
        loadingMode = true;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        final int visibleItemCount = layoutManager.getChildCount();
        final int totalItemCount = layoutManager.getItemCount();
        final int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
            if (loadingMode) {
                if (totalItemCount > previousTotalItemCount) {
                    loadingMode = false;
                    previousTotalItemCount = totalItemCount;
                }
            }
            if (!loadingMode && visibleItemCount + firstVisibleItemPosition >= totalItemCount - VISIBLE_THRESHOLD) {
                loadingMode = true;
                onLoadMore();
            }
        }
    }

    public abstract void onLoadMore();

}