package ru.testsimpleapps.coloraudioplayer.managers.player.data;

import android.support.annotation.IntRange;

import java.io.Serializable;

import ru.testsimpleapps.coloraudioplayer.managers.tools.CursorTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.SerializableTool;

public final class PlayerConfig implements Serializable {

    public static final String TAG = PlayerConfig.class.getSimpleName();
    private static volatile PlayerConfig sPlayerConfig;

    public static PlayerConfig getInstance() {
        PlayerConfig localInstance = sPlayerConfig;
        if (localInstance == null) {
            synchronized (PlayerConfig.class) {
                localInstance = sPlayerConfig;
                if (localInstance == null) {
                    final Object object = SerializableTool.fileToObject(TAG);
                    if (object instanceof PlayerConfig) {
                        sPlayerConfig = localInstance = (PlayerConfig) object;
                    } else {
                        sPlayerConfig = localInstance = new PlayerConfig();
                    }
                }
            }
        }

        return localInstance;
    }

    public static void save() {
        PlayerConfig localInstance = sPlayerConfig;
        if (localInstance != null) {
            synchronized (PlayerConfig.class) {
                localInstance = sPlayerConfig;
                if (localInstance != null) {
                    SerializableTool.objectToFile(TAG, localInstance);
                }
            }
        }
    }

    /*
    * Default seek position for init 0.
    * */
    public static final int DEFAULT_SEEK_POSITION = 0;

    /*
    * Playlist id
    * */
    private long mPlaylistId;

    /*
    * Playlist sort
    * */
    private String mPlaylistSort;

    /*
    * Playlist sort order
    * */
    private String mPlaylistSortOrder;

    /*
    * Track id
    * */
    private long mTrackId;

    /*
    * Set order
    * */
    private boolean mIsRandom;

    /*
    * Playlist mRepeat mode.
    * */
    private Repeat mRepeat;

    /*
    * For after app reload. Seek position in track. Only for one call. Value reset to 0 after getting.
    * */
    @IntRange(from = DEFAULT_SEEK_POSITION, to = Integer.MAX_VALUE)
    private int mLastSeekPosition;

    /*
    * Variants for mRepeat.
    * */
    public enum Repeat implements Serializable {
        ALL, ONE, NONE
    }

    /*
    * Audio effects
    * */
    private short mEqualizerPresent = 0;
    private short[] mEqualizerBands;
    private short mBassBoostStrength = 0;


    private PlayerConfig() {
        this(false, Repeat.NONE, DEFAULT_SEEK_POSITION, (short) 0, null, (short) 0, CursorTool.FIELD_NAME, CursorTool.SORT_ORDER_ASC);
    }

    private PlayerConfig(PlayerConfig playerConfig) {
        this(playerConfig.isRandom(),
                playerConfig.getRepeat(),
                playerConfig.getLastSeekPosition(),
                playerConfig.getEqualizerPresent(),
                playerConfig.getEqualizerBands(),
                playerConfig.getBassBoostStrength(),
                playerConfig.getPlaylistSort(),
                playerConfig.getPlaylistSortOrder());
    }

    private PlayerConfig(boolean isRandom,
                        Repeat repeat,
                        int lastSeekPosition,
                        short equalizerPresent,
                        short[] equalizerBands,
                        short bassBoostStrength,
                        String playlistSort,
                        String playlistSortOrder) {

        mIsRandom = isRandom;
        mRepeat = repeat;
        mLastSeekPosition = lastSeekPosition;
        mEqualizerPresent = equalizerPresent;
        mEqualizerBands = equalizerBands;
        mBassBoostStrength = bassBoostStrength;
        mPlaylistSort = playlistSort;
        mPlaylistSortOrder = playlistSortOrder;
    }

    public boolean isRandom() {
        return mIsRandom;
    }

    public void setRandom(final boolean random) {
        mIsRandom = random;
    }

    public Repeat getRepeat() {
        return mRepeat;
    }

    public void setRepeat(final Repeat repeat) {
        mRepeat = repeat;
    }

    public int getLastSeekPosition() {
        int copySeekPosition = mLastSeekPosition;
        mLastSeekPosition = DEFAULT_SEEK_POSITION;
        return copySeekPosition;
    }

    public void setLastSeekPosition(final int lastSeekPosition) {
        mLastSeekPosition = lastSeekPosition;
    }

    public long getPlaylistId() {
        return mPlaylistId;
    }

    public void setPlaylistId(final long playlistId) {
        mPlaylistId = playlistId;
    }

    public long getTrackId() {
        return mTrackId;
    }

    public void setTrackId(final long trackId) {
        mTrackId = trackId;
    }

    public short getEqualizerPresent() {
        return mEqualizerPresent;
    }

    public void setEqualizerPresent(final short equalizerPresent) {
        mEqualizerPresent = equalizerPresent;
    }

    public short[] getEqualizerBands() {
        return mEqualizerBands;
    }

    public void setEqualizerBands(final short[] equalizerBands) {
        mEqualizerBands = equalizerBands;
    }

    public short getBassBoostStrength() {
        return mBassBoostStrength;
    }

    public void setBassBoostStrength(final short bassBoostStrength) {
        mBassBoostStrength = bassBoostStrength;
    }

    public String getPlaylistSort() {
        return mPlaylistSort;
    }

    public void setPlaylistSort(String playlistSort) {
        mPlaylistSort = playlistSort;
    }

    public String getPlaylistSortOrder() {
        return mPlaylistSortOrder;
    }

    public void setPlaylistSortOrder(String playlistSortOrder) {
        mPlaylistSortOrder = playlistSortOrder;
    }
}
