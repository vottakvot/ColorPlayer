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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool;


public class PagerFragment extends BaseFragment {

    public static final String TAG = PagerFragment.class.getSimpleName();
    private static final String TAG_BOTTOM_SHEET_STATE = "TAG_BOTTOM_SHEET_STATE";
    private static final int ANIMATION_DURATION_FROM = 1000;
    private static final int ANIMATION_DURATION_TO = 8000;

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
    ImageButton mCloseInfoButton;

    private AlphaAnimation mAlphaAnimationFrom;
    private AlphaAnimation mAlphaAnimationTo;
    private OnAlphaListener mOnAlphaListener;

    public static PagerFragment newInstance() {
        PagerFragment fragment = new PagerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.control_hide_close_button)
    protected void onCloseInfoClick() {
        PreferenceTool.getInstance().setControlInfo(false);
        mControlInfoLayout.clearAnimation();
    }

    private void init(final Bundle savedInstanceState) {
        mOnAlphaListener = new OnAlphaListener(mControlInfoLayout);
        mAlphaAnimationFrom = new AlphaAnimation(0.0f, 1.0f);
        mAlphaAnimationFrom.setDuration(ANIMATION_DURATION_FROM);
        mAlphaAnimationFrom.setAnimationListener(mOnAlphaListener);
        mAlphaAnimationTo = new AlphaAnimation(1.0f, 0.0f);
        mAlphaAnimationTo.setDuration(ANIMATION_DURATION_TO);

        mControlInfoLayout.setVisibility(View.INVISIBLE);
        mViewPager.setAdapter(new AdapterForPages(getChildFragmentManager()));
        mBottomSheetBehavior = BottomSheetBehavior.from(mControlLayout);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        if (PreferenceTool.getInstance().getControlInfo()) {
                            mControlInfoLayout.startAnimation(mAlphaAnimationFrom);
//                        }
                        break;
                    }

                    case BottomSheetBehavior.STATE_EXPANDED: {
                        mControlInfoLayout.clearAnimation();
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        restoreStates(savedInstanceState);
    }

    private void restoreStates(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TAG_BOTTOM_SHEET_STATE)) {
                mBottomSheetBehavior.setState(savedInstanceState.getInt(TAG_BOTTOM_SHEET_STATE));
            } else {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
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

    /*
    * Alpha layout
    * */
    private class OnAlphaListener implements Animation.AnimationListener {

        private View mView;
        private boolean isAnimating = false;

        public OnAlphaListener(final View view) {
            mView = view;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mView.startAnimation(mAlphaAnimationTo);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}
