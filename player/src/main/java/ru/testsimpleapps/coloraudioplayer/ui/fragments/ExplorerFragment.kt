package ru.testsimpleapps.coloraudioplayer.ui.fragments

import android.os.Bundle
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar

import java.util.ArrayList
import java.util.Collections

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnLongClick
import butterknife.Unbinder
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ConfigData
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ContainerData
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ItemData
import ru.testsimpleapps.coloraudioplayer.managers.explorer.FoldersComparator
import ru.testsimpleapps.coloraudioplayer.managers.explorer.ItemsComparator
import ru.testsimpleapps.coloraudioplayer.managers.explorer.MediaExplorerManager
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool
import ru.testsimpleapps.coloraudioplayer.service.PlayerService
import ru.testsimpleapps.coloraudioplayer.ui.adapters.BaseAdapter
import ru.testsimpleapps.coloraudioplayer.ui.adapters.BaseListAdapter
import ru.testsimpleapps.coloraudioplayer.ui.adapters.ExplorerFilesAdapter
import ru.testsimpleapps.coloraudioplayer.ui.adapters.ExplorerFolderAdapter
import ru.testsimpleapps.coloraudioplayer.ui.animation.OnTranslationAnimation
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.ExplorerSettingsDialog


class ExplorerFragment : BaseFragment(), BaseAdapter.OnItemClickListener, MediaExplorerManager.OnDataReady,
        BaseAdapter.OnItemCheckListener, ExplorerSettingsDialog.OnViewEvent {

    protected lateinit var mUnbinder: Unbinder

    @BindView(R.id.explorer_list)
    lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.explorer_progress)
    lateinit var mProgressBar: ProgressBar

    @BindView(R.id.explorer_additional_layout)
    lateinit var mAdditionalPanel: FrameLayout
    @BindView(R.id.explorer_additional_files_layout)
    lateinit var mAdditionalFilesPanel: ConstraintLayout
    @BindView(R.id.explorer_additional_folders_layout)
    lateinit var mAdditionalFoldersPanel: ConstraintLayout

    /*
    * Buttons for files instance
    * */
    @BindView(R.id.explorer_files_back)
    lateinit var mBackFilesButton: ImageButton
    @BindView(R.id.explorer_files_add)
    lateinit var mAddFilesButton: ImageButton

    /*
    * Buttons for folders instance
    * */
    @BindView(R.id.explorer_folders_settings)
    lateinit var mSettingsFilesButton: ImageButton
    @BindView(R.id.explorer_folders_add)
    lateinit var mAddFoldersButton: ImageButton

    private var mExplorerFilesAdapter: ExplorerFilesAdapter? = null
    private var mExplorerFolderAdapter: ExplorerFolderAdapter? = null
    private var mFolderStateAdapter: Parcelable? = null
    private var mFolderPosition = 1
    private var mExplorerDialog: ExplorerSettingsDialog? = null
    private var mOnTranslationAnimation: OnTranslationAnimation? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_explorer, container, false)
        mUnbinder = ButterKnife.bind(this, view)
        init(savedInstanceState)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MediaExplorerManager.instance.removeFindCallback()
        mUnbinder.unbind()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TAG_ADD_PANEL, mAdditionalPanel!!.visibility)
        outState.putInt(TAG_FOLDER_POSITION, mFolderPosition)
        outState.putBoolean(TAG_TYPE_ADAPTER, mRecyclerView!!.adapter is ExplorerFolderAdapter)
        outState.putBoolean(TAG_DIALOG, mExplorerDialog!!.isShowing)

        outState.putParcelable(TAG_FOLDER_STATE_ADAPTER, mFolderStateAdapter)
        outState.putParcelable(TAG_PREVIOUS_STATE_ADAPTER, mRecyclerView!!.layoutManager.onSaveInstanceState())
        outState.putSerializable(TAG_FOLDER_CONTENT, mExplorerFolderAdapter!!.itemList as ArrayList<FolderData>)
    }

    override fun onItemClick(view: View, position: Int) {
        if (mRecyclerView!!.adapter is ExplorerFolderAdapter) {
            typePanelVisibility(false)
            mFolderPosition = position
            mFolderStateAdapter = mRecyclerView!!.layoutManager.onSaveInstanceState()
            val folderData = mExplorerFolderAdapter!!.getItem(position)
            val itemData = folderData!!.containerItemData
            mExplorerFilesAdapter!!.setItems(sortFiles(PreferenceTool.instance.explorerSortType,
                    PreferenceTool.instance.explorerSortOrder, itemData.list))
            mRecyclerView!!.adapter = mExplorerFilesAdapter
            mRecyclerView!!.scheduleLayoutAnimation()
        } else if (mRecyclerView!!.adapter is ExplorerFilesAdapter) {
            val itemData = mExplorerFilesAdapter!!.getItem(position)
            PlayerService.sendCommandPlayTrack(itemData!!.path!!)
        }
    }

    override fun onItemCheck(view: View, position: Int) {
        mOnTranslationAnimation!!.animate(false)
    }

    @OnClick(R.id.explorer_files_back)
    protected fun backButtonClick() {
        restoreAdapter()
    }

    @OnClick(R.id.explorer_folders_add)
    protected fun addFoldersButtonClick() {
        val items = ArrayList<Long>()
        for (folder in mExplorerFolderAdapter!!.itemList!!) {
            if (folder.isChecked) {
                for (file in folder.containerItemData.list) {
                    items.add(file.id)
                    file.isChecked = false
                }
            }


            folder.isChecked = false
        }

        mRecyclerView!!.adapter.notifyDataSetChanged()
        val isAdded = CursorFactory.instance.add(items)
        if (items.isEmpty() || !isAdded) {
            showToast(R.string.explorer_add_to_playlist_nothing)
        } else {
            showToast(getString(R.string.explorer_add_to_playlist) + items.size)
        }
    }

    @OnClick(R.id.explorer_files_add)
    protected fun addFilesButtonClick() {
        val items = ArrayList<Long>()
        for (file in mExplorerFilesAdapter!!.itemList!!) {
            if (file.isChecked) {
                items.add(file.id)
                file.isChecked = false
            }
        }

        mRecyclerView!!.adapter.notifyDataSetChanged()
        val isAdded = CursorFactory.instance.add(items)
        if (items.isEmpty() || !isAdded) {
            showToast(R.string.explorer_add_to_playlist_nothing)
        } else {
            showToast(getString(R.string.explorer_add_to_playlist) + items.size)
        }
    }

    @OnClick(R.id.explorer_folders_settings)
    protected fun settingsFoldersButtonClick() {
        mExplorerDialog!!.show()
    }

    @OnLongClick(R.id.explorer_files_add, R.id.explorer_folders_add)
    protected fun onLongAddClick(): Boolean {
        if (mRecyclerView!!.adapter is ExplorerFolderAdapter) {
            for (item in mExplorerFolderAdapter!!.itemList!!) {
                item.isChecked = true
            }
        } else if (mRecyclerView!!.adapter is ExplorerFilesAdapter) {
            for (item in mExplorerFilesAdapter!!.itemList!!) {
                item.isChecked = true
            }
        }

        mRecyclerView!!.adapter.notifyDataSetChanged()
        return true
    }

    override fun onBackPressed(): Boolean {
        return restoreAdapter()
    }

    override fun onSuccess() {
        showToast(R.string.explorer_find_media_ok)
        mProgressBar!!.visibility = View.INVISIBLE
        val folderDataList = groupFolders(PreferenceTool.instance.explorerGroupType)
        sortFolders(PreferenceTool.instance.explorerSortType,
                PreferenceTool.instance.explorerSortOrder, folderDataList)
        mExplorerFolderAdapter!!.setItems(folderDataList)
        mRecyclerView!!.adapter = mExplorerFolderAdapter
        mRecyclerView!!.scheduleLayoutAnimation()
    }

    override fun onError() {
        showToast(R.string.explorer_find_media_error)
        mProgressBar!!.visibility = View.INVISIBLE
    }

    override fun onGroup(value: Int) {
        mExplorerFolderAdapter!!.setItems(groupFolders(value))
        mRecyclerView!!.scheduleLayoutAnimation()
    }

    override fun onSort(value: Int) {
        sortFolders(value, PreferenceTool.instance.explorerSortOrder, mExplorerFolderAdapter!!.itemList)
        mExplorerFolderAdapter!!.notifyDataSetChanged()
        mRecyclerView!!.scheduleLayoutAnimation()
    }

    override fun onSortOrder(value: Int) {
        sortFolders(PreferenceTool.instance.explorerSortType, value, mExplorerFolderAdapter!!.itemList)
        mExplorerFolderAdapter!!.notifyDataSetChanged()
        mRecyclerView!!.scheduleLayoutAnimation()
    }

    private fun groupFolders(value: Int): List<FolderData> {
        when (value) {
            ConfigData.GROUP_TYPE_ALBUMS -> return MediaExplorerManager.instance.albums
            ConfigData.GROUP_TYPE_FOLDERS -> return MediaExplorerManager.instance.folders
            ConfigData.GROUP_TYPE_ARTISTS -> return MediaExplorerManager.instance.artists
            else -> return MediaExplorerManager.instance.folders
        }
    }

    private fun sortFolders(sortType: Int, sortOrder: Int, list: List<FolderData>?): List<FolderData> {
        when (sortType) {
            ConfigData.SORT_TYPE_NAME -> Collections.sort(list!!, FoldersComparator.Name(sortOrder))
            ConfigData.SORT_TYPE_VALUE -> Collections.sort(list!!, FoldersComparator.Size(sortOrder))
        }

        return list!!
    }

    private fun sortFiles(sortType: Int, sortOrder: Int, list: List<ItemData>): List<ItemData> {
        when (sortType) {
            ConfigData.SORT_TYPE_NAME -> Collections.sort(list, ItemsComparator.Name(sortOrder))
            ConfigData.SORT_TYPE_VALUE -> Collections.sort(list, ItemsComparator.Duration(sortOrder))
            ConfigData.SORT_TYPE_DATE -> Collections.sort(list, ItemsComparator.DataAdded(sortOrder))
        }

        return list
    }

    private fun init(savedInstanceState: Bundle?) {
        MediaExplorerManager.instance.setFindCallback(this)

        typePanelVisibility(true)
        mOnTranslationAnimation = OnTranslationAnimation(mAdditionalPanel, OnTranslationAnimation.DEFAULT_DURATION)

        mExplorerDialog = ExplorerSettingsDialog(context!!)
        mExplorerDialog!!.setOnViewEvent(this)
        mExplorerFilesAdapter = ExplorerFilesAdapter(context!!)
        mExplorerFilesAdapter!!.setOnItemClickListener(this)
        mExplorerFilesAdapter!!.setOnItemCheckListener(this)
        mExplorerFolderAdapter = ExplorerFolderAdapter(context!!)
        mExplorerFolderAdapter!!.setOnItemClickListener(this)
        mExplorerFolderAdapter!!.setOnItemCheckListener(this)

        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.recycleview_layout_animation)
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)
        mRecyclerView!!.layoutAnimation = animation
        mRecyclerView!!.addOnScrollListener(OnScrollRecycleViewListener())

        restoreStates(savedInstanceState)
    }

    private fun restoreStates(savedInstanceState: Bundle?) {
        // Check image_previous states
        if (savedInstanceState == null) {
            MediaExplorerManager.instance.findMediaAsync()
        } else {
            // State for adapter image_back press
            mFolderStateAdapter = savedInstanceState.getParcelable(TAG_FOLDER_STATE_ADAPTER)
            // Panel state
            mAdditionalPanel!!.visibility = savedInstanceState.getInt(TAG_ADD_PANEL)
            // Restore image_folder adapter every time
            val folderDataList = savedInstanceState.getSerializable(TAG_FOLDER_CONTENT) as ArrayList<FolderData>
            if (folderDataList != null) {
                mExplorerFolderAdapter!!.setItems(folderDataList)
            }
            // Restore files adapter every time
            mFolderPosition = savedInstanceState.getInt(TAG_FOLDER_POSITION)
            val folderData = mExplorerFolderAdapter!!.getItem(mFolderPosition)
            if (folderData != null) {
                mExplorerFilesAdapter!!.setItems(folderData.containerItemData.list)
            }

            // Previous adapter
            if (savedInstanceState.getBoolean(TAG_TYPE_ADAPTER)) {
                mRecyclerView!!.adapter = mExplorerFolderAdapter
                typePanelVisibility(true)
            } else {
                mRecyclerView!!.adapter = mExplorerFilesAdapter
                typePanelVisibility(false)
            }

            // Previous adapter state
            mRecyclerView!!.layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(TAG_PREVIOUS_STATE_ADAPTER))
            mRecyclerView!!.scheduleLayoutAnimation()

            // Check settings dialog
            if (savedInstanceState.getBoolean(TAG_DIALOG)) {
                mExplorerDialog!!.show()
            }
        }

        // Show progress for async data image_search
        if (MediaExplorerManager.instance.isProcessing) {
            mProgressBar!!.visibility = View.VISIBLE
        } else {
            mProgressBar!!.visibility = View.INVISIBLE
        }
    }

    private fun typePanelVisibility(isFolder: Boolean) {
        if (isFolder) {
            mAdditionalFoldersPanel!!.visibility = View.VISIBLE
            mAdditionalFilesPanel!!.visibility = View.GONE
        } else {
            mAdditionalFoldersPanel!!.visibility = View.GONE
            mAdditionalFilesPanel!!.visibility = View.VISIBLE
        }
    }

    private fun restoreAdapter(): Boolean {
        if (mRecyclerView!!.adapter is ExplorerFilesAdapter) {
            typePanelVisibility(true)
            mRecyclerView!!.adapter = mExplorerFolderAdapter
            mRecyclerView!!.layoutManager.onRestoreInstanceState(mFolderStateAdapter)
            mRecyclerView!!.scheduleLayoutAnimation()
            return true
        }

        return false
    }

    private inner class OnScrollRecycleViewListener : RecyclerView.OnScrollListener() {

        private var mState = -1

        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            mState = newState
        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (mState == RecyclerView.SCROLL_STATE_DRAGGING && !mOnTranslationAnimation!!.isAnimating) {
                mOnTranslationAnimation!!.animate(dy < 0)
            }
        }
    }

    companion object {

        val TAG = ExplorerFragment::class.java.simpleName
        private val TAG_ADD_PANEL = "TAG_ADD_PANEL"
        private val TAG_DIALOG = "TAG_DIALOG"
        private val TAG_TYPE_ADAPTER = "TAG_TYPE_ADAPTER"
        private val TAG_FOLDER_STATE_ADAPTER = "TAG_FOLDER_STATE_ADAPTER"
        private val TAG_FOLDER_POSITION = "TAG_FOLDER_POSITION"
        private val TAG_FOLDER_CONTENT = "TAG_FOLDER_CONTENT"
        private val TAG_PREVIOUS_STATE_ADAPTER = "TAG_PREVIOUS_STATE_ADAPTER"


        fun newInstance(): ExplorerFragment {
            return ExplorerFragment()
        }
    }

}
