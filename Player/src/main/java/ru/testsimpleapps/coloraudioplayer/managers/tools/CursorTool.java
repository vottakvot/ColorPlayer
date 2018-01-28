package ru.testsimpleapps.coloraudioplayer.managers.tools;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;

public class CursorTool {

    public static final Uri CONTENT_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
    public static final String SORT_ORDER_ASC = " ASC";
    public static final String SORT_ORDER_DESC = " DESC";
    public static final String FIELD_NAME = MediaStore.Audio.Media.DATA;
    public static final String FIELD_DURATION = MediaStore.Audio.Media.DURATION;
    public static final String FIELD_MODIFY = MediaStore.Audio.Media.DATE_MODIFIED;
    public static final String FIELD_ARTIST = MediaStore.Audio.Media.ARTIST;
    public static final String FIELD_ALBUMS = MediaStore.Audio.Media.ALBUM;
    public static final String FIELD_NONE = null;

    public static Map<Long, String> getPlaylist(final ContentResolver resolver) {
        Map<Long, String> playlistMap = null;
        final String[] projection = { MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME };
        final String sort = MediaStore.Audio.Playlists.NAME;
        final Cursor cursor = resolver.query(CONTENT_URI, projection, null, null, sort);

        if (cursor.getCount() > 0) {
            playlistMap = new TreeMap<>();
            while (cursor.moveToNext()) {
                playlistMap.put(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME)));
            }
        }

        return playlistMap;
    }

    public static Cursor getTracksFromPlaylist(final ContentResolver contentResolver, final long playListID,
                                               final String sortBy, final String sortType) {
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playListID);
        final Cursor cursor = contentResolver.query(uri,
                new String[] {
                        CursorTool.FIELD_NAME,
                        CursorTool.FIELD_DURATION,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DATE_MODIFIED,
                        MediaStore.Audio.Media.TITLE }, null, null, sortBy + sortType);
        return cursor;
    }

    public static long getPlaylistIdByName(final ContentResolver resolver, final String name) {
        long id = IPlaylist.ERROR_CODE;
        final Cursor cursor = resolver.query(CONTENT_URI,
                new String[]{ MediaStore.Audio.Playlists._ID },
                MediaStore.Audio.Playlists.NAME + " =? ",
                new String[] { name }, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
            }
            cursor.close();
        }

        return id;
    }

    public static String getPlaylistNameById(final ContentResolver resolver, final long id) {
        String playlistName = "Playlist not found!";
        if (id < 0)
            return playlistName;

        final Cursor cursor = resolver.query(CONTENT_URI,
                new String[]{ MediaStore.Audio.Playlists.NAME },
                MediaStore.Audio.Playlists._ID + "=?",
                new String[]{ Long.toString(id) }, null);

        if (cursor != null) {
            if (cursor.moveToNext())
                playlistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
            cursor.close();
        }

        return playlistName;
    }

    public static Cursor findTrackByID(final Cursor playlist, final long id) {
        if (playlist != null && playlist.getCount() > 0) {
            playlist.moveToFirst();
            do {
                if (playlist.getLong(playlist.getColumnIndex(MediaStore.Audio.Media._ID)) == id) {
                    return playlist;
                }
            } while (playlist.moveToNext());
        }

        return null;
    }

    public static long createPlaylist(final ContentResolver resolver, final String name) {
        long id = CursorTool.getPlaylistIdByName(resolver, name);

        // Create new playlist or clear existing
        if (id == IPlaylist.ERROR_CODE) {
            final ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, name);
            final Uri uri = resolver.insert(CONTENT_URI, values);
            id = Long.parseLong(uri.getLastPathSegment());
        } else {
            final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(CONTENT_URI.toString(), id);
            try {
                resolver.delete(uri, null, null);
            } catch (RuntimeException e) {
                // No need handle
//                id = IPlaylist.ERROR_CODE;
            }
        }

        return id;
    }


    public static int addToPlaylist(final ContentResolver resolver, final long playlistId, final List<Long> songId) {
        if (playlistId == -1 || (songId == null && songId.size() == 0))
            return 0;

        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        final String[] projection = new String[]{ MediaStore.Audio.Playlists.Members.PLAY_ORDER };
        final Cursor cursor = resolver.query(uri, projection, null, null, null);

        int base = 0;
        if (cursor.moveToLast()) {
            base = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.PLAY_ORDER)) + 1;
        }
        cursor.close();

        final int insertCol = songId.size();
        final ContentValues[] values = new ContentValues[insertCol];
        for (int i = 0; i < songId.size(); i++) {
            final ContentValues value = new ContentValues(2);
            value.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + 1));
            value.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songId.get(i));
            values[i] = value;
        }

        return resolver.bulkInsert(uri, values);
    }

    public static int deletePlaylist(final ContentResolver resolver, final long id) {
        final Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);
        return resolver.delete(uri, null, null);
    }

    public static int deleteTrackFromPlaylist(final ContentResolver resolver, final long playlistId, final long trackId) {
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        return resolver.delete(uri, MediaStore.Audio.Media._ID + " = ? ", new String[]{Long.toString(trackId)});
    }

    public static boolean renamePlaylist(final ContentResolver resolver, final long id, final String newName) {
        final long existingId = getPlaylistIdByName(resolver, newName);

        if (existingId == id)
            return false;

        if (existingId != -1)
            deletePlaylist(resolver, existingId);

        final ContentValues values = new ContentValues(1);
        values.put(MediaStore.Audio.Playlists.NAME, newName);
        resolver.update(CONTENT_URI, values, "_id=" + id, null);
        return true;
    }
}