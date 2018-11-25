package ru.testsimpleapps.coloraudioplayer.managers.player

interface IAudioPlayer {

    /*
    * Play with result success.
    * */
    fun play(): Boolean

    /*
    * Pause current track.
    * */
    fun pause(): Boolean

    /*
    * Next with result, has next.
    * */
    fun next(): Boolean

    /*
    * Previous with result, has image_previous.
    * */
    fun previous(): Boolean

    /*
    * Seek to position.
    * */
    fun seek(position: Int): Boolean

    /*
    * Stop current. Reset state.
    * */
    fun stop(): Boolean

    /*
    * Set new track
    * */
    fun setTrackPath(path: String)

    /*
    * Release android media player and other resources.
    * */
    fun release()
}
