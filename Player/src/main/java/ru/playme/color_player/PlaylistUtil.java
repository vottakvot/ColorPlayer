package ru.playme.color_player;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.TreeMap;

public class PlaylistUtil {

    public static final String SORT_ASC = " ASC";
    public static final String SORT_DESC = " DESC";
    public static final String SORT_NAME = MediaStore.Audio.Media.DATA;
    public static final String SORT_DURATION = MediaStore.Audio.Media.DURATION;
    public static final String SORT_MODIFY = MediaStore.Audio.Media.DATE_MODIFIED;
    public static final String SORT_NONE = null;

    public static TreeMap<Long, String> getPlaylists(ContentResolver resolver) {
        TreeMap<Long, String> playlistMap = null;

        try{
            Uri media = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            String[] projection = { MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME };
            String sort = MediaStore.Audio.Playlists.NAME;
            Cursor playlists = resolver.query(media, projection, null, null, sort);

            if(playlists.getCount() > 0){
                playlistMap = new TreeMap<>();
                while(playlists.moveToNext()) {
                    playlistMap.put(playlists.getLong(playlists.getColumnIndex(MediaStore.Audio.Playlists._ID)),
                            playlists.getString(playlists.getColumnIndex(MediaStore.Audio.Playlists.NAME)));
                }
            }
        } catch (RuntimeException e){
                e.printStackTrace();
            }

        return playlistMap;
    }

    public static long getPlaylistIdByName(ContentResolver resolver, String name) {
        long id = -1;

        try{
            Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    new String[] { MediaStore.Audio.Playlists._ID },
                    MediaStore.Audio.Playlists.NAME + "=?",
                    new String[] { name }, null);

            if (cursor != null) {
                if (cursor.moveToNext())
                    id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
                cursor.close();
            }
        } catch (RuntimeException e){
                e.printStackTrace();
            }

        return id;
    }

    public static String getPlaylistNameById(ContentResolver resolver, long id) {
        String playlistName = "Playlist not found!";

        try {
            if(id < 0)
                return playlistName;

            Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    new String[] { MediaStore.Audio.Playlists.NAME },
                    MediaStore.Audio.Playlists._ID + "=?",
                    new String[] { Long.toString(id) }, null);

            if (cursor != null) {
                if (cursor.moveToNext())
                    playlistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
                cursor.close();
            }
        } catch(RuntimeException e){
                e.printStackTrace();
            }

        return playlistName;
    }

    public static Cursor findTrackByID(Cursor playlist, long id){
        try{
            if(playlist != null && playlist.getCount() > 0){
                playlist.moveToFirst();
                do {
                    if(playlist.getLong(playlist.getColumnIndex(MediaStore.Audio.Media._ID)) == id){
                        return playlist;
                    }
                } while (playlist.moveToNext());
            }
        } catch (RuntimeException e){
                e.printStackTrace();
            }

        return null;
    }

    public static Cursor getTracksFromPlaylist (final ContentResolver contentResolver, final long playListID, final String sortBy) {
        Cursor tracks = null;
        try{
            final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playListID);
            tracks = contentResolver.query(  uri,
                    new String[]{   PlaylistUtil.SORT_NAME,
                                    PlaylistUtil.SORT_DURATION,
                                    MediaStore.Audio.Media._ID,
                                    MediaStore.Audio.Media.ARTIST,
                                    MediaStore.Audio.Media.ALBUM,
                                    MediaStore.Audio.Media.DATE_MODIFIED,
                                    MediaStore.Audio.Media.TITLE },
                    null,
                    null,
                    sortBy);
        } catch (RuntimeException e){
                e.printStackTrace();
            }

        return (tracks != null && tracks.getCount() > 0)? tracks : null;
    }

    public static long createPlaylist(ContentResolver resolver, String name) {
        long id = PlaylistUtil.getPlaylistIdByName(resolver, name);

        try {
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
        } catch (RuntimeException e){
                e.printStackTrace();
            }

        return id;
    }


    public static int addToPlaylist(ContentResolver resolver, long playlistId, ArrayList<Long> songId) {
        if (playlistId == -1 && songId == null && songId.size() == 0)
            return 0;

        try{
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
            String[] projection = new String[] { MediaStore.Audio.Playlists.Members.PLAY_ORDER };
            Cursor cursor = resolver.query(uri, projection, null, null, null);

            int base = 0;
            if (cursor.moveToLast()){
                base = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.PLAY_ORDER)) + 1;
            }
            cursor.close();

            int insertCol = songId.size();
            ContentValues[] values = new ContentValues[insertCol];
            for(int i = 0; i < songId.size(); i++){
                ContentValues value = new ContentValues(2);
                value.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + 1));
                value.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songId.get(i));
                values[i] = value;
            }

            return resolver.bulkInsert(uri, values);

        } catch (RuntimeException e){
                e.printStackTrace();
            }

        return 0;
    }

    public static int deletePlaylist(ContentResolver resolver, long id) {
        try{
            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
            return resolver.delete(uri, null, null);
        } catch (RuntimeException e){
                e.printStackTrace();
            }

        return  -1;
    }

    public static int deleteTrackFromPlaylist(ContentResolver resolver, long playlistId, long trackId) {
        try{
            final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
            return resolver.delete(uri, MediaStore.Audio.Media._ID + " = ? ", new String[] {Long.toString(trackId)});
        } catch (RuntimeException e){
                e.printStackTrace();
            }

        return -1;
    }

    public static boolean renamePlaylist(ContentResolver resolver, long id, String newName) {
        try{
            long existingId = getPlaylistIdByName(resolver, newName);

            if (existingId == id)
                return false;

            if (existingId != -1)
                deletePlaylist(resolver, existingId);

            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, newName);
            resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, "_id=" + id, null);

            return true;
        } catch (RuntimeException e){
                e.printStackTrace();
            }

        return false;
    }
}