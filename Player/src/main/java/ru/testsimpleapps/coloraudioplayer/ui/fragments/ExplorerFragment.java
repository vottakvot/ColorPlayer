package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ContainerData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ItemData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.MediaExplorerManager;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.BaseAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.ExplorerFilesAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.ExplorerFolderAdapter;


public class ExplorerFragment extends BaseFragment implements BaseAdapter.OnItemClickListener {

    public static final String TAG = ExplorerFragment.class.getSimpleName();

    protected Unbinder mUnbinder;
    @BindView(R.id.explorer_list)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.explorer_back)
    protected ImageButton mBackButton;
    @BindView(R.id.explorer_add)
    protected ImageButton mAddButton;

    private ExplorerFilesAdapter mExplorerFilesAdapter;
    private ExplorerFolderAdapter mExplorerFolderAdapter;
    private List<FolderData> mFolderData;
    private Parcelable mListState;

    public static ExplorerFragment newInstance() {
        ExplorerFragment fragment = new ExplorerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_explorer, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void init() {
        mExplorerFilesAdapter = new ExplorerFilesAdapter(getContext());
        mExplorerFolderAdapter = new ExplorerFolderAdapter(getContext());

        MediaExplorerManager.getInstance().findMedia();
        mFolderData = MediaExplorerManager.getInstance().getAlbums();
        mExplorerFolderAdapter.setItems(mFolderData);
        mExplorerFolderAdapter.setOnItemClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mExplorerFolderAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        mListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        final FolderData folderData = mExplorerFolderAdapter.getItem(position);
        final ContainerData<ItemData> itemData = folderData.getContainerItemData();
        mExplorerFilesAdapter.setItems(itemData.getList());
        mRecyclerView.setAdapter(mExplorerFilesAdapter);
    }

    @OnClick(R.id.explorer_back)
    protected void backButtonClick() {
        mRecyclerView.setAdapter(mExplorerFolderAdapter);
        mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
    }

}
