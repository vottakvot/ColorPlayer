package ru.testsimpleapps.coloraudioplayer.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ConfigData;
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool;

public class ExplorerSettingsDialog extends BaseDialog {

    public interface OnViewEvent {
        void onGroup(int value);
        void onSort(int value);
        void onSortOrder(int value);
    }

    private final Context mContext;
    private OnViewEvent mOnViewEvent;

    /*
    * Radio groups
    * */
    @BindView(R.id.explorer_dialog_groups)
    protected RadioGroup mRadioGroups;
    @BindView(R.id.explorer_dialog_sort)
    protected RadioGroup mRadioSort;

    /*
    * Radio buttons
    * */
    @BindView(R.id.explorer_dialog_groups_albums)
    protected RadioButton mRadioButtonAlbums;
    @BindView(R.id.explorer_dialog_groups_artists)
    protected RadioButton mRadioButtonArtists;
    @BindView(R.id.explorer_dialog_groups_folders)
    protected RadioButton mRadioButtonFolders;
    @BindView(R.id.explorer_dialog_sort_name)
    protected RadioButton mRadioButtonName;
    @BindView(R.id.explorer_dialog_sort_date)
    protected RadioButton mRadioButtonDate;
    @BindView(R.id.explorer_dialog_sort_value)
    protected RadioButton mRadioButtonValue;

    /*
    * Checkbox
    * */
    @BindView(R.id.explorer_dialog_sort_checkbox)
    protected CheckBox mSortOrderCheckbox;

    public ExplorerSettingsDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_explorer_settings);
        ButterKnife.bind(this);
        setRadioButtons();
        mSortOrderCheckbox.setChecked(false);
        final int sortOrder = PreferenceTool.getInstance().getExplorerSortOrder();
        if (sortOrder == ConfigData.SORT_ORDER_ASC) {
            mSortOrderCheckbox.setChecked(false);
        }

        mSortOrderCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final int sortOrder = isChecked? ConfigData.SORT_ORDER_ASC : ConfigData.SORT_ORDER_DESC;
                PreferenceTool.getInstance().setExplorerSortOrder(sortOrder);
                if (mOnViewEvent != null) {
                    mOnViewEvent.onSortOrder(sortOrder);
                }
            }
        });
    }

    private void setRadioButtons() {
        final int groupType = PreferenceTool.getInstance().getExplorerGroupType();
        final int sortType = PreferenceTool.getInstance().getExplorerSortType();

        // Set groups
        switch (groupType) {
            case ConfigData.GROUP_TYPE_ALBUMS:
                mRadioButtonAlbums.setChecked(true);
                break;
            case ConfigData.GROUP_TYPE_ARTISTS:
                mRadioButtonArtists.setChecked(true);
                break;
            case ConfigData.GROUP_TYPE_FOLDERS:
                mRadioButtonFolders.setChecked(true);
                break;
        }

        // Set sort
        switch (sortType) {
            case ConfigData.SORT_TYPE_NAME:
                mRadioButtonName.setChecked(true);
                break;
            case ConfigData.SORT_TYPE_DATE:
                mRadioButtonDate.setChecked(true);
                break;
            case ConfigData.SORT_TYPE_VALUE:
                mRadioButtonValue.setChecked(true);
                break;
        }

        mRadioGroups.setOnCheckedChangeListener(mRadioGroupsListener);
        mRadioSort.setOnCheckedChangeListener(mRadioSortListener);
    }

    public void setOnViewEvent(OnViewEvent onRadioButtonsCheck) {
        mOnViewEvent = onRadioButtonsCheck;
    }

    private RadioGroup.OnCheckedChangeListener mRadioGroupsListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.explorer_dialog_groups_albums:
                    PreferenceTool.getInstance().setExplorerGroupType(ConfigData.GROUP_TYPE_ALBUMS);
                    invokeCallback(ConfigData.GROUP_TYPE_ALBUMS);
                    break;
                case R.id.explorer_dialog_groups_artists:
                    PreferenceTool.getInstance().setExplorerGroupType(ConfigData.GROUP_TYPE_ARTISTS);
                    invokeCallback(ConfigData.GROUP_TYPE_ARTISTS);
                    break;
                case R.id.explorer_dialog_groups_folders:
                    PreferenceTool.getInstance().setExplorerGroupType(ConfigData.GROUP_TYPE_FOLDERS);
                    invokeCallback(ConfigData.GROUP_TYPE_FOLDERS);
                    break;
            }
        }

        private void invokeCallback(final int value) {
            if (mOnViewEvent != null) {
                mOnViewEvent.onGroup(value);
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener mRadioSortListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.explorer_dialog_sort_name:
                    PreferenceTool.getInstance().setExplorerSortType(ConfigData.SORT_TYPE_NAME);
                    invokeCallback(ConfigData.SORT_TYPE_NAME);
                    break;
                case R.id.explorer_dialog_sort_value:
                    PreferenceTool.getInstance().setExplorerSortType(ConfigData.SORT_TYPE_VALUE);
                    invokeCallback(ConfigData.SORT_TYPE_VALUE);
                    break;
                case R.id.explorer_dialog_sort_date:
                    PreferenceTool.getInstance().setExplorerSortType(ConfigData.SORT_TYPE_DATE);
                    invokeCallback(ConfigData.SORT_TYPE_DATE);
                    break;
            }
        }

        private void invokeCallback(final int value) {
            if (mOnViewEvent != null) {
                mOnViewEvent.onSort(value);
            }
        }
    };

}
