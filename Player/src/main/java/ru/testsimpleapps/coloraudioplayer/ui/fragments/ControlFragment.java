package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool;


public class ControlFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {

    public static final String TAG = ControlFragment.class.getSimpleName();
    private static final String NOTES = "♫♪♭♩";

    protected Unbinder mUnbinder;

    /*
    * Controls
    * */
    @BindView(R.id.control_play_pause)
    protected ImageButton mPlayPauseButton;
    @BindView(R.id.control_expand)
    protected ImageButton mExpandButton;
    @BindView(R.id.control_random)
    protected ImageButton mRandomButton;
    @BindView(R.id.control_previous)
    protected ImageButton mPreviousButton;
    @BindView(R.id.control_next)
    protected ImageButton mNextButton;
    @BindView(R.id.control_repeat)
    protected ImageButton mRepeatButton;

    /*
    * Seeker
    * */
    @BindView(R.id.control_seek_position)
    protected SeekBar mSeekBar;

    /*
    * Info
    * */
    @BindView(R.id.control_track_name)
    protected TextView mTrackNameTextView;
    @BindView(R.id.control_number_tracks)
    protected TextView mTrackNumberTextView;
    @BindView(R.id.control_position_time)
    protected TextView mCurrentTimeTextView;
    @BindView(R.id.control_total_time)
    protected TextView mTotalTimeTextView;
    @BindView(R.id.control_time_layout)
    protected RelativeLayout mTimeLayout;


    public static ControlFragment newInstance() {
        ControlFragment fragment = new ControlFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_control, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @OnClick(R.id.control_expand)
    protected void onExpandClick() {
        final boolean isExpand = !(mTimeLayout.getVisibility() == View.VISIBLE);
        setPartPanelVisibility(isExpand);
        PreferenceTool.getInstance().setExpand(isExpand);
    }

    private void init() {
        setPartPanelVisibility(PreferenceTool.getInstance().isExpand());
    }

    private void setPartPanelVisibility(final boolean isVisible) {
        if (isVisible) {
            mTimeLayout.setVisibility(View.VISIBLE);
            mTrackNameTextView.setVisibility(View.VISIBLE);
        } else {
            mTimeLayout.setVisibility(View.GONE);
            mTrackNameTextView.setVisibility(View.GONE);
        }
    }

}
