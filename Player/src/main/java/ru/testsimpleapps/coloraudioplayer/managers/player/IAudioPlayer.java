package ru.testsimpleapps.coloraudioplayer.managers.player;

import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;

public interface IAudioPlayer {

    /*
    * Set player config
    * */
    void setConfig(PlayerConfig playerConfig);

    /*
    * Get player config
    * */
    PlayerConfig getConfig();

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
    * Release android media player and other resources.
    * */
    void release();
}
