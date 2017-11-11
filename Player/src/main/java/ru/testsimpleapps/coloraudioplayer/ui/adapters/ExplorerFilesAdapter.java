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
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ItemData;


public class ExplorerFilesAdapter extends BaseAdapter<ItemData> {

    private final Context mContext;

    public ExplorerFilesAdapter(@NonNull Context context) {
        super();
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.explorer_file, viewGroup, false);
        return new ExplorerFilesAdapter.ViewHolderItem(viewItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final ExplorerFilesAdapter.ViewHolderItem mViewHolder = (ExplorerFilesAdapter.ViewHolderItem) viewHolder;
        final ItemData itemData = getItem(i);
        mViewHolder.mDurationFile.setText(String.valueOf(itemData.getDuration()));
        mViewHolder.mNameFile.setText(String.valueOf(itemData.getName()));
        mViewHolder.mCheckFile.setChecked(itemData.isChecked());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected class ViewHolderItem extends RecyclerView.ViewHolder {
        @BindView(R.id.explorer_file_duration) TextView mDurationFile;
        @BindView(R.id.explorer_file_name) TextView mNameFile;
        @BindView(R.id.explorer_file_check) CheckBox mCheckFile;

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
