package ru.testsimpleapps.coloraudioplayer.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import java.util.TreeMap

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory
import ru.testsimpleapps.coloraudioplayer.managers.tools.CursorTool
import ru.testsimpleapps.coloraudioplayer.ui.adapters.BaseAdapter
import ru.testsimpleapps.coloraudioplayer.ui.adapters.PlaylistChooserAdapter

class PlaylistChooserDialog(private val mContext: Context) : BaseDialog(mContext), BaseAdapter.OnItemClickListener, BaseAdapter.OnItemLongClickListener {

    @BindView(R.id.playlist_chooser_list)
    lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.playlist_chooser_cancel)
    lateinit var mCancelButton: Button

    private var mPlaylistChooserAdapter: PlaylistChooserAdapter? = null

    override fun show() {
        super.show()
        if (mPlaylistChooserAdapter != null) {
            mPlaylistChooserAdapter!!.setData(CursorTool.getPlaylist(mContext.contentResolver)!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        init()
    }

    @OnClick(R.id.playlist_chooser_cancel)
    protected fun onCancelClick() {
        dismiss()
    }

    private fun init() {
        setContentView(R.layout.dialog_playlist_chooser)
        ButterKnife.bind(this)
        mPlaylistChooserAdapter = PlaylistChooserAdapter(mContext)
        mPlaylistChooserAdapter!!.setOnItemClickListener(this)
        mPlaylistChooserAdapter!!.setOnItemLongClickListener(this)
        mRecyclerView!!.adapter = mPlaylistChooserAdapter
        mRecyclerView!!.layoutManager = LinearLayoutManager(mContext)
    }

    override fun onItemClick(view: View, position: Int) {
        val item = mPlaylistChooserAdapter!!.getItem(position)
        if (item != null && item.key != IPlaylist.ERROR_CODE) {
            PlayerConfig.instance.playlistId = item.key
            CursorFactory.newInstance()
            dismiss()
            return
        }
    }

    override fun onItemLongClick(view: View, position: Int) {
        val item = mPlaylistChooserAdapter!!.getItem(position)
        if (item != null) {
            CursorTool.deletePlaylist(mContext.contentResolver, item.key)
            mPlaylistChooserAdapter!!.setData(CursorTool.getPlaylist(mContext.contentResolver)!!)
        }
    }
}
