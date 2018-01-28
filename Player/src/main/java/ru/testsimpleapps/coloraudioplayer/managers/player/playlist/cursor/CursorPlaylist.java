package ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;
import ru.testsimpleapps.coloraudioplayer.managers.tools.CursorTool;
import ru.testsimpleapps.coloraudioplayer.service.PlayerService;

public class CursorPlaylist implements IPlaylist {

    private final Context mContext;
    private Cursor mPlaylist;
    private long mPlaylistId;
    private String mSortBy;
    private String mSortOrder;
    private long mTotalTime;

    public CursorPlaylist(@NonNull final Context context) {
        mContext = context;
        mPlaylistId = ERROR_CODE;
        mSortBy = CursorTool.FIELD_NONE;
        mSortOrder = CursorTool.SORT_ORDER_ASC;
    }

    @Override
    public boolean add(@NonNull Object items) {
        final List<Long> itemsList = (List<Long>) items;
        if (itemsList.isEmpty()) {
            return false;
        }

        final int count = CursorTool.addToPlaylist(mContext.getContentResolver(), mPlaylistId, itemsList);
        setCursor(mPlaylistId, mSortBy, mSortOrder);
        PlayerService.sendBroadcastPlaylistChange();
        return count > 0;
    }

    @Override
    public boolean delete(long id) {
        final int count = CursorTool.deleteTrackFromPlaylist(mContext.getContentResolver(), mPlaylistId, id);
        recountTotalTime();
        PlayerService.sendBroadcastPlaylistChange();
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
    public boolean goToPosition(long position) {
        return mPlaylist != null && position >= 0 && position < size()? mPlaylist.moveToPosition((int) position) : false;
    }

    @Override
    public boolean goToId(long id) {
        if (toFirst()) {
            do {
                if (getTrackId() == id)
                    return true;
            } while (toNext());
        }

        toFirst();
        return false;
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
        return mPlaylist != null ? mPlaylist.getCount() : 0L;
    }

    @Override
    public long position() {
        return isCursor()? mPlaylist.getPosition() : 0L;
    }

    @Override
    public long getPlaylistId() {
        return mPlaylistId;
    }

    @Override
    public long getTrackId() {
        return isCursor()? mPlaylist.getLong(mPlaylist.getColumnIndex(MediaStore.Audio.Media._ID)) : ERROR_CODE;
    }

    @Override
    public String getTrackPath() {
        return isCursor()? mPlaylist.getString(mPlaylist.getColumnIndex(MediaStore.Audio.Media.DATA)) : "";
    }

    @Override
    public String getTrackName() {
        return new File(getTrackPath()).getName();
    }

    @Override
    public String getTrackArtist() {
        return isCursor()? mPlaylist.getString(mPlaylist.getColumnIndex(MediaStore.Audio.Media.ARTIST)) : "";
    }

    @Override
    public String getTrackTitle() {
        return isCursor()? mPlaylist.getString(mPlaylist.getColumnIndex(MediaStore.Audio.Media.TITLE)) : "";
    }

    @Override
    public String getTrackAlbum() {
        return isCursor()? mPlaylist.getString(mPlaylist.getColumnIndex(MediaStore.Audio.Media.ALBUM)) : "";
    }

    @Override
    public long getTotalTime() {
        return mTotalTime;
    }

    @Override
    public long getTrackDuration() {
        return isCursor()? mPlaylist.getLong(mPlaylist.getColumnIndex(MediaStore.Audio.Media.DURATION)) : ERROR_CODE;
    }

    @Override
    public long getTrackDateModified() {
        return isCursor()? mPlaylist.getLong(mPlaylist.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)) * 1000L : ERROR_CODE;
    }

    @Override
    public long find(final long position, final String name) {
        if (name != null && !name.equals("")) {
            if (goToPosition(position)) {
                do {
                    if (getTrackName().matches("(?i).*(" + name + ").*"))
                        return position();
                } while (toNext());
            }
        }

        return ERROR_CODE;
    }

    @Override
    public IPlaylist clone() {
        CursorPlaylist clone = null;
        if (mPlaylistId > IPlaylist.ERROR_CODE) {
            try {
                clone = (CursorPlaylist) super.clone();
                clone.setPlaylist(CursorTool.getTracksFromPlaylist(mContext.getContentResolver(), mPlaylistId, mSortBy, mSortOrder));
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        return clone;
    }

    public long recountTotalTime() {
        mTotalTime = 0;
        if (mPlaylist != null) {
            final int position = mPlaylist.getPosition();
            if (mPlaylist.moveToFirst()) {
                do {
                    mTotalTime += getTrackDuration();
                } while (mPlaylist.moveToNext());
            }

            if (position >= 0) {
                mPlaylist.moveToPosition(position);
            } else {
                mPlaylist.moveToFirst();
            }
        }

        return mTotalTime;
    }

    public IPlaylist setCursor(final long playlistId, final String sortBy, final String sortOrder) {
        if (playlistId > IPlaylist.ERROR_CODE) {
            mPlaylistId = playlistId;
            mSortBy = sortBy;
            mSortOrder = sortOrder;
            final Cursor activePlaylist = CursorTool.getTracksFromPlaylist(mContext.getContentResolver(), mPlaylistId, sortBy, sortOrder);
            closeCursor();
            mPlaylist = activePlaylist;
            recountTotalTime();
            PlayerService.sendBroadcastPlaylistChange();
            return this;
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

    private boolean isCursor() {
        return mPlaylist != null && !mPlaylist.isBeforeFirst();
    }

}
