package ru.testsimpleapps.coloraudioplayer.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.data.DrawerItem
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig
import ru.testsimpleapps.coloraudioplayer.service.PlayerService
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity
import ru.testsimpleapps.coloraudioplayer.ui.adapters.BaseAdapter
import ru.testsimpleapps.coloraudioplayer.ui.adapters.DrawerAdapter
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.EqualizerDialog
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.PlaylistChooserDialog
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.PlaylistCreateDialog


class DrawerFragment : BaseFragment(), BaseAdapter.OnItemClickListener {

    private var mMainActivity: MainActivity? = null

    protected lateinit var mUnbinder: Unbinder

    @BindView(R.id.drawer_list)
    lateinit var mRecyclerView: RecyclerView

    private var mDrawerAdapter: DrawerAdapter? = null

    private val mDrawerImages = intArrayOf(R.drawable.image_playlist_create, R.drawable.image_playlist_choose, R.drawable.image_equalizer, R.drawable.image_timer, R.drawable.image_color, R.drawable.image_settings, R.drawable.image_exit)

    private var mPlaylistCreateDialog: PlaylistCreateDialog? = null
    private var mPlaylistChooserDialog: PlaylistChooserDialog? = null
    private var mEqualizerDialog: EqualizerDialog? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mMainActivity = context as MainActivity?
        } catch (e: ClassCastException) {
            throw ClassCastException(javaClass.simpleName + " must implement " +
                    MainActivity::class.java.simpleName)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_drawer, container, false)
        mUnbinder = ButterKnife.bind(this, view)
        init(savedInstanceState)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder.unbind()
    }

    private fun init(savedInstanceState: Bundle?) {
        val drawers = resources.getStringArray(R.array.drawer_items)
        val drawerItems = ArrayList<DrawerItem>()

        // Check sizes of images and strings arrays
        if (mDrawerImages.size != drawers.size) {
            throw RuntimeException("Size of image array must match with string array!")
        }

        // Fill adapter
        for (i in mDrawerImages.indices) {
            drawerItems.add(DrawerItem(mDrawerImages[i], drawers[i]))
        }

        mDrawerAdapter = DrawerAdapter(context!!)
        mDrawerAdapter!!.setItems(drawerItems)
        mDrawerAdapter!!.setOnItemClickListener(this)
        mRecyclerView!!.adapter = mDrawerAdapter
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)

        mPlaylistCreateDialog = PlaylistCreateDialog(context!!)
        mPlaylistChooserDialog = PlaylistChooserDialog(context!!)
        mEqualizerDialog = EqualizerDialog(context!!)
    }

    override fun onItemClick(view: View, position: Int) {
        val drawerItem = mDrawerAdapter!!.getItem(position)
        when (drawerItem!!.image) {
            R.drawable.image_playlist_create -> mPlaylistCreateDialog!!.show()
            R.drawable.image_playlist_choose -> mPlaylistChooserDialog!!.show()
            R.drawable.image_equalizer -> mEqualizerDialog!!.show(PlayerConfig.instance.audioSession)
            R.drawable.image_timer -> {
            }
            R.drawable.image_color -> {
            }
            R.drawable.image_settings -> {
            }
            R.drawable.image_exit -> {
                PlayerService.sendCommandExit()
                activity!!.finish()
            }
        }

        mMainActivity!!.closeDrawer()
    }

    companion object {

        val TAG = DrawerFragment::class.java.simpleName

        fun newInstance(): DrawerFragment {
            return DrawerFragment()
        }
    }

}
