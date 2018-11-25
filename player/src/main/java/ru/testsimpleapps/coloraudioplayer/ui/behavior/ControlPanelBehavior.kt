package ru.testsimpleapps.coloraudioplayer.ui.behavior


import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class ControlPanelBehavior : BottomSheetBehavior<LinearLayout> {

    private var mPreviousOffset = 0

    constructor() : super() {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: LinearLayout, directTargetChild: View, target: View, nestedScrollAxes: Int): Boolean {
        if (target is RecyclerView) {
            hideByOffset((target as RecyclerView?)!!)
        }

        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes)
    }

    private fun hideByLastPosition(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun hideByOffset(recyclerView: RecyclerView) {
        val currentOffset = recyclerView.computeVerticalScrollOffset()
        if (currentOffset - mPreviousOffset > 0 && state != BottomSheetBehavior.STATE_COLLAPSED) {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        mPreviousOffset = currentOffset
    }

}
