package ru.testsimpleapps.coloraudioplayer.control.player.playlist.cursor;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class CursorTool {

    public static final String SORT_ASC = " ASC";
    public static final String SORT_NAME = MediaStore.Audio.Media.DATA;
    public static final String SORT_DURATION = MediaStore.Audio.Media.DURATION;
    public static final String SORT_MODIFY = MediaStore.Audio.Media.DATE_MODIFIED;
    public static final String SORT_NONE = null;

    public static Map<Long, String> getPlaylists(ContentResolver resolver) {
        Map<Long, String> playlistMap = null;

        Uri media = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
        String sort = MediaStore.Audio.Playlists.NAME;
        Cursor playlists = resolver.query(media, projection, null, null, sort);

        if (playlists.getCount() > 0) {
            playlistMap = new TreeMap<>();
            while (playlists.moveToNext()) {
                playlistMap.put(playlists.getLong(playlists.getColumnIndex(MediaStore.Audio.Playlists._ID)),
                        playlists.getString(playlists.getColumnIndex(MediaStore.Audio.Playlists.NAME)));
            }
        }

        return playlistMap;
    }

    public static Cursor getTracksFromPlaylist(final ContentResolver contentResolver, final long playListID, final String sortBy) {
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playListID);
        Cursor tracks = contentResolver.query(uri,
                new String[]{
                        CursorTool.SORT_NAME,
                        CursorTool.SORT_DURATION,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DATE_MODIFIED,
                        MediaStore.Audio.Media.TITLE}, null, null, sortBy);
        return (tracks != null && tracks.getCount() > 0) ? tracks : null;
    }

    public static long getPlaylistIdByName(ContentResolver resolver, String name) {
        long id = -1;

        Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists._ID},
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[]{name}, null);

        if (cursor != null) {
            if (cursor.moveToNext())
                id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
            cursor.close();
        }

        return id;
    }

    public static String getPlaylistNameById(ContentResolver resolver, long id) {
        String playlistName = "Playlist not found!";
        if (id < 0)
            return playlistName;

        Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists.NAME},
                MediaStore.Audio.Playlists._ID + "=?",
                new String[]{Long.toString(id)}, null);

        if (cursor != null) {
            if (cursor.moveToNext())
                playlistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
            cursor.close();
        }

        return playlistName;
    }

    public static Cursor findTrackByID(Cursor playlist, long id) {
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

    public static long createPlaylist(ContentResolver resolver, String name) {
        long id = CursorTool.getPlaylistIdByName(resolver, name);
        if (id == -1) {
            // We need to create a new playlist.
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, name);
            Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
            id = Long.parseLong(uri.getLastPathSegment());
        } else {
            // We are overwriting an existing playlist. Clear existing songs.
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
            resolver.delete(uri, null, null);
        }

        return id;
    }


    public static int addToPlaylist(ContentResolver resolver, long playlistId, ArrayList<Long> songId) {
        if (playlistId == -1 && songId == null && songId.size() == 0)
            return 0;

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        String[] projection = new String[]{MediaStore.Audio.Playlists.Members.PLAY_ORDER};
        Cursor cursor = resolver.query(uri, projection, null, null, null);

        int base = 0;
        if (cursor.moveToLast()) {
            base = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.PLAY_ORDER)) + 1;
        }
        cursor.close();

        int insertCol = songId.size();
        ContentValues[] values = new ContentValues[insertCol];
        for (int i = 0; i < songId.size(); i++) {
            ContentValues value = new ContentValues(2);
            value.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + 1));
            value.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songId.get(i));
            values[i] = value;
        }

        return resolver.bulkInsert(uri, values);
    }

    public static int deletePlaylist(ContentResolver resolver, long id) {
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
        return resolver.delete(uri, null, null);
    }

    public static int deleteTrackFromPlaylist(ContentResolver resolver, long playlistId, long trackId) {
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        return resolver.delete(uri, MediaStore.Audio.Media._ID + " = ? ", new String[]{Long.toString(trackId)});
    }

    public static boolean renamePlaylist(ContentResolver resolver, long id, String newName) {
        long existingId = getPlaylistIdByName(resolver, newName);

        if (existingId == id)
            return false;

        if (existingId != -1)
            deletePlaylist(resolver, existingId);

        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Audio.Playlists.NAME, newName);
        resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, "_id=" + id, null);
        return true;
    }
}