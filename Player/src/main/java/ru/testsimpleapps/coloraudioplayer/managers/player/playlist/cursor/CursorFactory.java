package ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;

public class CursorFactory {

    private static volatile CursorPlaylist sCursorPlaylist = null;
    private static volatile CursorPlaylist sCursorPlaylistForView = null;

    private CursorFactory() {}

    public static IPlaylist getInstance() {
        CursorPlaylist cursorPlaylist = sCursorPlaylist;
        if (sCursorPlaylist == null) {
            synchronized (CursorFactory.class) {
                cursorPlaylist = sCursorPlaylist;
                if (cursorPlaylist == null) {
                    sCursorPlaylist = cursorPlaylist = new CursorPlaylist(App.getContext());
                    sCursorPlaylist.setCursor(PlayerConfig.getInstance().getPlaylistId(), PlayerConfig.getInstance().getPlaylistSort(),
                            PlayerConfig.getInstance().getPlaylistSortOrder());
                    sCursorPlaylist.goToId(PlayerConfig.getInstance().getTrackId());
                }
            }
        }

        return cursorPlaylist;
    }

    public static IPlaylist newInstance() {
        final CursorPlaylist cursorPlaylist = (CursorPlaylist) getInstance();
        synchronized (CursorFactory.class) {
            cursorPlaylist.setCursor(PlayerConfig.getInstance().getPlaylistId(), PlayerConfig.getInstance().getPlaylistSort(),
                    PlayerConfig.getInstance().getPlaylistSortOrder());
            cursorPlaylist.goToId(PlayerConfig.getInstance().getTrackId());
        }

        return cursorPlaylist;
    }

    public static IPlaylist getCopyInstance() {
        final CursorPlaylist cursorPlaylist = sCursorPlaylist;
        CursorPlaylist viewCursorPlaylist = sCursorPlaylistForView;
        if (cursorPlaylist != null) {
            synchronized (CursorFactory.class) {
                if (viewCursorPlaylist != null) {
                    viewCursorPlaylist.closeCursor();
                }
                sCursorPlaylistForView = viewCursorPlaylist = (CursorPlaylist) cursorPlaylist.clone();
            }
        }

        return viewCursorPlaylist;
    }

    public static void close() {
        final CursorPlaylist cursorPlaylist = sCursorPlaylist;
        final CursorPlaylist viewCursorPlaylist = sCursorPlaylistForView;

        if (cursorPlaylist != null) {
            synchronized (CursorFactory.class) {
                cursorPlaylist.closeCursor();
                if (viewCursorPlaylist != null) {
                    viewCursorPlaylist.closeCursor();
                }
            }
        }
    }

}
