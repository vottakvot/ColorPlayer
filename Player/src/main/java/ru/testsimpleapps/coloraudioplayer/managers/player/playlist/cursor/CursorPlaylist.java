package ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;

public class CursorPlaylist implements IPlaylist {

    private final Context mContext;
    private Cursor mPlaylist;
    private long mPlaylistId = NOT_INIT;
    private String mSortBy = CursorTool.SORT_NONE;
    private long mTotalTime;

    public CursorPlaylist(@NonNull final Context context) {
        mContext = context;
        mPlaylistId = NOT_INIT;
        mSortBy = CursorTool.SORT_NONE;
    }

    @Override
    public boolean add(@NonNull Object items) {
        final List<Long> itemsList = (List<Long>) items;
        if (itemsList.isEmpty()) {
            return false;
        }

        final int count = CursorTool.addToPlaylist(mContext.getContentResolver(), mPlaylistId, itemsList);
        recountTotalTime();
        return count > 0;
    }

    @Override
    public boolean delete(long id) {
        final int count = CursorTool.deleteTrackFromPlaylist(mContext.getContentResolver(), mPlaylistId, id);
        recountTotalTime();
        return count > 0;
    }

    @Override
    public boolean toFirst() {
        return mPlaylist != null ? mPlaylist.moveToFirst() : false;
    }

    @Override
    public boolean toLast() {
        return mPlaylist != null ? mPlaylist.moveToLast() : false;
    }

    @Override
    public boolean goTo(long position) {
        return mPlaylist != null ? mPlaylist.moveToPosition((int) position) : false;
    }

    @Override
    public boolean toNext() {
        return mPlaylist != null ? mPlaylist.moveToNext() : false;
    }

    @Override
    public boolean toPrevious() {
        return mPlaylist != null ? mPlaylist.moveToPrevious() : false;
    }

    @Override
    public long size() {
        return mPlaylist != null ? mPlaylist.getCount() : 0;
    }

    @Override
    public long position() {
        return mPlaylist != null ? mPlaylist.getPosition() : NOT_INIT;
    }

    @Override
    public long getPlaylistId() {
        return mPlaylistId;
    }

    @Override
    public long getTrackId() {
        return mPlaylist != null ? mPlaylist.getLong(mPlaylist.getColumnIndex(MediaStore.Audio.Media._ID)) : NOT_INIT;
    }

    @Override
    public String getTrackPath() {
        return mPlaylist != null ? mPlaylist.getString(mPlaylist.getColumnIndex(MediaStore.Audio.Media.DATA)) : "";
    }

    @Override
    public String getTrackName() {
        return new File(getTrackPath()).getName();
    }

    @Override
    public String getTrackArtist() {
        return mPlaylist != null ? mPlaylist.getString(mPlaylist.getColumnIndex(MediaStore.Audio.Media.ARTIST)) : "";
    }

    @Override
    public String getTrackTitle() {
        return mPlaylist != null ? mPlaylist.getString(mPlaylist.getColumnIndex(MediaStore.Audio.Media.TITLE)) : "";
    }

    @Override
    public String getTrackAlbum() {
        return mPlaylist != null ? mPlaylist.getString(mPlaylist.getColumnIndex(MediaStore.Audio.Media.ALBUM)) : "";
    }

    @Override
    public long getTotalTime() {
        return mTotalTime;
    }

    @Override
    public long getTrackDuration() {
        return mPlaylist != null ? mPlaylist.getLong(mPlaylist.getColumnIndex(MediaStore.Audio.Media.DURATION)) : NOT_INIT;
    }

    @Override
    public long getTrackDateModified() {
        return mPlaylist != null ? mPlaylist.getLong(mPlaylist.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)) * 1000L : NOT_INIT;
    }

    @Override
    public long find(final long position, final String name) {
        if (name != null && !name.equals("")) {
            if (goTo(position)) {
                do {
                    if (getTrackName().matches("(?i).*(" + name + ").*"))
                        return position();
                } while (toNext());
            }
        }

        return NOT_INIT;
    }

    @Override
    public IPlaylist clone() {
        CursorPlaylist clone = null;
        if (mPlaylistId > IPlaylist.NOT_INIT) {
            try {
                clone = (CursorPlaylist) super.clone();
                clone.setPlaylist(CursorTool.getTracksFromPlaylist(mContext.getContentResolver(), mPlaylistId, mSortBy));
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        return clone;
    }

    public long recountTotalTime() {
        if (mPlaylist != null) {
            final int position = mPlaylist.getPosition();
            mTotalTime = 0;
            if (mPlaylist.moveToFirst()) {
                do {
                    mTotalTime += getTrackDuration();
                } while (mPlaylist.moveToNext());
            }

            mPlaylist.moveToPosition(position);
        }

        return mTotalTime;
    }

    public IPlaylist setCursor(final long playlistId, final String sortBy) {
        if (playlistId > IPlaylist.NOT_INIT) {
            mPlaylistId = playlistId;
            mSortBy = sortBy;
            Cursor activePlaylist = CursorTool.getTracksFromPlaylist(mContext.getContentResolver(), mPlaylistId, sortBy);
            if (activePlaylist != null && activePlaylist.getCount() > 0) {
                closeCursor();
                mPlaylist = activePlaylist;
                return this;
            }
        }

        return null;
    }

    public void closeCursor() {
        if (mPlaylist != null && !mPlaylist.isClosed())
            mPlaylist.close();
    }

    public Cursor getPlaylist() {
        return mPlaylist;
    }

    public void setPlaylist(Cursor mPlaylist) {
        this.mPlaylist = mPlaylist;
    }



}
