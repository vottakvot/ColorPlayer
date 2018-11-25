package ru.testsimpleapps.coloraudioplayer.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.drawer_item.view.*
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.data.DrawerItem


class DrawerAdapter(private val mContext: Context) : BaseListAdapter<DrawerItem>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder? {
        when (i) {
            BaseAdapter.TYPE_ITEM -> {
                val viewItem = LayoutInflater.from(viewGroup.context).inflate(R.layout.drawer_item, viewGroup, false)
                return ViewHolderItem(viewItem)
            }

            else -> return null
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (viewHolder is ViewHolderItem) {
            val drawerItem = getItem(i)
            viewHolder.itemView.drawerImageView!!.setImageResource(drawerItem!!.image)
            viewHolder.itemView.drawerTextView!!.text = drawerItem.name
        }
    }

    override fun getItemViewType(position: Int): Int {
        return BaseAdapter.TYPE_ITEM
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

}
