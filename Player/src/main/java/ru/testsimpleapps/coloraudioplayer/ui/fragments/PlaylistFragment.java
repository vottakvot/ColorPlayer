package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory;
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool;
import ru.testsimpleapps.coloraudioplayer.service.PlayerService;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.PlaylistAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.animation.OnTranslationAnimation;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.PlaylistSettingsDialog;
import ru.testsimpleapps.coloraudioplayer.ui.views.RecycleViewLayoutManager;


public class PlaylistFragment extends BaseFragment implements PlaylistSettingsDialog.OnViewEvent,
        PlaylistAdapter.OnItemClickListener {

    public static final String TAG = PlaylistFragment.class.getSimpleName();
    private static final String TAG_ADD_PANEL = "TAG_ADD_PANEL";
    private static final float RECYCLE_CENTER = 1.0f;
    private static final float RECYCLE_SHRINK_AMOUNT = 0.1f;
    private static final float RECYCLE_SHRINK_CENTER = 1.0f;

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

    private PlaylistSettingsDialog mPlaylistDialog;
    private PlaylistAdapter mPlaylistAdapter;
    private RecycleViewLayoutManager mRecycleViewLayoutManager;
    private OnTranslationAnimation mTranslationAnimation;

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
        // Broadcast
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, getIntentFilter());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG_ADD_PANEL, mAdditionalPanel.getVisibility());
    }

    @Override
    public void onStop() {
        super.onStop();
        // Broadcast
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.playlist_search_button)
    protected void onSearchClickButton() {

    }

    @OnClick(R.id.playlist_settings_button)
    protected void onSettingsClickButton() {
        mPlaylistDialog.show();
    }

    @Override
    public void onSort(String value) {
        CursorFactory.newInstance();
        setPlaylist();
    }

    @Override
    public void onSortOrder(String value) {
        CursorFactory.newInstance();
        setPlaylist();
    }

    @Override
    public void onView(boolean isValue) {
        mPlaylistAdapter.setExpand(isValue);
        mRecyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onItemClick(View view, int position) {
        PlayerService.sendCommandTrackSelect(mPlaylistAdapter.getItemId(position));
    }

    private void init(final Bundle savedInstanceState) {
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.recycle_layout_animation);
        mTranslationAnimation = new OnTranslationAnimation(mAdditionalPanel, OnTranslationAnimation.DEFAULT_DURATION);
        mRecycleViewLayoutManager = new RecycleViewLayoutManager(getContext());
        mRecycleViewLayoutManager.setCenter(RECYCLE_CENTER);
        mRecycleViewLayoutManager.setShrinkAmount(RECYCLE_SHRINK_AMOUNT);
        mRecycleViewLayoutManager.setShrinkDistance(RECYCLE_SHRINK_CENTER);
        mRecycleViewLayoutManager.setDynamicCenter(true);

        mRecyclerView.setLayoutAnimation(animation);
        mRecyclerView.setLayoutManager(mRecycleViewLayoutManager);
        mRecyclerView.addOnScrollListener(new OnScrollRecycleViewListener());
        mPlaylistAdapter = new PlaylistAdapter(getContext());
        mPlaylistAdapter.setOnItemClickListener(this);
        mPlaylistAdapter.setExpand(PreferenceTool.getInstance().getPlaylistViewExpand());
        mRecyclerView.setAdapter(mPlaylistAdapter);

        mPlaylistDialog = new PlaylistSettingsDialog(getContext());
        mPlaylistDialog.setOnViewEvent(this);

        setPlaylist();
        restoreStates(savedInstanceState);
    }

    private void restoreStates(final Bundle savedInstanceState) {
        // Check previous states
        if (savedInstanceState == null) {

        } else {
            // Panel state
            mAdditionalPanel.setVisibility(savedInstanceState.getInt(TAG_ADD_PANEL));
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                if (action != null) {

                    // Update list resize
                    if (action.equals(PlayerService.RECEIVER_PLAYLIST_ADD)) {
                        setPlaylist();
                    }
                }
            }
        }
    };

    private void setPlaylist() {
        mPlaylistAdapter.setPlaylist(CursorFactory.getCopyInstance());
        mRecyclerView.scheduleLayoutAnimation();
    }

    private IntentFilter getIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.RECEIVER_PLAYLIST_ADD);
        return intentFilter;
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
            if (mState == RecyclerView.SCROLL_STATE_DRAGGING && !mTranslationAnimation.isAnimating()) {
                mTranslationAnimation.animate(dy < 0);
            }
        }
    }

}
