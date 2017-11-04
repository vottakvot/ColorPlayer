package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.DataContainer;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.DataItem;


public class ExplorerFolderAdapter extends BaseAdapter {

    private final Context mContext;
    private final OnItemClickListener mOnItemClickListener;
    private LinkedHashMap<String, DataContainer<DataItem>> mDataList;

    public ExplorerFolderAdapter(@NonNull Context context, @Nullable OnItemClickListener onItemClickListener) {
        super();
        mContext = context;
        mOnItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.explorer_item_folder, viewGroup, false);
        return new ViewHolderItem(viewItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final ViewHolderItem mViewHolder = (ViewHolderItem) viewHolder;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected class ViewHolderItem extends RecyclerView.ViewHolder {
        @BindView(R.id.countFolder) TextView mCountInFolder;
        @BindView(R.id.nameFolder) TextView mNameFolder;
        @BindView(R.id.noteFolder) CheckBox mCheckFolder;

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