package ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor

import ru.testsimpleapps.coloraudioplayer.app.App
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist
import ru.testsimpleapps.coloraudioplayer.service.PlayerService

object CursorFactory {

    @Volatile
    private var sCursorPlaylist: CursorPlaylist? = null
    @Volatile
    private var sCursorPlaylistForView: CursorPlaylist? = null

    val instance: IPlaylist
        get() {
            var cursorPlaylist = sCursorPlaylist
            if (sCursorPlaylist == null) {
                synchronized(CursorFactory::class.java) {
                    cursorPlaylist = sCursorPlaylist
                    if (cursorPlaylist == null) {
                        cursorPlaylist = CursorPlaylist(App.instance)
                        sCursorPlaylist = cursorPlaylist
                        sCursorPlaylist!!.setCursor(PlayerConfig.instance.playlistId, PlayerConfig.instance.playlistSort!!,
                                PlayerConfig.instance.playlistSortOrder!!)
                        sCursorPlaylist!!.goToId(PlayerConfig.instance.trackId)
                    }
                }
            }

            return cursorPlaylist!!
        }

    val copyInstance: IPlaylist?
        get() {
            val cursorPlaylist = sCursorPlaylist
            var viewCursorPlaylist = sCursorPlaylistForView
            if (cursorPlaylist != null) {
                synchronized(CursorFactory::class.java) {
                    if (viewCursorPlaylist != null) {
                        viewCursorPlaylist!!.closeCursor()
                    }
                    viewCursorPlaylist = cursorPlaylist.clone() as CursorPlaylist
                    sCursorPlaylistForView = viewCursorPlaylist
                }
            }

            return viewCursorPlaylist
        }

    fun newInstance(): IPlaylist {
        val cursorPlaylist = instance as CursorPlaylist
        synchronized(CursorFactory::class.java) {
            val playerConfig = PlayerConfig.instance
            cursorPlaylist.setCursor(playerConfig.playlistId, playerConfig.playlistSort!!, playerConfig.playlistSortOrder!!)
            cursorPlaylist.goToId(PlayerConfig.instance.trackId)
        }

        return cursorPlaylist
    }

    fun close() {
        val cursorPlaylist = sCursorPlaylist
        val viewCursorPlaylist = sCursorPlaylistForView

        if (cursorPlaylist != null) {
            synchronized(CursorFactory::class.java) {
                cursorPlaylist.closeCursor()
                viewCursorPlaylist?.closeCursor()
            }
        }
    }

}
