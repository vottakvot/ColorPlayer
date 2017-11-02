package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;


public class PlaylistFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = PlaylistFragment.class.getSimpleName();

    protected Unbinder mUnbinder;

    @BindView(R.id.search_track_button)
    protected ImageButton mSearchTrackButton;
    @BindView(R.id.search_track_input)
    protected EditText mInputTrackEdit;
    @BindView(R.id.playlist_list_fragment)
    protected RecyclerView mPlaylistRecyclerView;

    public static PlaylistFragment newInstance() {
        PlaylistFragment fragment = new PlaylistFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init();
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

    private void init() {
        setButtonsCallback();
    }

    private void setButtonsCallback() {
        mSearchTrackButton.setOnClickListener(this);
    }

    private void removeButtonsCallback() {
        mSearchTrackButton.setOnClickListener(null);
    }


}
