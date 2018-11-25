package ru.testsimpleapps.coloraudioplayer.ui.adapters

import java.util.ArrayList

abstract class BaseListAdapter<D> : BaseAdapter() {

    protected var mList: MutableList<D>? = null

    val itemList: List<D>?
        get() = mList

    override fun getItemCount(): Int {
        return if (mList != null) mList!!.size else 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun addItems(list: List<D>?) {
        if (mList == null) {
            mList = ArrayList()
        }

        if (list != null) {
            val positionStart = mList!!.size
            mList!!.addAll(list)
            notifyDataSetChanged()
            notifyItemRangeInserted(positionStart, list.size)
        }
    }

    fun addItemsAtTop(list: List<D>?) {
        if (mList == null) {
            mList = ArrayList()
        }

        if (list != null) {
            mList!!.addAll(0, list)
            notifyItemRangeInserted(0, list.size)
        }
    }

    open fun setItems(list: List<D>?) {
        if (list != null) {
            if (mList != null) {
                mList!!.clear()
            } else {
                mList = ArrayList()
            }
            mList!!.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun addItem(item: D) {
        if (mList == null) {
            mList = ArrayList()
        }

        mList!!.add(item)
        notifyItemInserted(mList!!.size)
    }

    fun addItemAtTop(item: D) {
        if (mList == null) {
            mList = ArrayList()
        }
        mList!!.add(0, item)
        notifyItemInserted(0)
    }

    open fun getItem(index: Int): D? {
        return if (mList != null && index < mList!!.size) mList!![index] else null
    }

    fun clear() {
        if (mList != null) {
            mList!!.clear()
            notifyItemRangeRemoved(0, mList!!.size)
        }
    }

    fun removeItem(item: D) {
        if (mList != null && !mList!!.isEmpty() && mList!!.contains(item)) {
            mList!!.remove(item)
            notifyDataSetChanged()
        }
    }

    fun updateItem(item: D) {
        if (mList != null && !mList!!.isEmpty() && mList!!.contains(item)) {
            val position = mList!!.indexOf(item)
            mList!![position] = item
            notifyDataSetChanged()
        }
    }
}