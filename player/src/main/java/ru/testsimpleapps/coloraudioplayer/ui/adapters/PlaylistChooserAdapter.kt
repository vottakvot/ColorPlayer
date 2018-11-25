package ru.testsimpleapps.coloraudioplayer.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.managers.tools.CollectionTool


class PlaylistChooserAdapter(private val mContext: Context) : BaseAdapter() {
    private var mData: Map<Long, String>? = null

    fun setData(data: Map<Long, String>) {
        mData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewItem = LayoutInflater.from(mContext).inflate(R.layout.playlist_choose_item, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        if (mData != null) {
            val entry = CollectionTool.getMapEntryByPosition(mData, position)
            viewHolder.mNamePlaylist!!.text = entry!!.value
        }
    }

    fun getItem(position: Int): kotlin.collections.Map.Entry<Long, String>? {
        return if (mData != null) CollectionTool.getMapEntryByPosition(mData, position + 1) else null
    }

    override fun getItemCount(): Int {
        return if (mData != null) mData!!.size else 0
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @BindView(R.id.playlist_choose_item_name)
        lateinit var mNamePlaylist: TextView

        init {
            ButterKnife.bind(this, view)
            view.setOnClickListener {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, layoutPosition - 1)
                }
            }

            view.setOnLongClickListener(View.OnLongClickListener { view ->
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemLongClick(view, layoutPosition - 1)
                    return@OnLongClickListener true
                }

                false
            })
        }
    }

}