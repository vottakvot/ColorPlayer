package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import ru.testsimpleapps.coloraudioplayer.service.PlayerService;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.PlaylistAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.views.RecycleViewLayoutManager;


public class PlaylistFragment extends BaseFragment {

    public static final String TAG = PlaylistFragment.class.getSimpleName();
    private static final float RECYCLE_CENTER = 1.0f;
    private static final float RECYCLE_SHRINK_AMOUNT = 0.15f;
    private static final float RECYCLE_SHRINK_CENTER = 0.55f;

    protected Unbinder mUnbinder;

    @BindView(R.id.search_track_button)
    protected ImageButton mFindTrackButton;
    @BindView(R.id.search_track_input)
    protected EditText mFindTrackEditText;
    @BindView(R.id.playlist_list_fragment)
    protected RecyclerView mRecyclerView;

    private PlayerService mPlayerService;
    private PlaylistAdapter mPlaylistAdapter;
    private RecycleViewLayoutManager mRecycleViewLayoutManager;

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
        mRecycleViewLayoutManager = new RecycleViewLayoutManager(getContext());
        mRecycleViewLayoutManager.setCenter(RECYCLE_CENTER);
        mRecycleViewLayoutManager.setShrinkAmount(RECYCLE_SHRINK_AMOUNT);
        mRecycleViewLayoutManager.setShrinkDistance(RECYCLE_SHRINK_CENTER);

        mRecyclerView.setLayoutAnimation(animation);
        mRecyclerView.setLayoutManager(mRecycleViewLayoutManager);
        mRecyclerView.addOnScrollListener(new OnScrollRecycleViewListener());
        mPlaylistAdapter = new PlaylistAdapter(getContext());
    }

    @OnClick(R.id.search_track_button)
    protected void onSearchClickButton() {

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG,  "onServiceConnected()");
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            mPlayerService = binder.getService();
            mPlaylistAdapter.setPlaylist(CursorFactory.getViewInstance());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG,  "onServiceDisconnected()");
            mPlayerService = null;
        }
    };

    private class OnScrollRecycleViewListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

}
