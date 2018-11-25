package ru.testsimpleapps.coloraudioplayer.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.managers.player.AudioPlayer
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool
import ru.testsimpleapps.coloraudioplayer.managers.tools.TextTool
import ru.testsimpleapps.coloraudioplayer.managers.tools.TimeTool
import ru.testsimpleapps.coloraudioplayer.service.PlayerService


class ControlFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener {

    protected lateinit var mUnbinder: Unbinder

    /*
    * Controls
    * */
    @BindView(R.id.control_play_pause)
    lateinit var mPlayPauseButton: ImageButton
    @BindView(R.id.control_expand)
    lateinit var mExpandButton: ImageButton
    @BindView(R.id.control_random)
    lateinit var mRandomButton: ImageButton
    @BindView(R.id.control_previous)
    lateinit var mPreviousButton: ImageButton
    @BindView(R.id.control_next)
    lateinit var mNextButton: ImageButton
    @BindView(R.id.control_repeat)
    lateinit var mRepeatButton: ImageButton

    /*
    * Seeker
    * */
    @BindView(R.id.control_seek_position)
    lateinit var mSeekBar: SeekBar

    /*
    * Info
    * */
    @BindView(R.id.control_track_name)
    lateinit var mTrackNameTextView: TextView
    @BindView(R.id.control_number_tracks)
    lateinit var mTrackNumberTextView: TextView
    @BindView(R.id.control_position_time)
    lateinit var mCurrentTimeTextView: TextView
    @BindView(R.id.control_total_time)
    lateinit var mTotalTimeTextView: TextView
    @BindView(R.id.control_time_layout)
    lateinit var mTimeLayout: LinearLayout

    private var mIsTouchSeekBar = false
    private var mDuration = 0

    private val mMessageReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null) {
                val action = intent.action
                if (action != null) {

                    // Update image_play/image_pause button
                    if (action == PlayerService.RECEIVER_PLAY_PAUSE) {
                        if (intent.hasExtra(PlayerService.EXTRA_PLAY_PAUSE)) {
                            setPlayPauseButton(intent.getBooleanExtra(PlayerService.EXTRA_PLAY_PAUSE, false))
                        }
                    }

                    // Update SeekBar
                    if (action == PlayerService.RECEIVER_PLAY_PROGRESS) {
                        if (intent.hasExtra(PlayerService.EXTRA_PLAY_PROGRESS) && intent.hasExtra(PlayerService.EXTRA_PLAY_DURATION)) {
                            val progress = intent.getIntExtra(PlayerService.EXTRA_PLAY_PROGRESS, AudioPlayer.MIN_SEEK_POSITION)
                            val duration = intent.getIntExtra(PlayerService.EXTRA_PLAY_DURATION, AudioPlayer.MIN_SEEK_POSITION)
                            setSeekBarProgress(progress, duration)
                            setTrackDuration(progress, duration)
                        }
                    }

                    // Update track position
                    if (action == PlayerService.RECEIVER_PLAYLIST_TRACKS) {
                        if (intent.hasExtra(PlayerService.EXTRA_PLAYLIST_POSITION) && intent.hasExtra(PlayerService.EXTRA_PLAYLIST_TOTAL)) {
                            setTrackPosition(intent.getLongExtra(PlayerService.EXTRA_PLAYLIST_POSITION, 0L),
                                    intent.getLongExtra(PlayerService.EXTRA_PLAYLIST_TOTAL, 0L))
                        }
                    }

                    // Update track name
                    if (action == PlayerService.RECEIVER_PLAYLIST_NAME) {
                        if (intent.hasExtra(PlayerService.EXTRA_PLAYLIST_NAME)) {
                            setTrackName(getRunningString(intent.getStringExtra(PlayerService.EXTRA_PLAYLIST_NAME), " ", mTrackNameTextView))
                        }
                    }
                }
            }
        }
    }

    private val intentFilter: IntentFilter
        get() {
            val intentFilter = IntentFilter()
            intentFilter.addAction(PlayerService.RECEIVER_PLAY_PAUSE)
            intentFilter.addAction(PlayerService.RECEIVER_PLAY_PROGRESS)
            intentFilter.addAction(PlayerService.RECEIVER_PLAYLIST_TRACKS)
            intentFilter.addAction(PlayerService.RECEIVER_PLAYLIST_NAME)
            return intentFilter
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_control, container, false)
        mUnbinder = ButterKnife.bind(this, view)
        init(savedInstanceState)
        LocalBroadcastManager.getInstance(context!!).registerReceiver(mMessageReceiver, intentFilter)
        return view
    }

    override fun onDestroyView() {
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(mMessageReceiver)
        super.onDestroyView()
        mUnbinder.unbind()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        mIsTouchSeekBar = true
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (mDuration > 0) {
            val step = mDuration / MAX_SEEK_POSITION
            mCurrentTimeTextView!!.text = TimeTool.getDuration((step * seekBar.progress).toLong())
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        mIsTouchSeekBar = false
        if (mDuration > 0) {
            val step = mDuration / MAX_SEEK_POSITION
            PlayerService.sendCommandSeek(step * seekBar.progress)
        }
    }

    @OnClick(R.id.control_expand)
    protected fun onExpandClick() {
        val isExpand = mTimeLayout!!.visibility != View.VISIBLE
        setPartPanelVisibility(isExpand)
        PreferenceTool.instance.controlPanelExpand = isExpand
    }

    @OnClick(R.id.control_play_pause)
    protected fun onPlayPauseClick() {
        PlayerService.sendCommandPlayPause()
    }

    @OnClick(R.id.control_repeat)
    protected fun onRepeatClick() {
        setRepeatButton(PlayerConfig.instance.setRepeat())
    }

    @OnClick(R.id.control_random)
    protected fun onRandomClick() {
        setRandomButton(PlayerConfig.instance.setRandom())
    }

    @OnClick(R.id.control_next)
    protected fun onNextClick() {
        PlayerService.sendCommandNext()
    }

    @OnClick(R.id.control_previous)
    protected fun onPreviousClick() {
        PlayerService.sendCommandPrevious()
    }

    private fun init(savedInstanceState: Bundle?) {
        mSeekBar!!.setOnSeekBarChangeListener(this)
        mSeekBar!!.max = MAX_SEEK_POSITION

        setPartPanelVisibility(PreferenceTool.instance.controlPanelExpand)
        setRepeatButton(PlayerConfig.instance.repeat!!)
        setRandomButton(PlayerConfig.instance.isRandom)
        setTrackPosition(CursorFactory.instance.position() + 1, CursorFactory.instance.size())

        if (PlayerConfig.instance.playlistId == IPlaylist.ERROR_CODE) {
            setTrackName(getRunningString(NOTES, NOTES, mTrackNameTextView))
        } else {
            setTrackName(getRunningString(CursorFactory.instance.trackName, " ", mTrackNameTextView))
        }

        PlayerService.sendCommandControlCheck()
    }

    private fun setPartPanelVisibility(isVisible: Boolean) {
        if (isVisible) {
            mExpandButton!!.setImageResource(R.drawable.image_expand_inactive)
            mTimeLayout!!.visibility = View.VISIBLE
            mTrackNameTextView!!.visibility = View.VISIBLE
        } else {
            mExpandButton!!.setImageResource(R.drawable.image_expand_active)
            mTimeLayout!!.visibility = View.GONE
            mTrackNameTextView!!.visibility = View.GONE
        }
    }

    private fun getRunningString(text: String, symbols: String, textView: TextView?): String {
        val widthScreen = displayWidth
        val widthText = TextTool.measureTextWidth(text, textView)
        val widthSymbols = TextTool.measureTextWidth(symbols, textView)
        val delta = widthScreen - widthText
        val runningString = StringBuilder(text)

        if (delta >= 0) {
            val n = delta / widthSymbols + 5
            for (i in 0 until n + 5) {
                runningString.append(symbols)
            }
        }

        return runningString.toString()
    }

    private fun setTrackName(name: String) {
        if (mTrackNameTextView != null) {
            mTrackNameTextView!!.text = name
            mTrackNameTextView!!.isSelected = true
            mTrackNameTextView!!.requestFocus()
        }
    }

    private fun setTrackPosition(current: Long, total: Long) {
        if (mTrackNumberTextView != null) {
            mTrackNumberTextView!!.text = "" + (if (current > 0) current else 0) + "/" + total
        }
    }

    private fun setSeekBarProgress(progress: Int, duration: Int) {
        if (mSeekBar != null && mCurrentTimeTextView != null && !mIsTouchSeekBar) {
            mDuration = duration
            val part = progress.toFloat() / mDuration.toFloat()
            mSeekBar!!.progress = (part * MAX_SEEK_POSITION).toInt()
            mCurrentTimeTextView!!.text = TimeTool.getDuration(progress.toLong())
        }
    }

    private fun setTrackDuration(progress: Int, duration: Int) {
        if (mCurrentTimeTextView != null && mTotalTimeTextView != null) {
            mTotalTimeTextView!!.text = TimeTool.getDuration(duration.toLong())
            if (!mIsTouchSeekBar) {
                mCurrentTimeTextView!!.text = TimeTool.getDuration(progress.toLong())
            }
        }
    }

    private fun setPlayPauseButton(isPlay: Boolean) {
        if (mPlayPauseButton != null) {
            if (isPlay) {
                mPlayPauseButton!!.setImageResource(R.drawable.image_pause)
            } else {
                mPlayPauseButton!!.setImageResource(R.drawable.image_play)
            }
        }
    }

    private fun setRepeatButton(repeat: PlayerConfig.Repeat) {
        when (repeat) {
            PlayerConfig.Repeat.ALL -> mRepeatButton!!.setImageResource(R.drawable.image_repeat_active)
            PlayerConfig.Repeat.ONE -> mRepeatButton!!.setImageResource(R.drawable.image_repeat_active_one)
            PlayerConfig.Repeat.NONE -> mRepeatButton!!.setImageResource(R.drawable.image_repeat_inactive)
        }
    }

    private fun setRandomButton(isRandom: Boolean) {
        if (isRandom) {
            mRandomButton!!.setImageResource(R.drawable.image_shuffle_active)
        } else {
            mRandomButton!!.setImageResource(R.drawable.image_shuffle_inactive)
        }
    }

    companion object {

        val TAG = ControlFragment::class.java.simpleName
        private val NOTES = " ♫ ♪ ♭ ♩"
        private val MAX_SEEK_POSITION = 1000

        fun newInstance(): ControlFragment {
            return ControlFragment()
        }
    }

}
