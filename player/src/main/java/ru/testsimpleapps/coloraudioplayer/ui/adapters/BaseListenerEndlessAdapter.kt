package ru.testsimpleapps.coloraudioplayer.ui.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

abstract class BaseListenerEndlessAdapter : RecyclerView.OnScrollListener() {
    private var mPreviousTotalItemCount = 0
    private var mLoadingMode = true

    fun reset() {
        mPreviousTotalItemCount = 0
        mLoadingMode = true
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
            val layoutManager = recyclerView!!.layoutManager as LinearLayoutManager
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if (mLoadingMode) {
                if (totalItemCount > mPreviousTotalItemCount) {
                    mLoadingMode = false
                    mPreviousTotalItemCount = totalItemCount
                }
            }

            if (!mLoadingMode && visibleItemCount + firstVisibleItemPosition >= totalItemCount - VISIBLE_THRESHOLD) {
                mLoadingMode = true
                onEndList()
            } else if (!mLoadingMode && firstVisibleItemPosition <= VISIBLE_THRESHOLD) {
                mLoadingMode = true
                onStartList()
            }
        }
    }

    abstract fun onStartList()

    abstract fun onEndList()

    companion object {

        private val VISIBLE_THRESHOLD = 5
    }

}