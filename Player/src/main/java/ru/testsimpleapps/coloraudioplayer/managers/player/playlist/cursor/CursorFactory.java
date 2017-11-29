package ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;

public class CursorFactory {

    private static CursorPlaylist sCursorPlaylist = null;
    private static CursorPlaylist sCursorPlaylistForView = null;

    private CursorFactory() {
    }

    public static IPlaylist getInstance(final long playlistId, final String sort) {
        if (sCursorPlaylist == null) {
            sCursorPlaylist = new CursorPlaylist(App.getContext(), playlistId, sort);
        }

        return sCursorPlaylist;
    }

    public static IPlaylist getViewInstance() {
        if (sCursorPlaylist != null) {
            if (sCursorPlaylistForView != null) {
                sCursorPlaylistForView.closeCursor();
            }
            sCursorPlaylistForView = (CursorPlaylist) sCursorPlaylist.clone();
            return sCursorPlaylistForView;
        }

        return null;
    }

    public static void close() {
        if (sCursorPlaylist != null) {
            sCursorPlaylist.closeCursor();
        }

        if (sCursorPlaylistForView != null) {
            sCursorPlaylistForView.closeCursor();
        }
    }
}
