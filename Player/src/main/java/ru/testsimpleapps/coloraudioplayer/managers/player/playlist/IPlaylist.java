package ru.testsimpleapps.coloraudioplayer.managers.player.playlist;

public interface IPlaylist extends Cloneable {

    long NOT_INIT = -1L;

    boolean add(Object items);

    boolean delete(long id);

    boolean toFirst();

    boolean toLast();

    boolean goTo(long position);

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

    long getTrackDuration();

    long getTrackDateModified();

    long find(long position, String name);

    IPlaylist clone();
}
