package ru.testsimpleapps.coloraudioplayer.managers.player.data;

import android.os.Parcel;
import android.support.annotation.IntRange;

import java.io.Serializable;

public final class PlayerConfig implements Serializable {

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
    * Track id
    * */
    private long mTrackId;

    /*
    * Set order.
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


    public PlayerConfig() {
        this(false, Repeat.NONE, DEFAULT_SEEK_POSITION, (short) 0, null, (short) 0, null);
    }

    public PlayerConfig(PlayerConfig playerConfig) {
        this(playerConfig.isRandom(),
                playerConfig.getRepeat(),
                playerConfig.getLastSeekPosition(),
                playerConfig.getEqualizerPresent(),
                playerConfig.getEqualizerBands(),
                playerConfig.getBassBoostStrength(),
                playerConfig.getPlaylistSort());
    }

    public PlayerConfig(boolean isRandom,
                        Repeat repeat,
                        int lastSeekPosition,
                        short equalizerPresent,
                        short[] equalizerBands,
                        short bassBoostStrength,
                        String playlistSort) {

        mIsRandom = isRandom;
        mRepeat = repeat;
        mLastSeekPosition = lastSeekPosition;
        mEqualizerPresent = equalizerPresent;
        mEqualizerBands = equalizerBands;
        mBassBoostStrength = bassBoostStrength;
        mPlaylistSort = playlistSort;
    }

    protected PlayerConfig(Parcel in) {
        mPlaylistId = in.readLong();
        mTrackId = in.readLong();
        mIsRandom = in.readByte() != 0;
        mLastSeekPosition = in.readInt();
    }

    public boolean isRandom() {
        return mIsRandom;
    }

    public void setRandom(boolean random) {
        mIsRandom = random;
    }

    public Repeat getRepeat() {
        return mRepeat;
    }

    public void setRepeat(Repeat repeat) {
        mRepeat = repeat;
    }

    public int getLastSeekPosition() {
        int copySeekPosition = mLastSeekPosition;
        mLastSeekPosition = DEFAULT_SEEK_POSITION;
        return copySeekPosition;
    }

    public void setLastSeekPosition(int lastSeekPosition) {
        mLastSeekPosition = lastSeekPosition;
    }

    public long getPlaylistId() {
        return mPlaylistId;
    }

    public void setPlaylistId(long playlistId) {
        mPlaylistId = playlistId;
    }

    public long getTrackId() {
        return mTrackId;
    }

    public void setTrackId(long trackId) {
        mTrackId = trackId;
    }

    public short getEqualizerPresent() {
        return mEqualizerPresent;
    }

    public void setEqualizerPresent(short equalizerPresent) {
        mEqualizerPresent = equalizerPresent;
    }

    public short[] getEqualizerBands() {
        return mEqualizerBands;
    }

    public void setEqualizerBands(short[] equalizerBands) {
        mEqualizerBands = equalizerBands;
    }

    public short getBassBoostStrength() {
        return mBassBoostStrength;
    }

    public void setBassBoostStrength(short bassBoostStrength) {
        mBassBoostStrength = bassBoostStrength;
    }

    public String getPlaylistSort() {
        return mPlaylistSort;
    }

    public void setPlaylistSort(String playlistSort) {
        mPlaylistSort = playlistSort;
    }
}
