package ru.testsimpleapps.coloraudioplayer.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.View


abstract class BaseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected lateinit var mOnItemClickListener: OnItemClickListener
    protected lateinit var mOnItemLongClickListener: OnItemLongClickListener
    protected lateinit var mOnItemCheckListener: OnItemCheckListener

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }

    interface OnItemCheckListener {
        fun onItemCheck(view: View, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        mOnItemLongClickListener = listener
    }

    fun setOnItemCheckListener(listener: OnItemCheckListener) {
        mOnItemCheckListener = listener
    }

    companion object {

        val TYPE_HEADER = 0
        val TYPE_ITEM = 1
    }

}