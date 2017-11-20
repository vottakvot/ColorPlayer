package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.MediaExplorerManager;
import ru.testsimpleapps.coloraudioplayer.managers.tools.TimeTool;


public class ExplorerFolderAdapter extends BaseAdapter<FolderData> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final Context mContext;

    public ExplorerFolderAdapter(@NonNull Context context) {
        super();
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE_HEADER: {
                final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.explorer_header_folders, viewGroup, false);
                return new ViewHolderHeader(viewItem);
            }

            case TYPE_ITEM: {
                final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.explorer_folder, viewGroup, false);
                return new ViewHolderItem(viewItem);
            }

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolderItem) {
            final ViewHolderItem mViewHolder = (ViewHolderItem) viewHolder;
            final FolderData folderData = getItem(i);
            mViewHolder.mCheckFolder.setTag(ViewHolderItem.TAG_CHECK, i);
            mViewHolder.mCheckFolder.setChecked(folderData.isChecked());
            mViewHolder.mCountInFolder.setText(String.valueOf(folderData.size()));
            mViewHolder.mNameFolder.setText(folderData.getName());
        } else if (viewHolder instanceof ViewHolderHeader) {
            final ViewHolderHeader mViewHolder = (ViewHolderHeader) viewHolder;
            mViewHolder.mCountFolders.setText(String.valueOf(getItemCount() - 1));
            mViewHolder.mCountTracks.setText(String.valueOf(MediaExplorerManager.getInstance().getTotalTracks()));
            mViewHolder.mTotalTime.setText(TimeTool.getDuration(MediaExplorerManager.getInstance().getTotalTime()));
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public FolderData getItem(int index) {
        return super.getItem(index - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    protected class ViewHolderItem extends RecyclerView.ViewHolder {

        public static final int TAG_CHECK = 10 << 24;

        @BindView(R.id.explorer_folder_count) TextView mCountInFolder;
        @BindView(R.id.explorer_folder_name) TextView mNameFolder;
        @BindView(R.id.explorer_folder_check) CheckBox mCheckFolder;

        public ViewHolderItem(final View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, getLayoutPosition());
                    }
                }
            });
        }

        @OnCheckedChanged(R.id.explorer_folder_check)
        protected void onChecked(final CheckBox checkBox) {
            final int position = (int) checkBox.getTag(TAG_CHECK);
            final FolderData folderData = getItem(position);
            folderData.setChecked(checkBox.isChecked());
        }

    }

    protected class ViewHolderHeader extends RecyclerView.ViewHolder {
        @BindView(R.id.explorer_header_folder_count_folders_value) TextView mCountFolders;
        @BindView(R.id.explorer_header_folder_count_tracks_value) TextView mCountTracks;
        @BindView(R.id.explorer_header_folder_total_time_value) TextView mTotalTime;

        public ViewHolderHeader(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}