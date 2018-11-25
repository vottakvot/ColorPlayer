package ru.testsimpleapps.coloraudioplayer.ui.dialogs


import android.content.Context
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView

import java.util.ArrayList
import java.util.HashMap

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.data.DrawerItem
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity
import ru.testsimpleapps.coloraudioplayer.ui.adapters.BaseListAdapter

class EqualizerDialog(private val mContext: Context) : BaseDialog(mContext), SeekBar.OnSeekBarChangeListener {
    
    @BindView(R.id.equalizer_choose_mode_spinner)
    lateinit var mEqualizerChooseModeSpinner: Spinner
    @BindView(R.id.equalizer_settings_recycle)
    lateinit var mEqualizerSettingsRecycle: RecyclerView
    @BindView(R.id.equalizer_cancel)
    lateinit var mEqualizerCancel: Button

    private var mEqualizerAdapter: EqualizerAdapter? = null
    private var mDefaultModes: HashMap<Short, String>? = null
    private var mEqualizer: Equalizer? = null
    private var mBassBoost: BassBoost? = null

    private var mMinLevel = 0
    private var mMaxLevel = 0

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        init()
    }

    @OnClick(R.id.equalizer_cancel)
    protected fun onCancelClick() {
        dismiss()
    }

    /*
    * Seekbar listeners
    * */
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        Log.d(TAG, this.javaClass.name.toString() + "onStopTrackingTouch - tag: " + seekBar.tag)
        if (seekBar.tag as Int == 0) {
            mBassBoost!!.setStrength((seekBar.progress * 50).toShort())
        } else {
            val newLevel = (mMinLevel + (mMaxLevel - mMinLevel) * seekBar.progress / seekBar.max).toShort()
            val newBand = getBandPosition(seekBar.tag as Int)
            mEqualizer!!.setBandLevel(newBand, newLevel)
        }
    }

    /*
    * Spinner listeners
    * */
//    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//
//    }
//
//    override fun onNothingSelected(parent: AdapterView<*>) {
//
//    }

    fun show(sessionId: Int) {
        super.show()
        initSession(sessionId)
    }

    private fun initSession(sessionId: Int): Boolean {
        try {
            initEqualizer(sessionId)
            mBassBoost = BassBoost(NORMAL_PRIORITY, sessionId)
        } catch (e: RuntimeException) {
            return false
        }

        return true
    }

    private fun init() {
        setContentView(R.layout.dialog_equalizer)
        ButterKnife.bind(this)

        mEqualizerAdapter = EqualizerAdapter(mContext)
        mEqualizerSettingsRecycle!!.layoutManager = LinearLayoutManager(mContext)
        mEqualizerSettingsRecycle!!.adapter = mEqualizerAdapter
    }

    private fun initEqualizer(sessionId: Int) {
        mEqualizer = Equalizer(NORMAL_PRIORITY, sessionId)
        mMinLevel = (if (mEqualizer!!.bandLevelRange[0].toInt() != 0) mEqualizer!!.bandLevelRange[0] else 1).toInt()
        mMaxLevel = (if (mEqualizer!!.bandLevelRange[1].toInt() != 0) mEqualizer!!.bandLevelRange[1] else 1).toInt()
        initEqualizerSpinner()
    }

    private fun initEqualizerSpinner() {
        if (mEqualizer!!.numberOfPresets > 0) {
            mDefaultModes = HashMap()
            for (i in 0 until mEqualizer!!.numberOfPresets) {
                mDefaultModes!![i.toShort()] = mEqualizer!!.getPresetName(i.toShort())
            }

            val spinnerItems = ArrayList(mDefaultModes!!.values)
            spinnerItems.add(mContext.resources.getString(R.string.equalizer_custom))

            val adapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_item, spinnerItems)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mEqualizerChooseModeSpinner!!.adapter = adapter
//            mEqualizerChooseModeSpinner!!.setOnItemSelectedListener(this)
        }
    }

    private fun milliHzToString(milliHz: Int): String {
        if (milliHz < 1000)
            return ""
        return if (milliHz < 1000000)
            "" + milliHz / 1000 + "Hz"
        else
            "" + milliHz / 1000000 + "kHz"
    }

    private fun getBandPosition(position: Int): Short {
        return (if (mBassBoost != null) position - 1 else position).toShort()
    }


    /*
    * Equalizer adapter
    * */
    inner class EqualizerAdapter(private val mContext: Context) : BaseListAdapter<DrawerItem>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
            val viewItem = LayoutInflater.from(viewGroup.context).inflate(R.layout.equalizer_item, viewGroup, false)
            return ViewHolderItem(viewItem)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = holder as ViewHolderItem

            if (position == 0 && mBassBoost != null) {
                viewHolder.mEqualizerItemText!!.visibility = TextView.VISIBLE
                viewHolder.mEqualizerItemText!!.text = mContext.getString(R.string.equalizer_bass_boost)
                viewHolder.mEqualizerItemSeek!!.progress = mBassBoost!!.roundedStrength / 50
                viewHolder.mEqualizerMaxText!!.text = INFINITY
            } else if (mEqualizer != null) {
                viewHolder.mEqualizerItemText!!.visibility = TextView.GONE
                val settings = mEqualizer!!.properties
                val bands = settings.bandLevels
                val progress = bands[getBandPosition(position).toInt()] / ((mMaxLevel - mMinLevel) / viewHolder.mEqualizerItemSeek!!.max) + MAX_RANGE / 2
                viewHolder.mEqualizerItemSeek!!.progress = progress
                val range = mEqualizer!!.getBandFreqRange(getBandPosition(position))
                viewHolder.mEqualizerMaxText!!.text = milliHzToString(range[1])
            }

            viewHolder.mEqualizerItemSeek!!.tag = position
            viewHolder.mEqualizerItemSeek!!.max = MAX_RANGE
            viewHolder.mEqualizerItemSeek!!.bringToFront()
            viewHolder.mEqualizerItemSeek!!.setOnSeekBarChangeListener(this@EqualizerDialog)
        }

        override fun getItemCount(): Int {
            var count = 0

            if (mBassBoost != null) {
                ++count
            }

            if (mEqualizer != null) {
                count += mEqualizer!!.numberOfBands.toInt()
            }

            return count
        }

        protected inner class ViewHolderItem(view: View) : RecyclerView.ViewHolder(view) {

            @BindView(R.id.equalizer_item_text)
             lateinit var mEqualizerItemText: TextView
            @BindView(R.id.equalizer_item_seek)
             lateinit var mEqualizerItemSeek: SeekBar
            @BindView(R.id.equalizer_max_text)
             lateinit var mEqualizerMaxText: TextView

            init {
                ButterKnife.bind(this, view)
                view.setOnClickListener {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, layoutPosition)
                    }
                }
            }
        }

    }

    companion object {

        val TAG = EqualizerDialog::class.java.simpleName
        val NORMAL_PRIORITY = 0

        private val MAX_RANGE = 20
        private val ZERO = "|"
        private val INFINITY = "\u221E"
    }

}
