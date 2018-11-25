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
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ConfigData
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool

class ExplorerSettingsDialog(private val mContext: Context) : BaseDialog(mContext) {
    private var mOnViewEvent: OnViewEvent? = null

    /*
    * Radio groups
    * */
    @BindView(R.id.explorer_dialog_groups)
     lateinit var mRadioGroups: RadioGroup
    @BindView(R.id.explorer_dialog_sort)
     lateinit var mRadioSort: RadioGroup

    /*
    * Radio buttons
    * */
    @BindView(R.id.explorer_dialog_groups_albums)
     lateinit var mRadioButtonAlbums: RadioButton
    @BindView(R.id.explorer_dialog_groups_artists)
     lateinit var mRadioButtonArtists: RadioButton
    @BindView(R.id.explorer_dialog_groups_folders)
     lateinit var mRadioButtonFolders: RadioButton
    @BindView(R.id.explorer_dialog_sort_name)
     lateinit var mRadioButtonName: RadioButton
    @BindView(R.id.explorer_dialog_sort_date)
     lateinit var mRadioButtonDate: RadioButton
    @BindView(R.id.explorer_dialog_sort_value)
     lateinit var mRadioButtonValue: RadioButton

    /*
    * Checkbox
    * */
    @BindView(R.id.explorer_dialog_sort_checkbox)
    lateinit var mSortOrderCheckbox: CheckBox

    private val mRadioGroupsListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        when (checkedId) {
            R.id.explorer_dialog_groups_albums -> {
                PreferenceTool.instance.explorerGroupType = ConfigData.GROUP_TYPE_ALBUMS
                invokeGroupCallback(ConfigData.GROUP_TYPE_ALBUMS)
            }
            R.id.explorer_dialog_groups_artists -> {
                PreferenceTool.instance.explorerGroupType = ConfigData.GROUP_TYPE_ARTISTS
                invokeGroupCallback(ConfigData.GROUP_TYPE_ARTISTS)
            }
            R.id.explorer_dialog_groups_folders -> {
                PreferenceTool.instance.explorerGroupType = ConfigData.GROUP_TYPE_FOLDERS
                invokeGroupCallback(ConfigData.GROUP_TYPE_FOLDERS)
            }
        }
    }

    private val mRadioSortListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        when (checkedId) {
            R.id.explorer_dialog_sort_name -> {
                PreferenceTool.instance.explorerSortType = ConfigData.SORT_TYPE_NAME
                invokeSortCallback(ConfigData.SORT_TYPE_NAME)
            }
            R.id.explorer_dialog_sort_value -> {
                PreferenceTool.instance.explorerSortType = ConfigData.SORT_TYPE_VALUE
                invokeSortCallback(ConfigData.SORT_TYPE_VALUE)
            }
            R.id.explorer_dialog_sort_date -> {
                PreferenceTool.instance.explorerSortType = ConfigData.SORT_TYPE_DATE
                invokeSortCallback(ConfigData.SORT_TYPE_DATE)
            }
        }
    }

    interface OnViewEvent {
        fun onGroup(value: Int)
        fun onSort(value: Int)
        fun onSortOrder(value: Int)
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        init()
    }

    @OnCheckedChanged(R.id.explorer_dialog_sort_checkbox)
    protected fun onCheckedSortCheckBox(buttonView: CompoundButton, isChecked: Boolean) {
        val sortOrder = if (isChecked) ConfigData.SORT_ORDER_ASC else ConfigData.SORT_ORDER_DESC
        PreferenceTool.instance.explorerSortOrder = sortOrder
        invokeSortOrderCallback(sortOrder)
    }

    private fun init() {
        setContentView(R.layout.dialog_explorer_settings)
        ButterKnife.bind(this)
        setRadioButtons()
        mSortOrderCheckbox!!.isChecked = false
        val sortOrder = PreferenceTool.instance.explorerSortOrder
        if (sortOrder == ConfigData.SORT_ORDER_ASC) {
            mSortOrderCheckbox!!.isChecked = false
        }
    }

    private fun setRadioButtons() {
        val groupType = PreferenceTool.instance.explorerGroupType
        val sortType = PreferenceTool.instance.explorerSortType

        // Set groups
        when (groupType) {
            ConfigData.GROUP_TYPE_ALBUMS -> mRadioButtonAlbums!!.isChecked = true
            ConfigData.GROUP_TYPE_ARTISTS -> mRadioButtonArtists!!.isChecked = true
            ConfigData.GROUP_TYPE_FOLDERS -> mRadioButtonFolders!!.isChecked = true
        }

        // Set sort
        when (sortType) {
            ConfigData.SORT_TYPE_NAME -> mRadioButtonName!!.isChecked = true
            ConfigData.SORT_TYPE_DATE -> mRadioButtonDate!!.isChecked = true
            ConfigData.SORT_TYPE_VALUE -> mRadioButtonValue!!.isChecked = true
        }

        mRadioGroups!!.setOnCheckedChangeListener(mRadioGroupsListener)
        mRadioSort!!.setOnCheckedChangeListener(mRadioSortListener)
    }

    fun setOnViewEvent(onRadioButtonsCheck: OnViewEvent) {
        mOnViewEvent = onRadioButtonsCheck
    }

    private fun invokeSortOrderCallback(value: Int) {
        if (mOnViewEvent != null) {
            mOnViewEvent!!.onSortOrder(value)
        }
    }

    private fun invokeGroupCallback(value: Int) {
        if (mOnViewEvent != null) {
            mOnViewEvent!!.onGroup(value)
        }
    }

    private fun invokeSortCallback(value: Int) {
        if (mOnViewEvent != null) {
            mOnViewEvent!!.onSort(value)
        }
    }

}
