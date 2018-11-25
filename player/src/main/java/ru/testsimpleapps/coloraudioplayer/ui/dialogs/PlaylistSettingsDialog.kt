package ru.testsimpleapps.coloraudioplayer.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig
import ru.testsimpleapps.coloraudioplayer.managers.tools.CursorTool
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool

class PlaylistSettingsDialog(private val mContext: Context) : BaseDialog(mContext) {
    private var mOnViewEvent: OnViewEvent? = null

    /*
    * Radio groups
    * */
    @BindView(R.id.playlist_dialog_sort)
    lateinit var mRadioSort: RadioGroup

    /*
    * Radio buttons
    * */
    @BindView(R.id.playlist_dialog_sort_name)
    lateinit var mRadioButtonName: RadioButton
    @BindView(R.id.playlist_dialog_sort_date)
    lateinit var mRadioButtonDate: RadioButton
    @BindView(R.id.playlist_dialog_sort_value)
    lateinit var mRadioButtonValue: RadioButton
    @BindView(R.id.playlist_dialog_sort_artist)
    lateinit var mRadioButtonArtist: RadioButton
    @BindView(R.id.playlist_dialog_sort_albums)
    lateinit var mRadioButtonAlbums: RadioButton

    /*
    * Checkbox
    * */
    @BindView(R.id.playlist_dialog_view_checkbox)
    lateinit var mExpandCheckbox: CheckBox
    @BindView(R.id.playlist_dialog_sort_checkbox)
    lateinit var mSortOrderCheckbox: CheckBox

    private val mRadioSortListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        when (checkedId) {
            R.id.playlist_dialog_sort_name -> {
                PlayerConfig.instance.playlistSort = CursorTool.FIELD_NAME
                invokeSortCallback(CursorTool.FIELD_NAME)
            }
            R.id.playlist_dialog_sort_value -> {
                PlayerConfig.instance.playlistSort = CursorTool.FIELD_DURATION
                invokeSortCallback(CursorTool.FIELD_DURATION)
            }
            R.id.playlist_dialog_sort_date -> {
                PlayerConfig.instance.playlistSort = CursorTool.FIELD_MODIFY
                invokeSortCallback(CursorTool.FIELD_MODIFY)
            }
            R.id.playlist_dialog_sort_artist -> {
                PlayerConfig.instance.playlistSort = CursorTool.FIELD_ARTIST
                invokeSortCallback(CursorTool.FIELD_ARTIST)
            }
            R.id.playlist_dialog_sort_albums -> {
                PlayerConfig.instance.playlistSort = CursorTool.FIELD_ALBUMS
                invokeSortCallback(CursorTool.FIELD_ALBUMS)
            }
        }
    }


    interface OnViewEvent {
        fun onSort(value: String)
        fun onSortOrder(value: String)
        fun onView(isValue: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        init()
    }

    @OnCheckedChanged(R.id.playlist_dialog_view_checkbox)
    protected fun onCheckedExpandCheckBox(buttonView: CompoundButton, isChecked: Boolean) {
        PreferenceTool.instance.playlistViewExpand = isChecked
        invokeViewCallback(isChecked)
    }

    @OnCheckedChanged(R.id.playlist_dialog_sort_checkbox)
    protected fun onCheckedSortCheckBox(buttonView: CompoundButton, isChecked: Boolean) {
        val sortOrder = if (isChecked) CursorTool.SORT_ORDER_ASC else CursorTool.SORT_ORDER_DESC
        PlayerConfig.instance.playlistSortOrder = sortOrder
        invokeSortOrderCallback(sortOrder)
    }

    private fun init() {
        setContentView(R.layout.dialog_playlist_settings)
        ButterKnife.bind(this)
        setRadioButtons()

        mExpandCheckbox!!.isChecked = PreferenceTool.instance.playlistViewExpand
        mSortOrderCheckbox!!.isChecked = false
        val sortOrder = PlayerConfig.instance.playlistSortOrder
        if (sortOrder == CursorTool.SORT_ORDER_ASC) {
            mSortOrderCheckbox!!.isChecked = true
        }
    }

    private fun setRadioButtons() {
        val sortType = PlayerConfig.instance.playlistSort

        // Set sort
        when (sortType) {
            CursorTool.FIELD_NAME -> mRadioButtonName!!.isChecked = true
            CursorTool.FIELD_DURATION -> mRadioButtonValue!!.isChecked = true
            CursorTool.FIELD_MODIFY -> mRadioButtonDate!!.isChecked = true
            CursorTool.FIELD_ARTIST -> mRadioButtonArtist!!.isChecked = true
            CursorTool.FIELD_ALBUMS -> mRadioButtonAlbums!!.isChecked = true
        }

        mRadioSort!!.setOnCheckedChangeListener(mRadioSortListener)
    }

    fun setOnViewEvent(onRadioButtonsCheck: OnViewEvent) {
        mOnViewEvent = onRadioButtonsCheck
    }

    private fun invokeSortOrderCallback(value: String) {
        if (mOnViewEvent != null) {
            mOnViewEvent!!.onSortOrder(value)
        }
    }

    private fun invokeViewCallback(value: Boolean) {
        if (mOnViewEvent != null) {
            mOnViewEvent!!.onView(value)
        }
    }

    private fun invokeSortCallback(value: String) {
        if (mOnViewEvent != null) {
            mOnViewEvent!!.onSort(value)
        }
    }

}
