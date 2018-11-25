package ru.testsimpleapps.coloraudioplayer.managers.player.playlist

interface IPlaylist : Cloneable {

    val playlistId: Long

    val trackId: Long

    val trackPath: String

    val trackName: String

    val trackArtist: String

    val trackTitle: String

    val trackAlbum: String

    val totalTime: Long

    val trackDuration: Long

    val trackDateModified: Long

    fun add(items: Any): Boolean

    fun delete(id: Long): Boolean

    fun toFirst(): Boolean

    fun toLast(): Boolean

    fun goToPosition(position: Long): Boolean

    fun goToId(id: Long): Boolean

    fun toNext(): Boolean

    fun toPrevious(): Boolean

    fun size(): Long

    fun position(): Long

    fun find(position: Long, name: String): Long

    public override fun clone(): IPlaylist

    companion object {

        val TEMP_PLAYLIST = "Temp_playlist"

        val ERROR_CODE = -1L
    }
}
