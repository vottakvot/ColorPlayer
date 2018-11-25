package ru.testsimpleapps.coloraudioplayer.ui.views

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class RecyclerLayout : LinearLayoutManager {

    private val DEFAULT_CENTER = 1.0f
    private val DEFAULT_MIN_CENTER = 0.0f
    private val DEFAULT_MAX_CENTER = 2.0f
    private val THRESHOLD_CENTER = 10.0f

    var shrinkAmount = 0.5f
    var shrinkDistance = 0.9f
    var center = DEFAULT_MIN_CENTER
    private var mIsDynamic = true

    private val dynamicCenterBorders: Float
        get() {
            var center = DEFAULT_CENTER
            if (mIsDynamic) {
                val totalItemCount = itemCount - 1
                val firstPosition = findFirstVisibleItemPosition()
                val lastPosition = findLastVisibleItemPosition()
                val topBorder = firstPosition - THRESHOLD_CENTER.toInt()
                val bottomBorder = lastPosition + THRESHOLD_CENTER.toInt()
                val delta = DEFAULT_CENTER / THRESHOLD_CENTER

                if (topBorder <= 0) {
                    center = delta * firstPosition
                } else if (bottomBorder >= totalItemCount) {
                    center = DEFAULT_MAX_CENTER - delta * (totalItemCount - lastPosition)
                }

            } else {
                center = this.center
            }

            return center
        }

    private val dynamicCenterPosition: Float
        get() {
            val center: Float
            if (mIsDynamic) {
                val totalItemCount = itemCount
                val visibleItemCount = childCount
                val centerVisibleItemPosition = findFirstVisibleItemPosition() + visibleItemCount / 2
                val delta = DEFAULT_MAX_CENTER / totalItemCount.toFloat()
                center = delta * centerVisibleItemPosition
            } else {
                center = this.center
            }

            return center
        }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout) {}

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val orientation = orientation
        if (orientation == LinearLayoutManager.VERTICAL) {
            val scrolled = super.scrollVerticallyBy(dy, recycler, state)
            val midpoint = height / 2f
            val d0 = 0f
            val d1 = shrinkDistance * midpoint
            val s0 = 1f
            val s1 = 1f - shrinkAmount
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val childMidpoint = (getDecoratedBottom(child) + getDecoratedTop(child)) / 2f
                val d = Math.min(d1, Math.abs(midpoint * dynamicCenterBorders - childMidpoint))
                val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
                child.scaleX = scale
                child.scaleY = scale
            }
            return scrolled
        } else {
            return 0
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val orientation = orientation
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            val midpoint = width / 2f
            val d0 = 0f
            val d1 = shrinkDistance * midpoint
            val s0 = 1f
            val s1 = 1f - shrinkAmount
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val childMidpoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2f
                val d = Math.min(d1, Math.abs(midpoint * dynamicCenterBorders - childMidpoint))
                val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
                child.scaleX = scale
                child.scaleY = scale
            }
            return scrolled
        } else {
            return 0
        }
    }

    fun scrollToPositionWithOffsetCenter(position: Int) {
        val visibleCenter = childCount / 2 - 2
        val view = getChildAt(visibleCenter)
        var offset = 0
        if (view != null) {
            offset = view.height * visibleCenter
        }

        super.scrollToPositionWithOffset(position, offset)
    }

    fun setDynamicCenter(isDynamic: Boolean) {
        mIsDynamic = isDynamic
    }

}