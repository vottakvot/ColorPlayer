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

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final Context mContext;

    public ExplorerFilesAdapter(@NonNull Context context) {
        super();
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE_HEADER: {
                final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.explorer_header, viewGroup, false);
                return new ViewHolderHeader(viewItem);
            }

            case TYPE_ITEM: {
                final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.explorer_file, viewGroup, false);
                return new ViewHolderItem(viewItem);
            }

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (mList != null) {
            if (viewHolder instanceof ViewHolderItem) {
                final ViewHolderItem mViewHolder = (ViewHolderItem) viewHolder;
                final ItemData itemData = getItem(i);
                mViewHolder.mDurationFile.setText(String.valueOf(itemData.getDuration()));
                mViewHolder.mNameFile.setText(String.valueOf(itemData.getName()));
                mViewHolder.mCheckFile.setChecked(itemData.isChecked());
            } else if (viewHolder instanceof ViewHolderHeader) {
                final ViewHolderHeader mViewHolder = (ViewHolderHeader) viewHolder;
                mViewHolder.mHeader.setText(mContext.getString(R.string.explorer_header_count_items) + mList.size());
            }
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public ItemData getItem(int index) {
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

    protected class ViewHolderHeader extends RecyclerView.ViewHolder {
        @BindView(R.id.explorer_list_header) TextView mHeader;

        public ViewHolderHeader(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
