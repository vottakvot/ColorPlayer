package ru.testsimpleapps.coloraudioplayer.ui.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory
import ru.testsimpleapps.coloraudioplayer.managers.tools.CursorTool
import ru.testsimpleapps.coloraudioplayer.managers.tools.TimeTool


class PlaylistAdapter(private val mContext: Context) : BaseAdapter() {

    private var mIPlaylist: IPlaylist? = null
    private var mIsExpand = true
    private var mPreviousSearchPosition: Long = 0
    private var mSearchedPosition = IPlaylist.ERROR_CODE
    private val mViewPadding: Int

    private val totalTime: Long
        get() = if (mIPlaylist != null) mIPlaylist!!.totalTime else 0

    private val totalTracks: Long
        get() = if (mIPlaylist != null) mIPlaylist!!.size() else 0

    init {
        mViewPadding = mContext.resources.getDimension(R.dimen.playlist_item_selection_padding).toInt()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder? {
        when (i) {
            BaseAdapter.TYPE_HEADER -> {
                val viewItem = LayoutInflater.from(viewGroup.context).inflate(R.layout.playlist_header, viewGroup, false)
                return ViewHolderHeader(viewItem)
            }

            BaseAdapter.TYPE_ITEM -> {
                val viewItem = LayoutInflater.from(viewGroup.context).inflate(R.layout.playlist_item_layout, viewGroup, false)
                return ViewHolderItem(viewItem)
            }

            else -> return null
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (mIPlaylist != null) {
            if (viewHolder is ViewHolderItem) {
                mIPlaylist!!.goToPosition((i - 1).toLong())

                // Main
                viewHolder.mImageTrack!!.setImageResource(R.drawable.image_item_track)
                viewHolder.mCountTrack!!.text = i.toString()
                viewHolder.mNameTrack!!.text = mIPlaylist!!.trackName.toString()
                viewHolder.mNameTrack!!.setTypeface(viewHolder.mNameTypeface, Typeface.NORMAL)

                // Info
                if (mIsExpand) {
                    viewHolder.mInfoLayout!!.visibility = View.VISIBLE
                    viewHolder.mDurationTrack!!.text = TimeTool.getDuration(mIPlaylist!!.trackDuration)
                    viewHolder.mArtistsTrack!!.text = mIPlaylist!!.trackArtist.toString()
                    viewHolder.mAlbumTrack!!.text = mIPlaylist!!.trackAlbum.toString()
                    viewHolder.mDateTrack!!.text = TimeTool.getDateTime(mIPlaylist!!.trackDateModified)
                } else {
                    viewHolder.mInfoLayout!!.visibility = View.GONE
                }

                // Background for current position or image_search
                if (mIPlaylist!!.trackId == CursorFactory.instance.trackId) {
                    viewHolder.itemView.setBackgroundResource(R.drawable.drawable_recycleview_item_selection)
                    viewHolder.itemView.setPadding(mViewPadding, mViewPadding, mViewPadding, mViewPadding)
                    viewHolder.mNameTrack!!.setTypeface(viewHolder.mNameTrack!!.typeface, Typeface.BOLD)
                } else if (mSearchedPosition == i.toLong()) {
                    viewHolder.itemView.setBackgroundResource(R.drawable.drawable_recycleview_item_find)
                    viewHolder.itemView.setPadding(mViewPadding, mViewPadding, mViewPadding, mViewPadding)
                } else {
                    viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
                    viewHolder.itemView.setPadding(0, 0, 0, 0)
                }

            } else if (viewHolder is ViewHolderHeader) {
                viewHolder.mNamePlaylist!!.text = CursorTool.getPlaylistNameById(mContext.contentResolver,
                        mIPlaylist!!.playlistId)
                viewHolder.mCountTracks!!.text = totalTracks.toString()
                viewHolder.mTotalTimeTracks!!.text = TimeTool.getDuration(totalTime)
            }
        }
    }

    override fun getItemCount(): Int {
        return (if (mIPlaylist != null) mIPlaylist!!.size().toInt() else 0) + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            BaseAdapter.TYPE_HEADER
        } else BaseAdapter.TYPE_ITEM
    }

    override fun getItemId(position: Int): Long {
        return if (mIPlaylist != null && mIPlaylist!!.goToPosition(position.toLong())) {
            mIPlaylist!!.trackId
        } else IPlaylist.ERROR_CODE

    }

    fun setPlaylist(iPlaylist: IPlaylist) {
        mIPlaylist = iPlaylist
        notifyDataSetChanged()
    }

    fun setExpand(isExpand: Boolean) {
        mIsExpand = isExpand
        notifyDataSetChanged()
    }

    fun searchMatch(text: String): Int {
        if (mIPlaylist != null) {
            val position = mIPlaylist!!.find(mPreviousSearchPosition, text)
            mPreviousSearchPosition = position + 1
            return position.toInt()
        }

        return IPlaylist.ERROR_CODE.toInt()
    }

    fun setSearchedPosition(position: Long) {
        mSearchedPosition = position
    }

    inner class ViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.playlist_header_name_value)
        lateinit var mNamePlaylist: TextView
        @BindView(R.id.playlist_header_count_value)
        lateinit var mCountTracks: TextView
        @BindView(R.id.playlist_header_total_time_value)
        lateinit var mTotalTimeTracks: TextView

        init {
            ButterKnife.bind(this, view)
        }
    }

    inner class ViewHolderItem(view: View) : RecyclerView.ViewHolder(view) {

        /*
        * Main views
        * */
        @BindView(R.id.playlist_track_image)
        lateinit var mImageTrack: ImageView
        @BindView(R.id.playlist_track_count)
        lateinit var mCountTrack: TextView
        @BindView(R.id.playlist_track_name)
        lateinit var mNameTrack: TextView

        /*
        * Optional
        * */
        @BindView(R.id.playlist_track_item_info_layout)
        lateinit var mInfoLayout: ConstraintLayout
        @BindView(R.id.playlist_track_duration_value)
        lateinit var mDurationTrack: TextView
        @BindView(R.id.playlist_track_artist_value)
        lateinit var mArtistsTrack: TextView
        @BindView(R.id.playlist_track_albums_value)
        lateinit var mAlbumTrack: TextView
        @BindView(R.id.playlist_track_date_value)
        lateinit var mDateTrack: TextView

        /*
        * Typeface
        * */
        internal var mNameTypeface: Typeface

        init {
            ButterKnife.bind(this, view)
            view.setOnClickListener {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, layoutPosition - 1)
                }
            }

            mNameTypeface = mNameTrack!!.typeface
        }
    }

}
