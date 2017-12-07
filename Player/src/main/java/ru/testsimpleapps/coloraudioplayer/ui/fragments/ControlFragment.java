package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
import ru.testsimpleapps.coloraudioplayer.managers.player.AudioPlayer;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.TimeTool;
import ru.testsimpleapps.coloraudioplayer.service.PlayerService;


public class ControlFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {

    public static final String TAG = ControlFragment.class.getSimpleName();
    private static final String NOTES = "♫♪♭♩";
    private static final int MAX_SEEK_POSITION = 1000;

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

    private int mDuration;

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
    public void onStart() {
        super.onStart();
        // Broadcast
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, getIntentFilter());
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        final int step = mDuration / MAX_SEEK_POSITION;
        PlayerService.sendCommandSeek(step * seekBar.getProgress());
    }

    @OnClick(R.id.control_expand)
    protected void onExpandClick() {
        final boolean isExpand = !(mTimeLayout.getVisibility() == View.VISIBLE);
        setPartPanelVisibility(isExpand);
        PreferenceTool.getInstance().setControlPanelExpand(isExpand);
    }

    @OnClick(R.id.control_play_pause)
    protected void onPlayPauseClick() {
        PlayerService.sendCommandPlayPause();
    }

    @OnClick(R.id.control_repeat)
    protected void onRepeatClick() {
        setRepeatButton(PlayerConfig.getInstance().setRepeat());
    }

    @OnClick(R.id.control_random)
    protected void onRandomClick() {
        setRandomButton(PlayerConfig.getInstance().setRandom());
    }

    @OnClick(R.id.control_next)
    protected void onNextClick() {
        PlayerService.sendCommandNext();
    }

    @OnClick(R.id.control_previous)
    protected void onPreviousClick() {
        PlayerService.sendCommandPrevious();
    }

    private void init() {
        setPartPanelVisibility(PreferenceTool.getInstance().getControlPanelExpand());
        setRepeatButton(PlayerConfig.getInstance().getRepeat());
        setRandomButton(PlayerConfig.getInstance().isRandom());
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(MAX_SEEK_POSITION);
        PlayerService.sendCommandControlCheck();
    }

    private void setPartPanelVisibility(final boolean isVisible) {
        if (isVisible) {
            mExpandButton.setImageResource(R.drawable.expand_inactive);
            mTimeLayout.setVisibility(View.VISIBLE);
            mTrackNameTextView.setVisibility(View.VISIBLE);

        } else {
            mExpandButton.setImageResource(R.drawable.expand_active);
            mTimeLayout.setVisibility(View.GONE);
            mTrackNameTextView.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                if (action != null) {

                    // Update play/pause button
                    if (action.equals(PlayerService.RECEIVER_PLAY_PAUSE)) {
                        if (intent.hasExtra(PlayerService.EXTRA_PLAY_PAUSE)) {
                            setPlayPauseButton(intent.getBooleanExtra(PlayerService.EXTRA_PLAY_PAUSE, false));
                        }
                    }

                    // Update SeekBar
                    if (action.equals(PlayerService.RECEIVER_PLAY_PROGRESS)) {
                        if (intent.hasExtra(PlayerService.EXTRA_PLAY_PROGRESS)) {
                            setSeekBarProgress(intent.getIntExtra(PlayerService.EXTRA_PLAY_PROGRESS, AudioPlayer.MIN_SEEK_POSITION));
                        }

                        if (intent.hasExtra(PlayerService.EXTRA_PLAY_DURATION)) {
                            setTrackDuration(intent.getIntExtra(PlayerService.EXTRA_PLAY_DURATION, AudioPlayer.MIN_SEEK_POSITION));
                        }
                    }
                }
            }
        }
    };

    private void setSeekBarProgress(final int progress) {
        if (mSeekBar != null) {
            final float part = (float) progress / (float) mDuration;
            mSeekBar.setProgress((int) (part * MAX_SEEK_POSITION));
            mCurrentTimeTextView.setText(TimeTool.getDuration(progress));
        }
    }

    private void setTrackDuration(final int duration) {
        mDuration = duration;
        mTotalTimeTextView.setText(TimeTool.getDuration(duration));
    }

    private void setPlayPauseButton(final boolean isPlay) {
        if (isPlay) {
            mPlayPauseButton.setImageResource(R.drawable.pause);
        } else {
            mPlayPauseButton.setImageResource(R.drawable.play);
        }
    }

    private void setRepeatButton(final PlayerConfig.Repeat repeat) {
        switch (repeat) {
            case ALL:
                mRepeatButton.setImageResource(R.drawable.repeat_active);
                break;
            case ONE:
                mRepeatButton.setImageResource(R.drawable.repeat_active_one);
                break;
            case NONE:
                mRepeatButton.setImageResource(R.drawable.repeat_inactive);
                break;
        }
    }

    private void setRandomButton(final boolean isRandom) {
        if (isRandom) {
            mRandomButton.setImageResource(R.drawable.shuffle_active);
        } else {
            mRandomButton.setImageResource(R.drawable.shuffle_inactive);
        }
    }

    private IntentFilter getIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.RECEIVER_PLAY_PAUSE);
        intentFilter.addAction(PlayerService.RECEIVER_PLAY_PROGRESS);
        return intentFilter;
    }

}
