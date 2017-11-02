package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;


public class ControlFragment extends BaseFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static final String TAG = ControlFragment.class.getSimpleName();
    private static final String NOTES = "♫♪♭♩";

    protected Unbinder mUnbinder;

    /*
    * Controls
    * */
    @BindView(R.id.play_pause_control)
    protected ImageButton mPlayPauseButton;
    @BindView(R.id.expand_control)
    protected ImageButton mExpandButton;
    @BindView(R.id.random_control)
    protected ImageButton mRandomButton;
    @BindView(R.id.previous_control)
    protected ImageButton mPreviousButton;
    @BindView(R.id.next_control)
    protected ImageButton mNextButton;
    @BindView(R.id.repeat_control)
    protected ImageButton mRepeatButton;

    /*
    * Seeker
    * */
    @BindView(R.id.seek_position_control)
    protected SeekBar mSeekBar;

    /*
    * Info
    * */
    @BindView(R.id.track_name_control)
    protected TextView mTrackNameTextView;
    @BindView(R.id.number_tracks_control)
    protected TextView mTrackNumberTextView;
    @BindView(R.id.position_time_control)
    protected TextView mCurrentTimeTextView;
    @BindView(R.id.total_time_control)
    protected TextView mTotalTimeTextView;


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
        removeButtonsCallback();
        mUnbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause_control:
                break;
            case R.id.expand_control:
                break;
            case R.id.random_control:
                break;
            case R.id.previous_control:
                break;
            case R.id.next_control:
                break;
            case R.id.repeat_control:
                break;
        }
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

    private void init() {
        setButtonsCallback();
    }

    private void setButtonsCallback() {
        mPlayPauseButton.setOnClickListener(this);
        mExpandButton.setOnClickListener(this);
        mRandomButton.setOnClickListener(this);
        mPreviousButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mRepeatButton.setOnClickListener(this);
    }

    private void removeButtonsCallback() {
        mPlayPauseButton.setOnClickListener(null);
        mExpandButton.setOnClickListener(null);
        mRandomButton.setOnClickListener(null);
        mPreviousButton.setOnClickListener(null);
        mNextButton.setOnClickListener(null);
        mRepeatButton.setOnClickListener(null);
    }


}
