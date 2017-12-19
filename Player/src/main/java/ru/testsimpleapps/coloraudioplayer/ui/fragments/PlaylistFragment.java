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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory;
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.TextTool;
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
    private InputMethodManager mInputMethodManager;

    public static PlaylistFragment newInstance() {
        PlaylistFragment fragment = new PlaylistFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, getIntentFilter());
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG_ADD_PANEL, mAdditionalPanel.getVisibility());
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        super.onDestroyView();
        mSearchTrackEditText.setOnFocusChangeListener(null);
        mUnbinder.unbind();
    }

    @OnClick(R.id.playlist_search_button)
    protected void onSearchClickButton() {
        if (mSearchTrackEditText.getVisibility() == View.INVISIBLE) {
            mSearchTrackEditText.setVisibility(View.VISIBLE);
            mSearchTrackEditText.requestFocus();
            mInputMethodManager.showSoftInput(mSearchTrackEditText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            final String text = mSearchTrackEditText.getText().toString();
            if (text.equals("")) {
                mSearchTrackEditText.setVisibility(View.INVISIBLE);
                mSearchTrackEditText.clearFocus();
                mInputMethodManager.hideSoftInputFromWindow(mSearchTrackEditText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_HIDDEN);
                mPlaylistAdapter.setSearchedPosition(IPlaylist.NOT_INIT);
                mPlaylistAdapter.notifyDataSetChanged();
            } else {

                // Go to position
                if (TextTool.isNumeric(text)) {
                    final int position = Integer.valueOf(text);
                    if (position > 0 && position <= mPlaylistAdapter.getItemCount()) {
                        mPlaylistAdapter.setSearchedPosition(position);
                        mPlaylistAdapter.notifyDataSetChanged();
                        mRecycleViewLayoutManager.scrollToPositionWithOffsetCenter(position + 1);
                        showToast(R.string.playlist_search_position);
                        return;
                    }
                }

                //  Go to text match
                final int position = mPlaylistAdapter.searchMatch(text);
                if (position > IPlaylist.NOT_INIT) {
                    mPlaylistAdapter.setSearchedPosition(position + 1);
                    mPlaylistAdapter.notifyDataSetChanged();
                    mRecycleViewLayoutManager.scrollToPositionWithOffsetCenter(position + 1);
                    showToast(R.string.playlist_search_text);
                    return;
                }
            }

            mPlaylistAdapter.setSearchedPosition(IPlaylist.NOT_INIT);
            showToast(R.string.playlist_search_no_match);
        }
    }

    @OnLongClick(R.id.playlist_search_button)
    protected boolean onSearchLongClickButton() {
        final long position = CursorFactory.getInstance().position();
        mRecyclerView.smoothScrollToPosition((int)position);
        return true;
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
        mRecycleViewLayoutManager.scrollToPositionWithOffsetCenter((int)CursorFactory.getInstance().position());
        mRecyclerView.smoothScrollBy(0, 1);
        mRecyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onItemClick(View view, int position) {
        PlayerService.sendCommandTrackSelect(mPlaylistAdapter.getItemId(position));
    }

    private void init(final Bundle savedInstanceState) {
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.recycleview_layout_animation);
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

        mSearchTrackEditText.setVisibility(View.INVISIBLE);
        mSearchTrackEditText.setOnFocusChangeListener(mOnFocusChangeListener);

        restoreStates(savedInstanceState);
        setPlaylist();
    }

    private void restoreStates(final Bundle savedInstanceState) {
        // Check image_previous states
        if (savedInstanceState != null) {
            // Panel state
            mAdditionalPanel.setVisibility(savedInstanceState.getInt(TAG_ADD_PANEL));
        } else {
            mRecycleViewLayoutManager.scrollToPositionWithOffsetCenter((int)CursorFactory.getInstance().position());
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

                    // Update current selection
                    if (action.equals(PlayerService.RECEIVER_PLAYLIST_POSITION)) {
                        mRecycleViewLayoutManager.scrollToPositionWithOffsetCenter((int)CursorFactory.getInstance().position());
                        mRecyclerView.smoothScrollBy(0, 1);
                        mPlaylistAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private void setPlaylist() {
        mPlaylistAdapter.setPlaylist(CursorFactory.getCopyInstance());
        mRecyclerView.smoothScrollBy(0, 1);
        mRecyclerView.scheduleLayoutAnimation();
    }

    private IntentFilter getIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.RECEIVER_PLAYLIST_ADD);
        intentFilter.addAction(PlayerService.RECEIVER_PLAYLIST_POSITION);
        return intentFilter;
    }

    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                mSearchTrackEditText.setText("");
                mSearchTrackEditText.setVisibility(View.INVISIBLE);
                mInputMethodManager.hideSoftInputFromWindow(mSearchTrackEditText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                mPlaylistAdapter.setSearchedPosition(IPlaylist.NOT_INIT);
                mPlaylistAdapter.notifyDataSetChanged();
            }
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
            if (mState == RecyclerView.SCROLL_STATE_DRAGGING && !mTranslationAnimation.isAnimating()) {
                mTranslationAnimation.animate(dy < 0);
            }
        }
    }

}
