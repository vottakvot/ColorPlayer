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
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData;


public class ExplorerFolderAdapter extends BaseAdapter<FolderData> {

    private final Context mContext;

    public ExplorerFolderAdapter(@NonNull Context context) {
        super();
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.explorer_folder, viewGroup, false);
        return new ViewHolderItem(viewItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final ViewHolderItem mViewHolder = (ViewHolderItem) viewHolder;
        final FolderData folderData = getItem(i);
        mViewHolder.mCheckFolder.setChecked(folderData.isChecked());
        mViewHolder.mCountInFolder.setText(String.valueOf(folderData.size()));
        mViewHolder.mNameFolder.setText(folderData.getName());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected class ViewHolderItem extends RecyclerView.ViewHolder {
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
    }
}