package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.ui.views.RecycleViewLayoutManager;


public class PlaylistFragment extends BaseFragment implements View.OnClickListener {

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
    public void onDestroyView() {
        super.onDestroyView();
        removeButtonsCallback();
        mUnbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_track_button:
                break;
        }
    }

    private void init(final Bundle savedInstanceState) {
        setButtonsCallback();

        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.recycle_layout_animation);
        mRecycleViewLayoutManager = new RecycleViewLayoutManager(getContext());
        mRecycleViewLayoutManager.setCenter(RECYCLE_CENTER);
        mRecycleViewLayoutManager.setShrinkAmount(RECYCLE_SHRINK_AMOUNT);
        mRecycleViewLayoutManager.setShrinkDistance(RECYCLE_SHRINK_CENTER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setLayoutAnimation(animation);
        mRecyclerView.setLayoutManager(mRecycleViewLayoutManager);
        mRecyclerView.addOnScrollListener(new OnScrollRecycleViewListener());
    }

    private void setButtonsCallback() {
        mFindTrackButton.setOnClickListener(this);
    }

    private void removeButtonsCallback() {
        mFindTrackButton.setOnClickListener(null);
    }

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
