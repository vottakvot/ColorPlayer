package ru.testsimpleapps.coloraudioplayer.managers.player;

public interface IAudioPlayer {

    /*
    * Play with result success.
    * */
    boolean play();

    /*
    * Pause current track.
    * */
    boolean pause();

    /*
    * Next with result, has next.
    * */
    boolean next();

    /*
    * Previous with result, has previous.
    * */
    boolean previous();

    /*
    * Seek to position.
    * */
    boolean seek(int position);

    /*
    * Stop current. Reset state.
    * */
    boolean stop();

    /*
    * Set new track
    * */
    void setTrackPath(final String path);

    /*
    * Release android media player and other resources.
    * */
    void release();
}
