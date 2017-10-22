package ru.testsimpleapps.coloraudioplayer.control.player.playlist.cursor;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;

import ru.testsimpleapps.coloraudioplayer.control.player.playlist.IPlaylist;

public class CursorPlaylist implements IPlaylist {

    private final Context mContext;
    private Cursor mPlaylist;
    private long mPlaylistId = NOT_INIT;
    private String mSortBy = CursorTool.SORT_NONE;

    public CursorPlaylist(@NonNull final Context context, final long playlistId, final String sortBy) {
        mContext = context;
        mPlaylistId = NOT_INIT;
        mSortBy = CursorTool.SORT_NONE;
        setCursor(playlistId, mSortBy);
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
    public boolean goTo(int position) {
        return mPlaylist != null ? mPlaylist.moveToPosition(position) : false;
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
        return mPlaylist != null ? mPlaylist.getCount() : NOT_INIT;
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
    public long getTrackDuration() {
        return mPlaylist != null ? mPlaylist.getLong(mPlaylist.getColumnIndex(MediaStore.Audio.Media.DURATION)) : NOT_INIT;
    }

    @Override
    public long getTrackDateModified() {
        return mPlaylist != null ? mPlaylist.getLong(mPlaylist.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)) * 1000L : NOT_INIT;
    }

    @Override
    public long find(final int position, final String name) {
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
        CursorPlaylist clone;
        try {
            clone = (CursorPlaylist) super.clone();
            clone.setPlaylist(CursorTool.getTracksFromPlaylist(mContext.getContentResolver(), mPlaylistId, mSortBy));
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        return clone;
    }

    public IPlaylist setCursor(final long playlistId, final String sortBy) {
        if (playlistId > -1) {
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
