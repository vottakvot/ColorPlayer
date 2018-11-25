package ru.testsimpleapps.coloraudioplayer.ui.fragments

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool
import ru.testsimpleapps.coloraudioplayer.ui.behavior.BaseBottomSheetCallback


class PagerFragment : BaseFragment() {

    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var mUnbinder: Unbinder? = null

    @BindView(R.id.view_pager)
    lateinit var mViewPager: ViewPager
    @BindView(R.id.control_behavior_layout)
    lateinit var mControlLayout: LinearLayout
    @BindView(R.id.control_hide_notification)
    lateinit var mControlInfoLayout: ConstraintLayout
    @BindView(R.id.control_hide_close_button)
    lateinit var mCloseInfoButton: ImageButton

    private var mIsShowInfoPanel: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_pager, container, false)
        mUnbinder = ButterKnife.bind(this, view)
        init(savedInstanceState)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(TAG_BOTTOM_SHEET_STATE, mBottomSheetBehavior!!.state)
    }

    override fun onStop() {
        super.onStop()
        PreferenceTool.instance.controlPanel = mBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnbinder!!.unbind()
    }

    @OnClick(R.id.control_hide_close_button)
    protected fun onCloseInfoClick() {
        mIsShowInfoPanel = false
        mControlInfoLayout!!.visibility = View.INVISIBLE
        PreferenceTool.instance.controlInfo = mIsShowInfoPanel
    }

    private fun init(savedInstanceState: Bundle?) {
        mControlInfoLayout!!.visibility = View.INVISIBLE
        mViewPager!!.adapter = AdapterForPages(childFragmentManager)
        mControlLayout!!.alpha = ALPHA_COMMON_PANEL - 1.0f

        mBottomSheetBehavior = BottomSheetBehavior.from(mControlLayout!!)
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        mBottomSheetBehavior!!.setBottomSheetCallback(object : BaseBottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                super.onStateChanged(view, i)
                when (i) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (mIsShowInfoPanel) {
                            mControlInfoLayout!!.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onSlide(view: View, v: Float) {
                super.onSlide(view, v)
                if (mControlInfoLayout != null) {
                    mControlInfoLayout!!.alpha = ALPHA_INFO_PANEL - v
                }

                if (mControlLayout != null) {
                    mControlLayout!!.alpha = ALPHA_COMMON_PANEL - v
                }
            }
        })

        restoreStates(savedInstanceState)
    }

    private fun restoreStates(savedInstanceState: Bundle?) {
        mIsShowInfoPanel = PreferenceTool.instance.controlInfo
        if (PreferenceTool.instance.controlPanel) {
            mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    /*
    * Adapter for view pager
    * */
    private inner class AdapterForPages(mFragmentManager: FragmentManager) : FragmentPagerAdapter(mFragmentManager) {

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                PLAYLIST_PAGE -> return getString(R.string.pagePlaylist)
                EXPLORER_PAGE -> return getString(R.string.pageExplorer)
            }

            return TITLE_NOT_NAMED + position
        }

        override fun getItem(position: Int): Fragment? {
            when (position) {
                PLAYLIST_PAGE -> return PlaylistFragment.newInstance()
                EXPLORER_PAGE -> return ExplorerFragment.newInstance()
            }

            return null
        }

        override fun getCount(): Int {
            return COUNT_PAGES
        }
    }

    companion object {

        val TAG = PagerFragment::class.java.simpleName
        private val TAG_BOTTOM_SHEET_STATE = "TAG_BOTTOM_SHEET_STATE"

        private val ALPHA_INFO_PANEL = 1.0f
        private val ALPHA_COMMON_PANEL = 1.8f
        private val COUNT_PAGES = 2
        private val PLAYLIST_PAGE = 0
        private val EXPLORER_PAGE = 1
        private val TITLE_NOT_NAMED = "Not_named_№"

        fun newInstance(): PagerFragment {
            return PagerFragment()
        }
    }

}
