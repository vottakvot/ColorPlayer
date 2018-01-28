package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.tools.CollectionTool;


public class PlaylistChooserAdapter extends BaseAdapter {

    private final Context mContext;
    private Map<Long, String> mData;

    public PlaylistChooserAdapter(@NonNull Context context) {
        mContext = context;
    }

    public void setData(Map<Long, String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View viewItem = LayoutInflater.from(mContext).inflate(R.layout.playlist_choose_item, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        if (mData != null) {
            final Map.Entry<Long, String> entry = CollectionTool.getMapEntryByPosition(mData, position);
            viewHolder.mNamePlaylist.setText(entry.getValue());
        }
    }

    public Map.Entry<Long, String> getItem(int position) {
        return mData != null? CollectionTool.getMapEntryByPosition(mData, position + 1) : null;
    }

    @Override
    public int getItemCount() {
        return mData != null? mData.size() : 0;
    }


    protected class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.playlist_choose_item_name)
        TextView mNamePlaylist;

        public ViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, getLayoutPosition() - 1);
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {
                    if (mOnItemLongClickListener != null) {
                        mOnItemLongClickListener.onItemLongClick(view, getLayoutPosition() - 1);
                        return true;
                    }

                    return false;
                }
            });
        }
    }

}