package ru.testsimpleapps.coloraudioplayer.managers.player.playlist;

public interface IPlaylist extends Cloneable {

    String TEMP_PLAYLIST = "Temp_playlist";

    long ERROR_CODE = -1L;

    boolean add(Object items);

    boolean delete(long id);

    boolean toFirst();

    boolean toLast();

    boolean goToPosition(long position);

    boolean goToId(long id);

    boolean toNext();

    boolean toPrevious();

    long size();

    long position();

    long getPlaylistId();

    long getTrackId();

    String getTrackPath();

    String getTrackName();

    String getTrackArtist();

    String getTrackTitle();

    String getTrackAlbum();

    long getTotalTime();

    long getTrackDuration();

    long getTrackDateModified();

    long find(long position, String name);

    IPlaylist clone();
}
