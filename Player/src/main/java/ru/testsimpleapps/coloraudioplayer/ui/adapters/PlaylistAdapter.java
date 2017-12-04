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
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;


public class PlaylistAdapter extends BaseAdapter {

    private final Context mContext;
    private IPlaylist mIPlaylist;

    public PlaylistAdapter(@NonNull Context context) {
        mContext = context;
    }

    public void setPlaylist(final IPlaylist iPlaylist) {
        mIPlaylist = iPlaylist;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE_HEADER: {
                final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_header, viewGroup, false);
                return new ViewHolderHeader(viewItem);
            }

            case TYPE_ITEM: {
                final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_item_track, viewGroup, false);
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


        } else if (viewHolder instanceof ViewHolderHeader) {
            final ViewHolderHeader mViewHolder = (ViewHolderHeader) viewHolder;
            mViewHolder.mCountTracks.setText(String.valueOf(1));
            mViewHolder.mTotalTime.setText(String.valueOf(1));
        }
    }

    @Override
    public int getItemCount() {
        return (mIPlaylist != null ? (int) mIPlaylist.size() : 0) + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    protected class ViewHolderItem extends RecyclerView.ViewHolder {

        @BindView(R.id.explorer_file_duration)
        TextView mDurationFile;
        @BindView(R.id.explorer_file_name)
        TextView mNameFile;
        @BindView(R.id.explorer_file_check)
        CheckBox mCheckFile;

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
        @BindView(R.id.playlist_header_count_value)
        TextView mCountTracks;
        @BindView(R.id.playlist_header_total_time_value)
        TextView mTotalTime;

        public ViewHolderHeader(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
