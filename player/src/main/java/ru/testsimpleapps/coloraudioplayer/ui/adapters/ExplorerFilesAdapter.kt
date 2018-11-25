package ru.testsimpleapps.coloraudioplayer.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.explorer_file.view.*
import kotlinx.android.synthetic.main.explorer_header_files.view.*
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ItemData
import ru.testsimpleapps.coloraudioplayer.managers.tools.TimeTool


class ExplorerFilesAdapter(private val mContext: Context) : BaseListAdapter<ItemData>() {

    companion object {
        val TAG_CHECK = 10 shl 24
    }

    private var mTotalTime = -1L

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder? {
        when (i) {
            BaseAdapter.TYPE_HEADER -> {
                val viewItem = LayoutInflater.from(viewGroup.context).inflate(R.layout.explorer_header_files, viewGroup, false)
                return ViewHolderHeader(viewItem)
            }

            BaseAdapter.TYPE_ITEM -> {
                val viewItem = LayoutInflater.from(viewGroup.context).inflate(R.layout.explorer_file, viewGroup, false)
                return ViewHolderItem(viewItem)
            }

            else -> return null
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (viewHolder is ViewHolderItem) {
            val itemData = getItem(i)
            viewHolder.itemView.explorer_file_image!!.setTag(TAG_CHECK, i)
            viewHolder.itemView.explorer_file_duration!!.text = TimeTool.getDuration(itemData!!.duration)
            viewHolder.itemView.explorer_file_name!!.text = itemData.name.toString()
            viewHolder.itemView.explorer_file_check!!.isChecked = itemData.isChecked
        } else if (viewHolder is ViewHolderHeader) {
            viewHolder.itemView.explorer_header_files_count_folders_label!!.text = (itemCount - 1).toString()
            viewHolder.itemView.explorer_header_files_total_time_label!!.text = TimeTool.getDuration(mTotalTime)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItem(index: Int): ItemData? {
        return super.getItem(index - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            BaseAdapter.TYPE_HEADER
        } else BaseAdapter.TYPE_ITEM
    }

    override fun setItems(list: List<ItemData>?) {
        super.setItems(list)
        mTotalTime = countTotalTime()
    }

    /*
    * Count total time for current folder/album/artist only one time
    * */
    private fun countTotalTime(): Long {
        var totalTime: Long = 0
        if (mList != null) {
            for (item in mList!!) {
                totalTime += item.duration
            }
        }

        return totalTime
    }

    inner class ViewHolderItem(view: View) : RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, layoutPosition)
                }
            }
        }
    }

    protected inner class ViewHolderHeader(view: View) : RecyclerView.ViewHolder(view)

}
