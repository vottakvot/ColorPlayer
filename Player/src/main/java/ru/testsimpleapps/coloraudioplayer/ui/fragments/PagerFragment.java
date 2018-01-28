package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool;
import ru.testsimpleapps.coloraudioplayer.ui.behavior.BaseBottomSheetCallback;


public class PagerFragment extends BaseFragment {

    public static final String TAG = PagerFragment.class.getSimpleName();
    private static final String TAG_BOTTOM_SHEET_STATE = "TAG_BOTTOM_SHEET_STATE";

    private static final float ALPHA_INFO_PANEL = 1.0f;
    private static final float ALPHA_COMMON_PANEL = 1.8f;
    private static final int COUNT_PAGES = 2;
    private static final int PLAYLIST_PAGE = 0;
    private static final int EXPLORER_PAGE = 1;
    private static final String TITLE_NOT_NAMED = "Not_named_№";

    private BottomSheetBehavior mBottomSheetBehavior;
    private Unbinder mUnbinder;

    @BindView(R.id.view_pager)
    protected ViewPager mViewPager;
    @BindView(R.id.control_behavior_layout)
    protected LinearLayout mControlLayout;
    @BindView(R.id.control_hide_notification)
    protected ConstraintLayout mControlInfoLayout;
    @BindView(R.id.control_hide_close_button)
    protected ImageButton mCloseInfoButton;

    private boolean mIsShowInfoPanel;

    public static PagerFragment newInstance() {
        PagerFragment fragment = new PagerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_pager, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init(savedInstanceState);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG_BOTTOM_SHEET_STATE, mBottomSheetBehavior.getState());
    }

    @Override
    public void onStop() {
        super.onStop();
        PreferenceTool.getInstance().setControlPanel(mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.control_hide_close_button)
    protected void onCloseInfoClick() {
        mIsShowInfoPanel = false;
        mControlInfoLayout.setVisibility(View.INVISIBLE);
        PreferenceTool.getInstance().setControlInfo(mIsShowInfoPanel);
    }

    private void init(final Bundle savedInstanceState) {
        mControlInfoLayout.setVisibility(View.INVISIBLE);
        mViewPager.setAdapter(new AdapterForPages(getChildFragmentManager()));
        mControlLayout.setAlpha(ALPHA_COMMON_PANEL - 1.0f);

        mBottomSheetBehavior = BottomSheetBehavior.from(mControlLayout);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setBottomSheetCallback(new BaseBottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                super.onStateChanged(view, i);
                switch (i) {
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        if (mIsShowInfoPanel) {
                            mControlInfoLayout.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                super.onSlide(view, v);
                if (mControlInfoLayout != null) {
                    mControlInfoLayout.setAlpha(ALPHA_INFO_PANEL - v);
                }

                if (mControlLayout != null) {
                    mControlLayout.setAlpha(ALPHA_COMMON_PANEL - v);
                }
            }
        });

        restoreStates(savedInstanceState);
    }

    private void restoreStates(final Bundle savedInstanceState) {
        mIsShowInfoPanel = PreferenceTool.getInstance().getControlInfo();
        if (PreferenceTool.getInstance().getControlPanel()) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    /*
    * Adapter for view pager
    * */
    private class AdapterForPages extends FragmentPagerAdapter {

        public AdapterForPages(FragmentManager mFragmentManager) {
            super(mFragmentManager);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case PLAYLIST_PAGE:
                    return getString(R.string.pagePlaylist);
                case EXPLORER_PAGE:
                    return getString(R.string.pageExplorer);
            }

            return TITLE_NOT_NAMED + position;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case PLAYLIST_PAGE:
                    return PlaylistFragment.newInstance();
                case EXPLORER_PAGE:
                    return ExplorerFragment.newInstance();
            }

            return null;
        }

        @Override
        public int getCount() {
            return COUNT_PAGES;
        }
    }

}
