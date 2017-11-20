package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ItemData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.MediaExplorerManager;
import ru.testsimpleapps.coloraudioplayer.managers.tools.TimeTool;


public class ExplorerFilesAdapter extends BaseAdapter<ItemData> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final Context mContext;
    private long mTotalTime = -1L;

    public ExplorerFilesAdapter(@NonNull Context context) {
        super();
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE_HEADER: {
                final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.explorer_header_files, viewGroup, false);
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
        if (viewHolder instanceof ViewHolderItem) {
            final ViewHolderItem mViewHolder = (ViewHolderItem) viewHolder;
            final ItemData itemData = getItem(i);
            mViewHolder.mCheckFile.setTag(ViewHolderItem.TAG_CHECK, i);
            mViewHolder.mDurationFile.setText(TimeTool.getDuration(itemData.getDuration()));
            mViewHolder.mNameFile.setText(String.valueOf(itemData.getName()));
            mViewHolder.mCheckFile.setChecked(itemData.isChecked());
        } else if (viewHolder instanceof ViewHolderHeader) {
            final ViewHolderHeader mViewHolder = (ViewHolderHeader) viewHolder;
            mViewHolder.mCountFiles.setText(String.valueOf(getItemCount() - 1));
            mViewHolder.mTotalTime.setText(TimeTool.getDuration(mTotalTime));
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

    public void setItems(final List<ItemData> list) {
        super.setItems(list);
        mTotalTime = countTotalTime();
    }

    /*
    * Count total time for current folder/album/artist only one time
    * */
    private long countTotalTime() {
        long totalTime = 0;
        if (mList != null) {
            for (ItemData item : mList) {
                totalTime += item.getDuration();
            }
        }

        return totalTime;
    }

    protected class ViewHolderItem extends RecyclerView.ViewHolder {

        public static final int TAG_CHECK = 10 << 24;

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

        @OnCheckedChanged(R.id.explorer_file_check)
        protected void onChecked(final CheckBox checkBox) {
            final int position = (int) checkBox.getTag(TAG_CHECK);
            final ItemData itemData = getItem(position);
            itemData.setChecked(checkBox.isChecked());
        }
    }

    protected class ViewHolderHeader extends RecyclerView.ViewHolder {
        @BindView(R.id.explorer_header_files_count_files_value) TextView mCountFiles;
        @BindView(R.id.explorer_header_files_total_time_value) TextView mTotalTime;

        public ViewHolderHeader(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
