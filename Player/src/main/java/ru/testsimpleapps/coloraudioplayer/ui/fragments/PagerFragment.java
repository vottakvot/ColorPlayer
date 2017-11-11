package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;


public class PagerFragment extends BaseFragment {

    public static final String TAG = PagerFragment.class.getSimpleName();

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

    public static PagerFragment newInstance() {
        PagerFragment fragment = new PagerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pager, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void init() {
        mViewPager.setAdapter(new AdapterForPages(getChildFragmentManager()));
        mBottomSheetBehavior = BottomSheetBehavior.from(mControlLayout);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetState());
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

    /*
    * Panel behavior state
    * */
    private class BottomSheetState extends BottomSheetBehavior.BottomSheetCallback {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            Log.d(TAG, BottomSheetState.class.getSimpleName() + " - onStateChanged(). State: " + newState);

            switch (newState) {
                case BottomSheetBehavior.STATE_COLLAPSED:
                    break;
                case BottomSheetBehavior.STATE_HIDDEN:
                    break;
                case BottomSheetBehavior.STATE_EXPANDED:
                    break;
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            Log.d(TAG, BottomSheetState.class.getSimpleName() + " - onSlide(). Offset: " + slideOffset);
        }

    }

}
