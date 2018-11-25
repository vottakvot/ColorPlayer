package ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore

import java.io.File

import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist
import ru.testsimpleapps.coloraudioplayer.managers.tools.CursorTool
import ru.testsimpleapps.coloraudioplayer.service.PlayerService

class CursorPlaylist(private val mContext: Context) : IPlaylist {

    var playlist: Cursor? = null

    override var playlistId: Long = 0
        private set
    private var mSortBy: String? = null
    private var mSortOrder: String? = null
    override var totalTime: Long = 0
        private set

    override val trackId: Long
        get() = if (isCursor) playlist!!.getLong(playlist!!.getColumnIndex(MediaStore.Audio.Media._ID)) else IPlaylist.ERROR_CODE

    override val trackPath: String
        get() = if (isCursor) playlist!!.getString(playlist!!.getColumnIndex(MediaStore.Audio.Media.DATA)) else ""

    override val trackName: String
        get() = File(trackPath).name

    override val trackArtist: String
        get() = if (isCursor) playlist!!.getString(playlist!!.getColumnIndex(MediaStore.Audio.Media.ARTIST)) else ""

    override val trackTitle: String
        get() = if (isCursor) playlist!!.getString(playlist!!.getColumnIndex(MediaStore.Audio.Media.TITLE)) else ""

    override val trackAlbum: String
        get() = if (isCursor) playlist!!.getString(playlist!!.getColumnIndex(MediaStore.Audio.Media.ALBUM)) else ""

    override val trackDuration: Long
        get() = if (isCursor) playlist!!.getLong(playlist!!.getColumnIndex(MediaStore.Audio.Media.DURATION)) else IPlaylist.ERROR_CODE

    override val trackDateModified: Long
        get() = if (isCursor) playlist!!.getLong(playlist!!.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)) * 1000L else IPlaylist.ERROR_CODE

    private val isCursor: Boolean
        get() = playlist != null && !playlist!!.isBeforeFirst

    init {
        playlistId = IPlaylist.ERROR_CODE
        mSortBy = CursorTool.FIELD_NONE
        mSortOrder = CursorTool.SORT_ORDER_ASC
    }

    override fun add(items: Any): Boolean {
        val itemsList = items as List<Long>
        if (itemsList.isEmpty()) {
            return false
        }

        val count = CursorTool.addToPlaylist(mContext.contentResolver, playlistId, itemsList)
        setCursor(playlistId, mSortBy!!, mSortOrder!!)
        PlayerService.sendBroadcastPlaylistChange()
        return count > 0
    }

    override fun delete(id: Long): Boolean {
        val count = CursorTool.deleteTrackFromPlaylist(mContext.contentResolver, playlistId, id)
        recountTotalTime()
        PlayerService.sendBroadcastPlaylistChange()
        return count > 0
    }

    override fun toFirst(): Boolean {
        return if (playlist != null) playlist!!.moveToFirst() else false
    }

    override fun toLast(): Boolean {
        return if (playlist != null) playlist!!.moveToLast() else false
    }

    override fun goToPosition(position: Long): Boolean {
        return if (playlist != null && position >= 0 && position < size()) playlist!!.moveToPosition(position.toInt()) else false
    }

    override fun goToId(id: Long): Boolean {
        if (toFirst()) {
            do {
                if (trackId == id)
                    return true
            } while (toNext())
        }

        toFirst()
        return false
    }

    override fun toNext(): Boolean {
        return if (playlist != null) playlist!!.moveToNext() else false
    }

    override fun toPrevious(): Boolean {
        return if (playlist != null) playlist!!.moveToPrevious() else false
    }

    override fun size(): Long {
        return if (playlist != null) playlist!!.count.toLong() else 0L
    }

    override fun position(): Long {
        return if (isCursor) playlist!!.position.toLong() else 0L
    }

    override fun find(position: Long, name: String): Long {
        if (name != null && name != "") {
            if (goToPosition(position)) {
                do {
                    if (trackName.matches("(?i).*($name).*".toRegex()))
                        return position()
                } while (toNext())
            }
        }

        return IPlaylist.ERROR_CODE
    }

    override fun clone(): IPlaylist {
        var clone: CursorPlaylist? = null
        if (playlistId > IPlaylist.ERROR_CODE) {
            try {
                clone = clone() as CursorPlaylist
                clone.playlist = CursorTool.getTracksFromPlaylist(mContext.contentResolver, playlistId, mSortBy!!, mSortOrder!!)
            } catch (e: CloneNotSupportedException) {
                throw RuntimeException(e)
            }

        }

        return clone!!
    }

    fun recountTotalTime(): Long {
        totalTime = 0
        if (playlist != null) {
            val position = playlist!!.position
            if (playlist!!.moveToFirst()) {
                do {
                    totalTime += trackDuration
                } while (playlist!!.moveToNext())
            }

            if (position >= 0) {
                playlist!!.moveToPosition(position)
            } else {
                playlist!!.moveToFirst()
            }
        }

        return totalTime
    }

    fun setCursor(playlistId: Long, sortBy: String, sortOrder: String): IPlaylist? {
        if (playlistId > IPlaylist.ERROR_CODE) {
            this.playlistId = playlistId
            mSortBy = sortBy
            mSortOrder = sortOrder
            val activePlaylist = CursorTool.getTracksFromPlaylist(mContext.contentResolver, this.playlistId, sortBy, sortOrder)
            closeCursor()
            playlist = activePlaylist
            recountTotalTime()
            PlayerService.sendBroadcastPlaylistChange()
            return this
        }

        return null
    }

    fun closeCursor() {
        if (playlist != null && !playlist!!.isClosed)
            playlist!!.close()
    }

}
