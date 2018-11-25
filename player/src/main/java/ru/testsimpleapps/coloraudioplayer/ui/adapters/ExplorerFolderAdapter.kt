package ru.testsimpleapps.coloraudioplayer.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import kotlinx.android.synthetic.main.explorer_folder.view.*
import kotlinx.android.synthetic.main.explorer_header_folders.view.*

import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData
import ru.testsimpleapps.coloraudioplayer.managers.explorer.MediaExplorerManager
import ru.testsimpleapps.coloraudioplayer.managers.tools.TimeTool


class ExplorerFolderAdapter(private val mContext: Context) : BaseListAdapter<FolderData>() {

    companion object {
        val TAG_CHECK = 10 shl 24
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder? {
        when (i) {
            BaseAdapter.TYPE_HEADER -> {
                val viewItem = LayoutInflater.from(viewGroup.context).inflate(R.layout.explorer_header_folders, viewGroup, false)
                return ViewHolderHeader(viewItem)
            }

            BaseAdapter.TYPE_ITEM -> {
                val viewItem = LayoutInflater.from(viewGroup.context).inflate(R.layout.explorer_folder, viewGroup, false)
                return ViewHolderItem(viewItem)
            }

            else -> return null
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (viewHolder is ViewHolderItem) {
            val folderData = getItem(i)
            viewHolder.itemView.explorer_folder_check!!.isChecked = folderData!!.isChecked
            viewHolder.itemView.explorer_folder_count!!.text = folderData.size().toString()
            viewHolder.itemView.explorer_folder_name!!.text = folderData.name
        } else if (viewHolder is ViewHolderHeader) {
            viewHolder.itemView.explorer_header_folder_count_folders_value!!.text = (itemCount - 1).toString()
            viewHolder.itemView.explorer_header_folder_count_tracks_value!!.setText(MediaExplorerManager.instance.totalTracks.toString())
            viewHolder.itemView.explorer_header_folder_total_time_value!!.text = TimeTool.getDuration(MediaExplorerManager.instance.totalTime)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItem(index: Int): FolderData? {
        return super.getItem(index - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            BaseAdapter.TYPE_HEADER
        } else BaseAdapter.TYPE_ITEM
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

    protected inner class ViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {

    }
}