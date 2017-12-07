package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ConfigData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ContainerData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ItemData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.FoldersComparator;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.ItemsComparator;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.MediaExplorerManager;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory;
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.BaseListAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.ExplorerFilesAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.ExplorerFolderAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.animation.OnTranslationAnimation;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.ExplorerDialog;


public class ExplorerFragment extends BaseFragment implements BaseListAdapter.OnItemClickListener,
        MediaExplorerManager.OnDataReady, BaseListAdapter.OnItemCheckListener, ExplorerDialog.OnViewEvent {

    public static final String TAG = ExplorerFragment.class.getSimpleName();
    private static final String TAG_ADD_PANEL = "TAG_ADD_PANEL";
    private static final String TAG_DIALOG = "TAG_DIALOG";
    private static final String TAG_TYPE_ADAPTER = "TAG_TYPE_ADAPTER";
    private static final String TAG_FOLDER_STATE_ADAPTER = "TAG_FOLDER_STATE_ADAPTER";
    private static final String TAG_FOLDER_POSITION = "TAG_FOLDER_POSITION";
    private static final String TAG_FOLDER_CONTENT = "TAG_FOLDER_CONTENT";
    private static final String TAG_PREVIOUS_STATE_ADAPTER = "TAG_PREVIOUS_STATE_ADAPTER";

    protected Unbinder mUnbinder;

    @BindView(R.id.explorer_list)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.explorer_progress)
    protected ProgressBar mProgressBar;

    @BindView(R.id.explorer_additional_layout)
    protected FrameLayout mAdditionalPanel;
    @BindView(R.id.explorer_additional_files_layout)
    protected ConstraintLayout mAdditionalFilesPanel;
    @BindView(R.id.explorer_additional_folders_layout)
    protected ConstraintLayout mAdditionalFoldersPanel;

    /*
    * Buttons for files context
    * */
    @BindView(R.id.explorer_files_back)
    protected ImageButton mBackFilesButton;
    @BindView(R.id.explorer_files_add)
    protected ImageButton mAddFilesButton;

    /*
    * Buttons for folders context
    * */
    @BindView(R.id.explorer_folders_settings)
    protected ImageButton mSettingsFilesButton;
    @BindView(R.id.explorer_folders_add)
    protected ImageButton mAddFoldersButton;

    private ExplorerFilesAdapter mExplorerFilesAdapter;
    private ExplorerFolderAdapter mExplorerFolderAdapter;
    private Parcelable mFolderStateAdapter;
    private int mFolderPosition = 1;
    private ExplorerDialog mExplorerDialog;
    private OnTranslationAnimation mOnTranslationAnimation;


    public static ExplorerFragment newInstance() {
        ExplorerFragment fragment = new ExplorerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_explorer, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init(savedInstanceState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MediaExplorerManager.getInstance().removeFindCallback();
        mUnbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG_ADD_PANEL, mAdditionalPanel.getVisibility());
        outState.putInt(TAG_FOLDER_POSITION, mFolderPosition);
        outState.putBoolean(TAG_TYPE_ADAPTER, mRecyclerView.getAdapter() instanceof ExplorerFolderAdapter);
        outState.putBoolean(TAG_DIALOG, mExplorerDialog.isShowing());

        outState.putParcelable(TAG_FOLDER_STATE_ADAPTER, mFolderStateAdapter);
        outState.putParcelable(TAG_PREVIOUS_STATE_ADAPTER, mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putSerializable(TAG_FOLDER_CONTENT, (ArrayList<FolderData>) mExplorerFolderAdapter.getItemList());
    }

    @Override
    public void onItemClick(View view, int position) {
        typePanelVisibility(false);
        mFolderPosition = position;
        mFolderStateAdapter = mRecyclerView.getLayoutManager().onSaveInstanceState();
        final FolderData folderData = mExplorerFolderAdapter.getItem(position);
        final ContainerData<ItemData> itemData = folderData.getContainerItemData();
        mExplorerFilesAdapter.setItems(sortFiles(PreferenceTool.getInstance().getExplorerSortType(), itemData.getList()));
        mRecyclerView.setAdapter(mExplorerFilesAdapter);
        mRecyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onItemCheck(View view, int position) {
        mOnTranslationAnimation.animate(false);
    }

    @OnClick(R.id.explorer_files_back)
    protected void backButtonClick() {
        restoreAdapter();
    }

    @OnClick(R.id.explorer_folders_add)
    protected void addFoldersButtonClick() {
        final List<Long> items = new ArrayList<>();
        for (FolderData folder : mExplorerFolderAdapter.getItemList()) {
            if (folder.isChecked()) {
                for (ItemData file : folder.getContainerItemData().getList()) {
                    items.add(file.getId());
                    file.setChecked(false);
                }
            }


            folder.setChecked(false);
        }

        mRecyclerView.getAdapter().notifyDataSetChanged();
        if (items.isEmpty()) {
            showToast(R.string.explorer_add_to_playlist_nothing);
        } else {
            showToast(getString(R.string.explorer_add_to_playlist) + items.size());
            CursorFactory.getInstance().add(items);
        }
    }

    @OnClick(R.id.explorer_files_add)
    protected void addFilesButtonClick() {
        final List<Long> items = new ArrayList<>();
        for (ItemData file : mExplorerFilesAdapter.getItemList()) {
            if (file.isChecked()) {
                items.add(file.getId());
                file.setChecked(false);
            }
        }

        mRecyclerView.getAdapter().notifyDataSetChanged();
        if (items.isEmpty()) {
            showToast(R.string.explorer_add_to_playlist_nothing);
        } else {
            showToast(getString(R.string.explorer_add_to_playlist) + items.size());
            CursorFactory.getInstance().add(items);
        }
    }

    @OnClick(R.id.explorer_folders_settings)
    protected void settingsFoldersButtonClick() {
        mExplorerDialog.show();
    }

    @OnLongClick({R.id.explorer_files_add, R.id.explorer_folders_add})
    protected boolean onLongAddClick() {
        if (mRecyclerView.getAdapter() instanceof ExplorerFolderAdapter) {
            for (FolderData item : mExplorerFolderAdapter.getItemList()) {
                item.setChecked(true);
            }
        } else {
            for (ItemData item : mExplorerFilesAdapter.getItemList()) {
                item.setChecked(true);
            }
        }

        mRecyclerView.getAdapter().notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onBackPressed() {
        return restoreAdapter();
    }

    @Override
    public void onSuccess() {
        showToast(R.string.explorer_find_media_ok);
        mProgressBar.setVisibility(View.INVISIBLE);
        final List<FolderData> folderDataList = groupFolders(PreferenceTool.getInstance().getExplorerGroupType());
        sortFolders(PreferenceTool.getInstance().getExplorerSortType(), folderDataList);
        mExplorerFolderAdapter.setItems(folderDataList);
        mRecyclerView.setAdapter(mExplorerFolderAdapter);
        mRecyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onError() {
        showToast(R.string.explorer_find_media_error);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onGroup(final int value) {
        mExplorerFolderAdapter.setItems(groupFolders(value));
        mRecyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onSort(final int value) {
        sortFolders(value, mExplorerFolderAdapter.getItemList());
        mExplorerFolderAdapter.notifyDataSetChanged();
        mRecyclerView.scheduleLayoutAnimation();
    }

    private List<FolderData> groupFolders(final int value) {
        switch (value) {
            case ConfigData.GROUP_TYPE_ALBUMS:
                return MediaExplorerManager.getInstance().getAlbums();
            case ConfigData.GROUP_TYPE_FOLDERS:
                return MediaExplorerManager.getInstance().getFolders();
            case ConfigData.GROUP_TYPE_ARTISTS:
                return MediaExplorerManager.getInstance().getArtists();
            default:
                return MediaExplorerManager.getInstance().getFolders();
        }
    }

    private List<FolderData> sortFolders(final int value, final List<FolderData> list) {
        switch (value) {
            case ConfigData.SORT_TYPE_AZ:
                Collections.sort(list, new FoldersComparator.NameAz());
                break;
            case ConfigData.SORT_TYPE_ZA:
                Collections.sort(list, new FoldersComparator.NameZa());
                break;
            case ConfigData.SORT_TYPE_VALUE:
                Collections.sort(list, new FoldersComparator.Size());
                break;
        }

        return list;
    }

    private List<ItemData> sortFiles(final int value, final List<ItemData> list) {
        switch (value) {
            case ConfigData.SORT_TYPE_AZ:
                Collections.sort(list, new ItemsComparator.NameAz());
                break;
            case ConfigData.SORT_TYPE_ZA:
                Collections.sort(list, new ItemsComparator.NameZa());
                break;
            case ConfigData.SORT_TYPE_VALUE:
                Collections.sort(list, new ItemsComparator.Duration());
                break;
            case ConfigData.SORT_TYPE_DATE:
                Collections.sort(list, new ItemsComparator.DataAdded());
                break;
        }

        return list;
    }

    private void init(final Bundle savedInstanceState) {
        MediaExplorerManager.getInstance().setFindCallback(this);

        typePanelVisibility(true);
        mOnTranslationAnimation = new OnTranslationAnimation(mAdditionalPanel, OnTranslationAnimation.DEFAULT_DURATION);

        mExplorerDialog = new ExplorerDialog(getContext());
        mExplorerDialog.setOnViewEvent(this);
        mExplorerFilesAdapter = new ExplorerFilesAdapter(getContext());
        mExplorerFilesAdapter.setOnItemCheckListener(this);
        mExplorerFolderAdapter = new ExplorerFolderAdapter(getContext());
        mExplorerFolderAdapter.setOnItemClickListener(this);
        mExplorerFolderAdapter.setOnItemCheckListener(this);

        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.recycle_layout_animation);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setLayoutAnimation(animation);
        mRecyclerView.addOnScrollListener(new OnScrollRecycleViewListener());

        restoreStates(savedInstanceState);
    }

    private void restoreStates(final Bundle savedInstanceState) {
        // Check previous states
        if (savedInstanceState == null) {
            MediaExplorerManager.getInstance().findMediaAsync();
        } else {
            // State for adapter back press
            mFolderStateAdapter = savedInstanceState.getParcelable(TAG_FOLDER_STATE_ADAPTER);
            // Panel state
            mAdditionalPanel.setVisibility(savedInstanceState.getInt(TAG_ADD_PANEL));
            // Restore folder adapter every time
            final ArrayList<FolderData> folderDataList = (ArrayList<FolderData>) savedInstanceState.getSerializable(TAG_FOLDER_CONTENT);
            if (folderDataList != null) {
                mExplorerFolderAdapter.setItems(folderDataList);
            }
            // Restore files adapter every time
            mFolderPosition = savedInstanceState.getInt(TAG_FOLDER_POSITION);
            final FolderData folderData = mExplorerFolderAdapter.getItem(mFolderPosition);
            if (folderData != null) {
                mExplorerFilesAdapter.setItems(folderData.getContainerItemData().getList());
            }

            // Previous adapter
            if (savedInstanceState.getBoolean(TAG_TYPE_ADAPTER)) {
                mRecyclerView.setAdapter(mExplorerFolderAdapter);
                typePanelVisibility(true);
            } else {
                mRecyclerView.setAdapter(mExplorerFilesAdapter);
                typePanelVisibility(false);
            }

            // Previous adapter state
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(TAG_PREVIOUS_STATE_ADAPTER));
            mRecyclerView.scheduleLayoutAnimation();

            // Check settings dialog
            if (savedInstanceState.getBoolean(TAG_DIALOG)) {
                mExplorerDialog.show();
            }
        }

        // Show progress for async data search
        if (MediaExplorerManager.getInstance().isProcessing()) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void typePanelVisibility(final boolean isFolder) {
        if (isFolder) {
            mAdditionalFoldersPanel.setVisibility(View.VISIBLE);
            mAdditionalFilesPanel.setVisibility(View.GONE);
        } else {
            mAdditionalFoldersPanel.setVisibility(View.GONE);
            mAdditionalFilesPanel.setVisibility(View.VISIBLE);
        }
    }

    private boolean restoreAdapter() {
        if (mRecyclerView.getAdapter() instanceof ExplorerFilesAdapter) {
            typePanelVisibility(true);
            mRecyclerView.setAdapter(mExplorerFolderAdapter);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mFolderStateAdapter);
            mRecyclerView.scheduleLayoutAnimation();
            return true;
        }

        return false;
    }

    private class OnScrollRecycleViewListener extends RecyclerView.OnScrollListener {

        private int mState = -1;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            mState = newState;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mState == RecyclerView.SCROLL_STATE_DRAGGING && !mOnTranslationAnimation.isAnimating()) {
                mOnTranslationAnimation.animate(dy < 0);
            }
        }
    }

}
