package ru.testsimpleapps.coloraudioplayer.control.player.playlist;


public interface IPlaylist extends Cloneable {

    long NOT_INIT = -1L;

    boolean toFirst();
    boolean toLast();
    boolean goTo(int position);
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
    long find(int position, String name);

    IPlaylist clone();
}
