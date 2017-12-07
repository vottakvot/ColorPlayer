package ru.testsimpleapps.coloraudioplayer.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ConfigData;
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool;

public class ExplorerDialog extends BaseDialog {

    public interface OnViewEvent {
        void onGroup(int value);
        void onSort(int value);
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
    @BindView(R.id.explorer_dialog_sort_name_az)
    protected RadioButton mRadioButtonAz;
    @BindView(R.id.explorer_dialog_sort_name_za)
    protected RadioButton mRadioButtonZa;
    @BindView(R.id.explorer_dialog_sort_date)
    protected RadioButton mRadioButtonDate;
    @BindView(R.id.explorer_dialog_sort_value)
    protected RadioButton mRadioButtonValue;

    public ExplorerDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_explorer);
        ButterKnife.bind(this);
        setRadioButtons();
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
            case ConfigData.SORT_TYPE_AZ:
                mRadioButtonAz.setChecked(true);
                break;
            case ConfigData.SORT_TYPE_ZA:
                mRadioButtonZa.setChecked(true);
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
                case R.id.explorer_dialog_sort_name_az:
                    PreferenceTool.getInstance().setExplorerSortType(ConfigData.SORT_TYPE_AZ);
                    invokeCallback(ConfigData.SORT_TYPE_AZ);
                    break;
                case R.id.explorer_dialog_sort_name_za:
                    PreferenceTool.getInstance().setExplorerSortType(ConfigData.SORT_TYPE_ZA);
                    invokeCallback(ConfigData.SORT_TYPE_ZA);
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
