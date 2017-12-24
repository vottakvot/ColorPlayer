package ru.testsimpleapps.coloraudioplayer.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.managers.tools.CursorTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool;

public class PlaylistSettingsDialog extends BaseDialog {


    public interface OnViewEvent {
        void onSort(String value);
        void onSortOrder(String value);
        void onView(boolean isValue);
    }

    private final Context mContext;
    private OnViewEvent mOnViewEvent;

    /*
    * Radio groups
    * */
    @BindView(R.id.playlist_dialog_sort)
    protected RadioGroup mRadioSort;

    /*
    * Radio buttons
    * */
    @BindView(R.id.playlist_dialog_sort_name)
    protected RadioButton mRadioButtonName;
    @BindView(R.id.playlist_dialog_sort_date)
    protected RadioButton mRadioButtonDate;
    @BindView(R.id.playlist_dialog_sort_value)
    protected RadioButton mRadioButtonValue;
    @BindView(R.id.playlist_dialog_sort_artist)
    protected RadioButton mRadioButtonArtist;
    @BindView(R.id.playlist_dialog_sort_albums)
    protected RadioButton mRadioButtonAlbums;

    /*
    * Checkbox
    * */
    @BindView(R.id.playlist_dialog_view_checkbox)
    protected CheckBox mExpandCheckbox;
    @BindView(R.id.playlist_dialog_sort_checkbox)
    protected CheckBox mSortOrderCheckbox;


    public PlaylistSettingsDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @OnCheckedChanged(R.id.playlist_dialog_view_checkbox)
    protected void onCheckedExpandCheckBox(CompoundButton buttonView, boolean isChecked) {
        PreferenceTool.getInstance().setPlaylistViewExpand(isChecked);
        invokeViewCallback(isChecked);
    }

    @OnCheckedChanged(R.id.playlist_dialog_sort_checkbox)
    protected void onCheckedSortCheckBox(CompoundButton buttonView, boolean isChecked) {
        final String sortOrder = isChecked? CursorTool.SORT_ORDER_ASC : CursorTool.SORT_ORDER_DESC;
        PlayerConfig.getInstance().setPlaylistSortOrder(sortOrder);
        invokeSortOrderCallback(sortOrder);
    }

    private void init() {
        setContentView(R.layout.dialog_playlist_settings);
        ButterKnife.bind(this);
        setRadioButtons();

        mExpandCheckbox.setChecked(PreferenceTool.getInstance().getPlaylistViewExpand());
        mSortOrderCheckbox.setChecked(false);
        final String sortOrder = PlayerConfig.getInstance().getPlaylistSortOrder();
        if (sortOrder.equals(CursorTool.SORT_ORDER_ASC)) {
            mSortOrderCheckbox.setChecked(true);
        }
    }

    private void setRadioButtons() {
        final String sortType = PlayerConfig.getInstance().getPlaylistSort();

        // Set sort
        switch (sortType) {
            case CursorTool.FIELD_NAME:
                mRadioButtonName.setChecked(true);
                break;
            case CursorTool.FIELD_DURATION:
                mRadioButtonValue.setChecked(true);
                break;
            case CursorTool.FIELD_MODIFY:
                mRadioButtonDate.setChecked(true);
                break;
            case CursorTool.FIELD_ARTIST:
                mRadioButtonArtist.setChecked(true);
                break;
            case CursorTool.FIELD_ALBUMS:
                mRadioButtonAlbums.setChecked(true);
                break;
        }

        mRadioSort.setOnCheckedChangeListener(mRadioSortListener);
    }

    public void setOnViewEvent(OnViewEvent onRadioButtonsCheck) {
        mOnViewEvent = onRadioButtonsCheck;
    }

    private void invokeSortOrderCallback(final String value) {
        if (mOnViewEvent != null) {
            mOnViewEvent.onSortOrder(value);
        }
    }

    private void invokeViewCallback(final boolean value) {
        if (mOnViewEvent != null) {
            mOnViewEvent.onView(value);
        }
    }

    private void invokeSortCallback(final String value) {
        if (mOnViewEvent != null) {
            mOnViewEvent.onSort(value);
        }
    }

    private RadioGroup.OnCheckedChangeListener mRadioSortListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.playlist_dialog_sort_name:
                    PlayerConfig.getInstance().setPlaylistSort(CursorTool.FIELD_NAME);
                    invokeSortCallback(CursorTool.FIELD_NAME);
                    break;
                case R.id.playlist_dialog_sort_value:
                    PlayerConfig.getInstance().setPlaylistSort(CursorTool.FIELD_DURATION);
                    invokeSortCallback(CursorTool.FIELD_DURATION);
                    break;
                case R.id.playlist_dialog_sort_date:
                    PlayerConfig.getInstance().setPlaylistSort(CursorTool.FIELD_MODIFY);
                    invokeSortCallback(CursorTool.FIELD_MODIFY);
                    break;
                case R.id.playlist_dialog_sort_artist:
                    PlayerConfig.getInstance().setPlaylistSort(CursorTool.FIELD_ARTIST);
                    invokeSortCallback(CursorTool.FIELD_ARTIST);
                    break;
                case R.id.playlist_dialog_sort_albums:
                    PlayerConfig.getInstance().setPlaylistSort(CursorTool.FIELD_ALBUMS);
                    invokeSortCallback(CursorTool.FIELD_ALBUMS);
                    break;
            }
        }
    };

}
