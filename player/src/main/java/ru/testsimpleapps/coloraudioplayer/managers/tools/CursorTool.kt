package ru.testsimpleapps.coloraudioplayer.managers.tools

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import java.util.TreeMap

import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist

object CursorTool {

    val CONTENT_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
    val SORT_ORDER_ASC = " ASC"
    val SORT_ORDER_DESC = " DESC"
    val FIELD_NAME = MediaStore.Audio.Media.DATA
    val FIELD_DURATION = MediaStore.Audio.Media.DURATION
    val FIELD_MODIFY = MediaStore.Audio.Media.DATE_MODIFIED
    val FIELD_ARTIST = MediaStore.Audio.Media.ARTIST
    val FIELD_ALBUMS = MediaStore.Audio.Media.ALBUM
    val FIELD_NONE: String? = null

    fun getPlaylist(resolver: ContentResolver): Map<Long, String>? {
        var playlistMap: MutableMap<Long, String>? = null
        val projection = arrayOf(MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME)
        val sort = MediaStore.Audio.Playlists.NAME
        val cursor = resolver.query(CONTENT_URI, projection, null, null, sort)

        if (cursor!!.count > 0) {
            playlistMap = TreeMap()
            while (cursor.moveToNext()) {
                playlistMap[cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID))] = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME))
            }
        }

        return playlistMap
    }

    fun getTracksFromPlaylist(contentResolver: ContentResolver, playListID: Long,
                              sortBy: String, sortType: String): Cursor? {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playListID)
        return contentResolver.query(uri,
                arrayOf(FIELD_NAME, FIELD_DURATION, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DATE_MODIFIED, MediaStore.Audio.Media.TITLE), null, null, sortBy + sortType)
    }

    fun getPlaylistIdByName(resolver: ContentResolver, name: String): Long {
        var id = IPlaylist.ERROR_CODE
        val cursor = resolver.query(CONTENT_URI,
                arrayOf(MediaStore.Audio.Playlists._ID),
                MediaStore.Audio.Playlists.NAME + " =? ",
                arrayOf(name), null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID))
            }
            cursor.close()
        }

        return id
    }

    fun getPlaylistNameById(resolver: ContentResolver, id: Long): String {
        var playlistName = "Playlist not found!"
        if (id < 0)
            return playlistName

        val cursor = resolver.query(CONTENT_URI,
                arrayOf(MediaStore.Audio.Playlists.NAME),
                MediaStore.Audio.Playlists._ID + "=?",
                arrayOf(java.lang.Long.toString(id)), null)

        if (cursor != null) {
            if (cursor.moveToNext())
                playlistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME))
            cursor.close()
        }

        return playlistName
    }

    fun findTrackByID(playlist: Cursor?, id: Long): Cursor? {
        if (playlist != null && playlist.count > 0) {
            playlist.moveToFirst()
            do {
                if (playlist.getLong(playlist.getColumnIndex(MediaStore.Audio.Media._ID)) == id) {
                    return playlist
                }
            } while (playlist.moveToNext())
        }

        return null
    }

    fun createPlaylist(resolver: ContentResolver, name: String): Long {
        var id = CursorTool.getPlaylistIdByName(resolver, name)

        // Create new playlist or clear existing
        if (id == IPlaylist.ERROR_CODE) {
            val values = ContentValues(1)
            values.put(MediaStore.Audio.Playlists.NAME, name)
            val uri = resolver.insert(CONTENT_URI, values)
            id = java.lang.Long.parseLong(uri!!.lastPathSegment!!)
        } else {
            val uri = MediaStore.Audio.Playlists.Members.getContentUri(CONTENT_URI.toString(), id)
            try {
                resolver.delete(uri, null, null)
            } catch (e: RuntimeException) {
                // No need handle
                //                id = IPlaylist.ERROR_CODE;
            }

        }

        return id
    }


    fun addToPlaylist(resolver: ContentResolver, playlistId: Long, songId: List<Long>?): Int {
        if (playlistId.toInt() == -1 || songId == null && songId!!.size == 0)
            return 0

        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        val projection = arrayOf(MediaStore.Audio.Playlists.Members.PLAY_ORDER)
        val cursor = resolver.query(uri, projection, null, null, null)

        var base = 0
        if (cursor!!.moveToLast()) {
            base = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.PLAY_ORDER)) + 1
        }
        cursor.close()

        val insertCol = songId.size
        val values = arrayOfNulls<ContentValues>(insertCol)
        for (i in songId.indices) {
            val value = ContentValues(2)
            value.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + 1))
            value.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songId[i])
            values[i] = value
        }

        return resolver.bulkInsert(uri, values)
    }

    fun deletePlaylist(resolver: ContentResolver, id: Long): Int {
        val uri = ContentUris.withAppendedId(CONTENT_URI, id)
        return resolver.delete(uri, null, null)
    }

    fun deleteTrackFromPlaylist(resolver: ContentResolver, playlistId: Long, trackId: Long): Int {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        return resolver.delete(uri, MediaStore.Audio.Media._ID + " = ? ", arrayOf(java.lang.Long.toString(trackId)))
    }

    fun renamePlaylist(resolver: ContentResolver, id: Long, newName: String): Boolean {
        val existingId = getPlaylistIdByName(resolver, newName)

        if (existingId == id)
            return false

        if (existingId.toInt() != -1)
            deletePlaylist(resolver, existingId)

        val values = ContentValues(1)
        values.put(MediaStore.Audio.Playlists.NAME, newName)
        resolver.update(CONTENT_URI, values, "_id=$id", null)
        return true
    }
}