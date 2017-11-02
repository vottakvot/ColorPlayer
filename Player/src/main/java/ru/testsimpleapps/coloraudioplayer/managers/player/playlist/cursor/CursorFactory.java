package ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor;

import android.content.Context;
import android.support.annotation.NonNull;

public class CursorFactory {

    private static CursorPlaylist sCursorPlaylist = null;
    private static CursorPlaylist sCursorPlaylistForView = null;

    private CursorFactory() {
    }

    public static CursorPlaylist setCursorPlaylist(@NonNull final Context context, final long playlistId, final String sort) {
        if (sCursorPlaylist == null)
            sCursorPlaylist = new CursorPlaylist(context, playlistId, sort);
        return sCursorPlaylist;
    }

    public static CursorPlaylist getCursorPlaylistForView() {
        if (sCursorPlaylist != null) {
            if (sCursorPlaylistForView != null)
                sCursorPlaylistForView.closeCursor();
            sCursorPlaylistForView = (CursorPlaylist) sCursorPlaylist.clone();
            return sCursorPlaylistForView;
        }

        return null;
    }

    public static void closeCursorsPlaylist() {
        if (sCursorPlaylist != null)
            sCursorPlaylist.closeCursor();
        if (sCursorPlaylistForView != null)
            sCursorPlaylistForView.closeCursor();
    }
}
