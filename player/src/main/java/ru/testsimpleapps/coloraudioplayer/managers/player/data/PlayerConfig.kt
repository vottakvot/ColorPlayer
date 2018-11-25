package ru.testsimpleapps.coloraudioplayer.managers.player.data

import java.io.Serializable

import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist
import ru.testsimpleapps.coloraudioplayer.managers.tools.CursorTool
import ru.testsimpleapps.coloraudioplayer.managers.tools.SerializableTool

class PlayerConfig private constructor(/*
    * Set order
    * */
        var isRandom: Boolean = false,
        /*
    * Playlist mRepeat mode.
    * */
        var repeat: Repeat? = Repeat.NONE,
        lastSeekPosition: Int = DEFAULT_SEEK_POSITION,
        equalizerPresent: Short = 0.toShort(),
        var equalizerBands: ShortArray? = null,
        bassBoostStrength: Short = 0.toShort(),
        /*
    * Playlist sort
    * */
        var playlistSort: String? = CursorTool.FIELD_NAME,
        /*
    * Playlist sort order
    * */
        var playlistSortOrder: String? = CursorTool.SORT_ORDER_ASC) : Serializable {

    /*
    * Playlist id
    * */
    var playlistId = IPlaylist.ERROR_CODE

    /*
    * Track id
    * */
    var trackId = IPlaylist.ERROR_CODE

    /*
    * For after app reload. Seek position in track. Only for one call. Value reset to 0 after getting.
    * */
    var lastSeekPosition: Int = 0
        get() {
            val copySeekPosition = field
            lastSeekPosition = DEFAULT_SEEK_POSITION
            return copySeekPosition
        }


    /*
    * Audio effects
    * */
    @Transient
    var audioSession = 0
    var equalizerPresent: Short = 0
    var bassBoostStrength: Short = 0

    /*
    * Variants for mRepeat.
    * */
    enum class Repeat : Serializable {
        ALL, ONE, NONE
    }

    private constructor(playerConfig: PlayerConfig) : this(playerConfig.isRandom,
            playerConfig.repeat,
            playerConfig.lastSeekPosition,
            playerConfig.equalizerPresent,
            playerConfig.equalizerBands,
            playerConfig.bassBoostStrength,
            playerConfig.playlistSort,
            playerConfig.playlistSortOrder) {
    }

    init {
        this.lastSeekPosition = lastSeekPosition
        this.equalizerPresent = equalizerPresent
        this.bassBoostStrength = bassBoostStrength
    }

    fun setRandom(): Boolean {
        return true;
//        return isRandom = !isRandom
    }

    fun setRepeat(): Repeat {
        if (repeat == Repeat.ALL) {
            repeat = Repeat.ONE
        } else if (repeat == Repeat.ONE) {
            repeat = Repeat.NONE
        } else {
            repeat = Repeat.ALL
        }

        return repeat!!
    }

    companion object {

        val TAG = PlayerConfig::class.java.simpleName
        @Volatile
        private var sPlayerConfig: PlayerConfig? = null

        val instance: PlayerConfig
            get() {
                var localInstance = sPlayerConfig
                if (localInstance == null) {
                    synchronized(PlayerConfig::class.java) {
                        localInstance = sPlayerConfig
                        if (localInstance == null) {
                            val `object` = SerializableTool.fileToObject(TAG)
                            if (`object` is PlayerConfig) {
                                localInstance = `object`
                                sPlayerConfig = localInstance
                            } else {
                                localInstance = PlayerConfig()
                                sPlayerConfig = localInstance
                            }
                        }
                    }
                }

                return localInstance!!
            }

        fun save() {
            var localInstance = sPlayerConfig
            if (localInstance != null) {
                synchronized(PlayerConfig::class.java) {
                    localInstance = sPlayerConfig
                    if (localInstance != null) {
                        SerializableTool.objectToFile(TAG, localInstance!!)
                    }
                }
            }
        }

        /*
    * Default seek position for init 0.
    * */
        val DEFAULT_SEEK_POSITION = 0
    }
}
