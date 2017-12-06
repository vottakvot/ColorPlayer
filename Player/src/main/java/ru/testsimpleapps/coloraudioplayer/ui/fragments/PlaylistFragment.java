package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory;
import ru.testsimpleapps.coloraudioplayer.service.PlayerService;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.PlaylistAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.animation.BaseAnimationListener;
import ru.testsimpleapps.coloraudioplayer.ui.views.RecycleViewLayoutManager;


public class PlaylistFragment extends BaseFragment {

    public static final String TAG = PlaylistFragment.class.getSimpleName();
    private static final float RECYCLE_CENTER = 1.0f;
    private static final float RECYCLE_SHRINK_AMOUNT = 0.1f;
    private static final float RECYCLE_SHRINK_CENTER = 1.0f;
    private static final int ANIMATION_TRANSLATION_DURATION = 200;

    protected Unbinder mUnbinder;

    @BindView(R.id.playlist_search_button)
    protected ImageButton mSearchTrackButton;
    @BindView(R.id.playlist_search_edit)
    protected EditText mSearchTrackEditText;
    @BindView(R.id.playlist_settings_button)
    protected ImageButton mSettingsButton;
    @BindView(R.id.playlist_list_fragment)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.playlist_additional_layout)
    protected ConstraintLayout mAdditionalPanel;

    private PlayerService mPlayerService;
    private PlaylistAdapter mPlaylistAdapter;
    private RecycleViewLayoutManager mRecycleViewLayoutManager;
    private TranslateAnimation mTranslateAnimation;
    private OnTranslationListener mTranslateListener;

    public static PlaylistFragment newInstance() {
        PlaylistFragment fragment = new PlaylistFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init(savedInstanceState);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getContext(), PlayerService.class);
        getContext().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unbindService(mServiceConnection);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void init(final Bundle savedInstanceState) {
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.recycle_layout_animation);
        mTranslateListener = new OnTranslationListener(mAdditionalPanel);
        mRecycleViewLayoutManager = new RecycleViewLayoutManager(getContext());
        mRecycleViewLayoutManager.setCenter(RECYCLE_CENTER);
        mRecycleViewLayoutManager.setShrinkAmount(RECYCLE_SHRINK_AMOUNT);
        mRecycleViewLayoutManager.setShrinkDistance(RECYCLE_SHRINK_CENTER);
        mRecycleViewLayoutManager.setDynamicCenter(true);

        mRecyclerView.setLayoutAnimation(animation);
        mRecyclerView.setLayoutManager(mRecycleViewLayoutManager);
        mRecyclerView.addOnScrollListener(new OnScrollRecycleViewListener());
        mPlaylistAdapter = new PlaylistAdapter(getContext());
    }

    @OnClick(R.id.playlist_search_button)
    protected void onSearchClickButton() {

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG,  "onServiceConnected()");
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            mPlayerService = binder.getService();
            mPlaylistAdapter.setPlaylist(CursorFactory.getCopyInstance());
            mRecyclerView.setAdapter(mPlaylistAdapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG,  "onServiceDisconnected()");
            mPlayerService = null;
        }
    };


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
            if (mState == RecyclerView.SCROLL_STATE_DRAGGING && !mTranslateListener.isAnimating()) {
                animateAddPanel(dy < 0);
            }
        }
    }

    /*
    * Additional panel translation listener
    * */
    private class OnTranslationListener extends BaseAnimationListener {

        private boolean isTranslation = false;

        public OnTranslationListener(final View view) {
            super(view);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            super.onAnimationEnd(animation);
            if (isTranslation) {
                mView.setVisibility(View.INVISIBLE);
            } else {
                mView.setVisibility(View.VISIBLE);
            }
        }

        public void setTranslationType(final boolean isDown) {
            isTranslation = isDown;
        }
    }

    /*
    * Hide or show additional panel animation
    * */
    private void animateAddPanel(final boolean isHide) {
        if ((isHide && mAdditionalPanel.getVisibility() == View.VISIBLE) ||
                (!isHide && mAdditionalPanel.getVisibility() == View.INVISIBLE)) {

            // Get view position
            final Rect rect = new Rect();
            mAdditionalPanel.getLocalVisibleRect(rect);

            final int height = mAdditionalPanel.getHeight();
            float fromTop;
            float toTop;

            // Get new coordinates
            if (isHide) {
                fromTop = rect.top;
                toTop = rect.top - height;
            } else {
                fromTop = rect.top - height;
                toTop = rect.top;
            }

            // Animate transition
            mTranslateListener.setTranslationType(isHide);
            mTranslateAnimation = new TranslateAnimation(rect.left, rect.left, fromTop, toTop);
            mTranslateAnimation.setDuration(ANIMATION_TRANSLATION_DURATION);
            mTranslateAnimation.setInterpolator(new LinearInterpolator());
            mTranslateAnimation.setAnimationListener(mTranslateListener);
            mAdditionalPanel.startAnimation(mTranslateAnimation);
        }
    }

}
