package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ContainerData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ItemData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.MediaExplorerManager;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.BaseAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.ExplorerFilesAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.ExplorerFolderAdapter;


public class ExplorerFragment extends BaseFragment implements BaseAdapter.OnItemClickListener {

    public static final String TAG = ExplorerFragment.class.getSimpleName();
    private static final String TAG_ADD_PANEL = "TAG_ADD_PANEL";

    private static final int ANIMATION_TRANSLATION_DURATION = 200;
    private static final int ANIMATION_ALPHA_DURATION = 300;

    protected Unbinder mUnbinder;
    @BindView(R.id.explorer_additional_panel_layout)
    protected ConstraintLayout mAdditionalPanel;
    @BindView(R.id.explorer_list)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.explorer_back)
    protected ImageButton mBackButton;
    @BindView(R.id.explorer_add)
    protected ImageButton mAddButton;

    private ExplorerFilesAdapter mExplorerFilesAdapter;
    private ExplorerFolderAdapter mExplorerFolderAdapter;
    private List<FolderData> mFolderData;
    private Parcelable mListState;

    private AlphaAnimation mAlphaAnimation;
    private TranslateAnimation mTranslateAnimation;

    private OnTranslationListener mOnAnimation;
    private OnAlphaListener mOnFadeAnimation;

    public static ExplorerFragment newInstance() {
        ExplorerFragment fragment = new ExplorerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_explorer, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init(savedInstanceState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBackButton.clearAnimation();
        mAdditionalPanel.clearAnimation();
        mUnbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG_ADD_PANEL, mAdditionalPanel.getVisibility());
    }

    private void init(final Bundle savedInstanceState) {
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.recycle_layout_animation);
        mRecyclerView.setLayoutAnimation(animation);

        mBackButton.setVisibility(View.INVISIBLE);
        mAdditionalPanel.setVisibility(View.INVISIBLE);

        mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        mOnAnimation = new OnTranslationListener(mAdditionalPanel);
        mOnFadeAnimation = new OnAlphaListener(mBackButton);

        mExplorerFilesAdapter = new ExplorerFilesAdapter(getContext());
        mExplorerFolderAdapter = new ExplorerFolderAdapter(getContext());

        MediaExplorerManager.getInstance().findMedia();
        mFolderData = MediaExplorerManager.getInstance().getAlbums();
        mExplorerFolderAdapter.setItems(mFolderData);
        mExplorerFolderAdapter.setOnItemClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mExplorerFolderAdapter);
        mRecyclerView.addOnScrollListener(new OnScrollRecycleViewListener());

        restoreStates(savedInstanceState);
    }

    private void restoreStates(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TAG_ADD_PANEL)) {
                mAdditionalPanel.setVisibility(savedInstanceState.getInt(TAG_ADD_PANEL));
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        mListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        final FolderData folderData = mExplorerFolderAdapter.getItem(position);
        final ContainerData<ItemData> itemData = folderData.getContainerItemData();
        mExplorerFilesAdapter.setItems(itemData.getList());
        mRecyclerView.setAdapter(mExplorerFilesAdapter);
        mRecyclerView.scheduleLayoutAnimation();
        backButtonAnimation(0.0f, 1.0f, true);
    }

    @OnClick(R.id.explorer_back)
    protected void backButtonClick() {
        restoreAdapter();
    }

    @OnClick(R.id.explorer_add)
    protected void addButtonClick() {
        for (FolderData folder : mFolderData) {
            for (ItemData file : folder.getContainerItemData().getList()) {
                if (folder.isChecked() || file.isChecked()) {
                    // Todo: add checked files or all folders
                }
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        return restoreAdapter();
    }

    private boolean restoreAdapter() {
        if (mRecyclerView.getAdapter() instanceof ExplorerFilesAdapter) {
            mRecyclerView.setAdapter(mExplorerFolderAdapter);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
            mRecyclerView.scheduleLayoutAnimation();
            backButtonAnimation(1.0f, 0.0f, false);
            return true;
        }

        return false;
    }


    private class OnScrollRecycleViewListener extends RecyclerView.OnScrollListener {

        private int mState = -1;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            mState = newState;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mState == RecyclerView.SCROLL_STATE_DRAGGING && !mOnAnimation.isAnimating()) {
                animateAddPanel(dy < 0);
            }
        }
    }

    private void backButtonAnimation(final float from, final float to, final boolean isVisible) {
        mAlphaAnimation = new AlphaAnimation(from, to);
        mAlphaAnimation.setDuration(ANIMATION_ALPHA_DURATION);
        mOnFadeAnimation.setVisible(isVisible);
        mAlphaAnimation.setAnimationListener(mOnFadeAnimation);
        mBackButton.startAnimation(mAlphaAnimation);
    }

    /*
    * Additional panel translation listener
    * */
    private class OnTranslationListener implements Animation.AnimationListener {

        private View mView;
        private boolean isAnimating = false;
        private boolean isTranslation = false;

        public OnTranslationListener(final View view) {
            mView = view;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            Log.d(TAG, "onAnimationStart()");
            isAnimating = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "onAnimationEnd()");
            isAnimating = false;
            if (isTranslation) {
                mView.setVisibility(View.INVISIBLE);
            } else {
                mView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            Log.d(TAG, "onAnimationRepeat()");
        }

        public boolean isAnimating() {
            return isAnimating;
        }

        public void setTranslationType(final boolean isDown) {
            isTranslation = isDown;
        }

    }

    /*
    * Back button on additional panel listener
    * */
    private class OnAlphaListener implements Animation.AnimationListener {

        private View mView;
        private boolean mIsAnimating = false;
        private boolean mIsVisible = false;

        public OnAlphaListener(final View view) {
            mView = view;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            Log.d(TAG, "onAnimationStart()");
            mIsAnimating = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "onAnimationEnd()");
            mIsAnimating = false;
            if (mIsVisible) {
                mView.setVisibility(View.VISIBLE);
            } else {
                mView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        public boolean isAnimating() {
            return mIsAnimating;
        }

        public void setVisible(final boolean isVisible) {
            mIsVisible = isVisible;
        }

    }

    /*
    * Hide or show additional panel animation
    * */
    private void animateAddPanel(final boolean isDown) {
        if ((isDown && mAdditionalPanel.getVisibility() == View.VISIBLE) ||
                (!isDown && mAdditionalPanel.getVisibility() == View.INVISIBLE)) {

            // Get view position
            final Rect rect = new Rect();
            mAdditionalPanel.getLocalVisibleRect(rect);

            final int height = mAdditionalPanel.getHeight();
            float fromTop;
            float toTop;

            // Get new coordinates
            if (isDown) {
                fromTop = rect.top;
                toTop = rect.top - height;
            } else {
                fromTop = rect.top - height;
                toTop = rect.top;
            }

            // Animate transition
            mOnAnimation.setTranslationType(isDown);
            mTranslateAnimation = new TranslateAnimation(rect.left, rect.left, fromTop, toTop);
            mTranslateAnimation.setDuration(ANIMATION_TRANSLATION_DURATION);
            mTranslateAnimation.setInterpolator(new LinearInterpolator());
            mTranslateAnimation.setAnimationListener(mOnAnimation);
            mAdditionalPanel.startAnimation(mTranslateAnimation);
        }
    }



}
