package ru.testsimpleapps.coloraudioplayer.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnEditorAction
import butterknife.OnFocusChange
import butterknife.OnLongClick
import butterknife.Unbinder
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool
import ru.testsimpleapps.coloraudioplayer.managers.tools.TextTool
import ru.testsimpleapps.coloraudioplayer.service.PlayerService
import ru.testsimpleapps.coloraudioplayer.ui.adapters.BaseAdapter
import ru.testsimpleapps.coloraudioplayer.ui.adapters.PlaylistAdapter
import ru.testsimpleapps.coloraudioplayer.ui.animation.OnTranslationAnimation
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.PlaylistSettingsDialog
import ru.testsimpleapps.coloraudioplayer.ui.views.RecyclerLayout


class PlaylistFragment : BaseFragment(), PlaylistSettingsDialog.OnViewEvent, BaseAdapter.OnItemClickListener {

    protected lateinit var mUnbinder: Unbinder

    @BindView(R.id.playlist_search_button)
    lateinit var mSearchTrackButton: ImageButton
    @BindView(R.id.playlist_search_edit)
    lateinit var mSearchTrackEditText: EditText
    @BindView(R.id.playlist_settings_button)
    lateinit var mSettingsButton: ImageButton
    @BindView(R.id.playlist_list)
    lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.playlist_additional_layout)
    lateinit var mAdditionalPanel: ConstraintLayout

    private var mPlaylistDialog: PlaylistSettingsDialog? = null
    private var mPlaylistAdapter: PlaylistAdapter? = null
    private var mRecyclerLayout: RecyclerLayout? = null
    private var mTranslationAnimation: OnTranslationAnimation? = null
    private var mInputMethodManager: InputMethodManager? = null

    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                val action = intent.action
                if (action != null) {

                    // Change playlist
                    if (action == PlayerService.RECEIVER_PLAYLIST_CHANGE) {
                        setPlaylist()
                    }

                    // Update current selection
                    if (action == PlayerService.RECEIVER_PLAYLIST_POSITION) {
                        if (intent.hasExtra(PlayerService.EXTRA_PLAYLIST_POSITION)) {
                            val position = intent.getLongExtra(PlayerService.EXTRA_PLAYLIST_POSITION, 0L).toInt()
                            mRecyclerLayout!!.scrollToPositionWithOffsetCenter(position)
                            mRecyclerView!!.smoothScrollBy(0, 1)
                            mPlaylistAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    private val intentFilter: IntentFilter
        get() {
            val intentFilter = IntentFilter()
            intentFilter.addAction(PlayerService.RECEIVER_PLAYLIST_CHANGE)
            intentFilter.addAction(PlayerService.RECEIVER_PLAYLIST_CHANGE)
            intentFilter.addAction(PlayerService.RECEIVER_PLAYLIST_POSITION)
            return intentFilter
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)
        mUnbinder = ButterKnife.bind(this, view)
        init(savedInstanceState)
        LocalBroadcastManager.getInstance(context!!).registerReceiver(mMessageReceiver, intentFilter)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TAG_ADD_PANEL, mAdditionalPanel!!.visibility)
    }

    override fun onDestroyView() {
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(mMessageReceiver)
        super.onDestroyView()
        mSearchTrackEditText!!.onFocusChangeListener = null
        mUnbinder.unbind()
    }

    @OnClick(R.id.playlist_search_button)
    protected fun onSearchClickButton() {
        if (mSearchTrackEditText!!.visibility == View.INVISIBLE) {
            mSearchTrackEditText!!.visibility = View.VISIBLE
            mSearchTrackEditText!!.requestFocus()
            mInputMethodManager!!.showSoftInput(mSearchTrackEditText, InputMethodManager.SHOW_IMPLICIT)
        } else {
            val text = mSearchTrackEditText!!.text.toString()
            if (text == "") {
                mSearchTrackEditText!!.visibility = View.INVISIBLE
                mSearchTrackEditText!!.clearFocus()
                mInputMethodManager!!.hideSoftInputFromWindow(mSearchTrackEditText!!.windowToken, InputMethodManager.RESULT_UNCHANGED_HIDDEN)
                updateRecyclePosition(CursorFactory.instance.position().toInt() + 1)
            } else {

                // Go to position
                if (TextTool.isNumeric(text)) {
                    val position = Integer.valueOf(text)
                    if (position > 0 && position <= mPlaylistAdapter!!.itemCount) {
                        updateRecyclePosition(position + 1)
                        mPlaylistAdapter!!.setSearchedPosition(position.toLong())
                        mPlaylistAdapter!!.notifyDataSetChanged()
                        showToast(R.string.playlist_search_position)
                        return
                    }
                }

                //  Go to text match
                val position = mPlaylistAdapter!!.searchMatch(text)
                if (position > IPlaylist.ERROR_CODE) {
                    updateRecyclePosition(position + 1)
                    mPlaylistAdapter!!.setSearchedPosition((position + 1).toLong())
                    mPlaylistAdapter!!.notifyDataSetChanged()
                    showToast(R.string.playlist_search_text)
                    return
                }
            }

            mPlaylistAdapter!!.setSearchedPosition(IPlaylist.ERROR_CODE)
            showToast(R.string.playlist_search_no_match)
        }
    }

    @OnLongClick(R.id.playlist_search_button)
    protected fun onSearchLongClickButton(): Boolean {
        val position = CursorFactory.instance.position()
        updateRecyclePosition(position.toInt() + 1)
        return true
    }

    @OnFocusChange(R.id.playlist_search_edit)
    protected fun onFocusChangeEdit(view: View, hasFocus: Boolean) {
        if (!hasFocus) {
            mSearchTrackEditText!!.setText("")
            mSearchTrackEditText!!.visibility = View.INVISIBLE
            mInputMethodManager!!.hideSoftInputFromWindow(mSearchTrackEditText!!.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
            mPlaylistAdapter!!.setSearchedPosition(IPlaylist.ERROR_CODE)
            mPlaylistAdapter!!.notifyDataSetChanged()
        }
    }

    @OnEditorAction(R.id.playlist_search_edit)
    protected fun onEditorActionEdit(view: TextView, actionId: Int, event: KeyEvent): Boolean {
        if (actionId == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
            onSearchClickButton()
            return true
        }

        return false
    }

    @OnClick(R.id.playlist_settings_button)
    protected fun onSettingsClickButton() {
        mPlaylistDialog!!.show()
    }

    override fun onSort(value: String) {
        CursorFactory.newInstance()
        setPlaylist()
    }

    override fun onSortOrder(value: String) {
        CursorFactory.newInstance()
        setPlaylist()
    }

    override fun onView(isValue: Boolean) {
        mPlaylistAdapter!!.setExpand(isValue)
        updateRecyclePosition(CursorFactory.instance.position().toInt())
        mRecyclerView!!.scheduleLayoutAnimation()
    }

    override fun onItemClick(view: View, position: Int) {
        PlayerService.sendCommandTrackSelect(mPlaylistAdapter!!.getItemId(position))
    }

    private fun init(savedInstanceState: Bundle?) {
        mInputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val animation = AnimationUtils.loadLayoutAnimation(context, R.anim.recycleview_layout_animation)
        mTranslationAnimation = OnTranslationAnimation(mAdditionalPanel!!, OnTranslationAnimation.DEFAULT_DURATION)

        mRecyclerLayout = RecyclerLayout(context!!)
        mRecyclerLayout!!.center = RECYCLE_CENTER
        mRecyclerLayout!!.shrinkAmount = RECYCLE_SHRINK_AMOUNT
        mRecyclerLayout!!.shrinkDistance = RECYCLE_SHRINK_CENTER
        mRecyclerLayout!!.setDynamicCenter(true)

        mRecyclerView!!.layoutAnimation = animation
        mRecyclerView!!.layoutManager = mRecyclerLayout
        mRecyclerView!!.addOnScrollListener(OnScrollRecycleViewListener())
        mPlaylistAdapter = PlaylistAdapter(context!!)
        mPlaylistAdapter!!.setOnItemClickListener(this)
        mPlaylistAdapter!!.setExpand(PreferenceTool.instance.playlistViewExpand)
        mRecyclerView!!.adapter = mPlaylistAdapter

        mPlaylistDialog = PlaylistSettingsDialog(context!!)
        mPlaylistDialog!!.setOnViewEvent(this)
        mSearchTrackEditText!!.visibility = View.INVISIBLE

        restoreStates(savedInstanceState)
        setPlaylist()
    }

    private fun updateRecyclePosition(position: Int) {
        mRecyclerLayout!!.scrollToPositionWithOffsetCenter(position)
        mRecyclerView!!.smoothScrollBy(0, 1)
    }

    private fun restoreStates(savedInstanceState: Bundle?) {
        // Check image_previous states
        if (savedInstanceState != null) {
            // Panel state
            mAdditionalPanel!!.visibility = savedInstanceState.getInt(TAG_ADD_PANEL)
        } else {
            mRecyclerLayout!!.scrollToPositionWithOffsetCenter(CursorFactory.instance.position().toInt())
        }
    }

    private fun setPlaylist() {
        mPlaylistAdapter!!.setPlaylist(CursorFactory.copyInstance!!)
        mRecyclerView!!.smoothScrollBy(0, 1)
        mRecyclerView!!.scheduleLayoutAnimation()
    }

    private inner class OnScrollRecycleViewListener : RecyclerView.OnScrollListener() {

        private var mState = -1

        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            mState = newState
        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (mState == RecyclerView.SCROLL_STATE_DRAGGING && !mTranslationAnimation!!.isAnimating) {
                mTranslationAnimation!!.animate(dy < 0)
            }
        }
    }

    companion object {

        val TAG = PlaylistFragment::class.java.simpleName
        private val TAG_ADD_PANEL = "TAG_ADD_PANEL"
        private val RECYCLE_CENTER = 1.0f
        private val RECYCLE_SHRINK_AMOUNT = 0.1f
        private val RECYCLE_SHRINK_CENTER = 1.0f

        fun newInstance(): PlaylistFragment {
            return PlaylistFragment()
        }
    }

}
