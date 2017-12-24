package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory;
import ru.testsimpleapps.coloraudioplayer.managers.tools.CursorTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.TimeTool;


public class PlaylistAdapter extends BaseAdapter {

    private final Context mContext;
    private IPlaylist mIPlaylist;
    private boolean mIsExpand = true;
    private long mPreviousSearchPosition = 0;
    private long mSearchedPosition = IPlaylist.ERROR_CODE;
    private int mViewPadding;

    public PlaylistAdapter(@NonNull Context context) {
        mContext = context;
        mViewPadding = (int)context.getResources().getDimension(R.dimen.playlist_item_selection_padding);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE_HEADER: {
                final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_header, viewGroup, false);
                return new ViewHolderHeader(viewItem);
            }

            case TYPE_ITEM: {
                final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_item_layout, viewGroup, false);
                return new ViewHolderItem(viewItem);
            }

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (mIPlaylist != null) {
            if (viewHolder instanceof ViewHolderItem) {
                final ViewHolderItem mViewHolder = (ViewHolderItem) viewHolder;
                mIPlaylist.goToPosition(i - 1);

                // Main
                mViewHolder.mImageTrack.setImageResource(R.drawable.image_item_track);
                mViewHolder.mCountTrack.setText(String.valueOf(i));
                mViewHolder.mNameTrack.setText(String.valueOf(mIPlaylist.getTrackName()));

                // Info
                if (mIsExpand) {
                    mViewHolder.mInfoLayout.setVisibility(View.VISIBLE);
                    mViewHolder.mDurationTrack.setText(TimeTool.getDuration(mIPlaylist.getTrackDuration()));
                    mViewHolder.mArtistsTrack.setText(String.valueOf(mIPlaylist.getTrackArtist()));
                    mViewHolder.mAlbumTrack.setText(String.valueOf(mIPlaylist.getTrackAlbum()));
                    mViewHolder.mDateTrack.setText(TimeTool.getDateTime(mIPlaylist.getTrackDateModified()));
                } else {
                    mViewHolder.mInfoLayout.setVisibility(View.GONE);
                }

                // Background for current position or image_search
                if (mIPlaylist.getTrackId() == CursorFactory.getInstance().getTrackId()) {
                    mViewHolder.itemView.setBackgroundResource(R.drawable.drawable_recycleview_item_selection);
                    mViewHolder.itemView.setPadding(mViewPadding, mViewPadding, mViewPadding, mViewPadding);
                } else if (mSearchedPosition == i) {
                    mViewHolder.itemView.setBackgroundResource(R.drawable.drawable_recycleview_item_find);
                    mViewHolder.itemView.setPadding(mViewPadding, mViewPadding, mViewPadding, mViewPadding);
                } else {
                    mViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
                    mViewHolder.itemView.setPadding(0, 0, 0, 0);
                }

            } else if (viewHolder instanceof ViewHolderHeader) {
                final ViewHolderHeader mViewHolder = (ViewHolderHeader) viewHolder;
                mViewHolder.mNamePlaylist.setText(CursorTool.getPlaylistNameById(mContext.getContentResolver(),
                        mIPlaylist.getPlaylistId()));
                mViewHolder.mCountTracks.setText(String.valueOf(getTotalTracks()));
                mViewHolder.mTotalTimeTracks.setText(TimeTool.getDuration(getTotalTime()));
            }
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

    @Override
    public long getItemId(int position) {
        if (mIPlaylist != null && mIPlaylist.goToPosition(position)) {
            return mIPlaylist.getTrackId();
        }

        return IPlaylist.ERROR_CODE;
    }

    public void setPlaylist(final IPlaylist iPlaylist) {
        mIPlaylist = iPlaylist;
        notifyDataSetChanged();
    }

    public void setExpand(final boolean isExpand) {
        mIsExpand = isExpand;
        notifyDataSetChanged();
    }

    public int searchMatch(final String text) {
        if (mIPlaylist != null) {
            final long position = mIPlaylist.find(mPreviousSearchPosition, text);
            mPreviousSearchPosition = position + 1;
            return (int)position;
        }

        return (int)IPlaylist.ERROR_CODE;
    }

    public void setSearchedPosition(final long position) {
        mSearchedPosition = position;
    }

    private long getTotalTime() {
        return mIPlaylist != null? mIPlaylist.getTotalTime() : 0;
    }

    private long getTotalTracks() {
        return mIPlaylist != null? mIPlaylist.size() : 0;
    }

    protected class ViewHolderHeader extends RecyclerView.ViewHolder {
        @BindView(R.id.playlist_header_name_value)
        TextView mNamePlaylist;
        @BindView(R.id.playlist_header_count_value)
        TextView mCountTracks;
        @BindView(R.id.playlist_header_total_time_value)
        TextView mTotalTimeTracks;

        public ViewHolderHeader(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    protected class ViewHolderItem extends RecyclerView.ViewHolder {

        /*
        * Main views
        * */
        @BindView(R.id.playlist_track_image)
        ImageView mImageTrack;
        @BindView(R.id.playlist_track_count)
        TextView mCountTrack;
        @BindView(R.id.playlist_track_name)
        TextView mNameTrack;

        /*
        * Optional
        * */
        @BindView(R.id.playlist_track_item_info_layout)
        ConstraintLayout mInfoLayout;
        @BindView(R.id.playlist_track_duration_value)
        TextView mDurationTrack;
        @BindView(R.id.playlist_track_artist_value)
        TextView mArtistsTrack;
        @BindView(R.id.playlist_track_albums_value)
        TextView mAlbumTrack;
        @BindView(R.id.playlist_track_date_value)
        TextView mDateTrack;

        public ViewHolderItem(final View view) {
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
        }
    }

}
