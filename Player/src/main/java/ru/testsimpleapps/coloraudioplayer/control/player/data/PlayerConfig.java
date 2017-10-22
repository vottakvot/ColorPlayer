package ru.testsimpleapps.coloraudioplayer.control.player.data;

import android.os.Parcel;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

import java.io.Serializable;
import ru.testsimpleapps.coloraudioplayer.control.player.playlist.IPlaylist;

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
    * For mPlaylist. If null you must set track identifier by method.
    * */
    @Nullable
    private transient IPlaylist mPlaylist;

    /*
    * Track path.
    * */
    @Nullable
    private String mTrackPath;

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
    private short [] mEqualizerBands;
    private short mBassBoostStrength = 0;


    public PlayerConfig(){
        this(false, Repeat.NONE, DEFAULT_SEEK_POSITION, null, null, (short)0, null, (short)0, null);
    }

    public PlayerConfig(PlayerConfig playerConfig){
        this(playerConfig.isRandom(),
                playerConfig.getRepeat(),
                playerConfig.getLastSeekPosition(),
                playerConfig.getPlaylist(),
                playerConfig.getTrackPath(),
                playerConfig.getEqualizerPresent(),
                playerConfig.getEqualizerBands(),
                playerConfig.getBassBoostStrength(),
                playerConfig.getPlaylistSort());
    }

    public PlayerConfig(boolean isRandom,
                        Repeat repeat,
                        int lastSeekPosition,
                        @Nullable IPlaylist playlist,
                        @Nullable String trackPath,
                        short equalizerPresent,
                        short [] equalizerBands,
                        short bassBoostStrength,
                        String playlistSort) {

        mIsRandom = isRandom;
        mRepeat = repeat;
        mLastSeekPosition = lastSeekPosition;
        mTrackPath = trackPath;
        mEqualizerPresent = equalizerPresent;
        mEqualizerBands = equalizerBands;
        mBassBoostStrength = bassBoostStrength;
        mPlaylistSort = playlistSort;
        setPlaylist(playlist);
    }

    protected PlayerConfig(Parcel in) {
        mPlaylistId = in.readLong();
        mTrackId = in.readLong();
        mIsRandom = in.readByte() != 0;
        mLastSeekPosition = in.readInt();
        mTrackPath = in.readString();
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
        this.mRepeat = repeat;
    }

    public int getLastSeekPosition() {
        int copySeekPosition = mLastSeekPosition;
        mLastSeekPosition = DEFAULT_SEEK_POSITION;
        return copySeekPosition;
    }

    public void setLastSeekPosition(int lastSeekPosition) {
        this.mLastSeekPosition = lastSeekPosition;
    }

    @Nullable
    public IPlaylist getPlaylist() {
        return mPlaylist;
    }

    public void setPlaylist(@Nullable IPlaylist playlist) {
        this.mPlaylist = playlist;
        setTrackPathFromPlaylist();
    }

    @Nullable
    public String getTrackPath() {
        return mTrackPath;
    }

    @Nullable
    public void setTrackPathFromPlaylist() {
        if(mPlaylist != null)
            mTrackPath = String.valueOf(mPlaylist.getTrackPath());
    }

    public void setTrackPath(@Nullable String trackPath) {
        this.mTrackPath = trackPath;
    }

    public long getPlaylistId() {
        return mPlaylistId;
    }

    public void setPlaylistId(long playlistId) {
        this.mPlaylistId = playlistId;
    }

    public long getTrackId() {
        return mTrackId;
    }

    public void setTrackId(long trackId) {
        this.mTrackId = trackId;
    }

    public short getEqualizerPresent() {
        return mEqualizerPresent;
    }

    public void setEqualizerPresent(short equalizerPresent) {
        this.mEqualizerPresent = equalizerPresent;
    }

    public short[] getEqualizerBands() {
        return mEqualizerBands;
    }

    public void setEqualizerBands(short[] equalizerBands) {
        this.mEqualizerBands = equalizerBands;
    }

    public short getBassBoostStrength() {
        return mBassBoostStrength;
    }

    public void setBassBoostStrength(short bassBoostStrength) {
        this.mBassBoostStrength = bassBoostStrength;
    }

    public String getPlaylistSort() {
        return mPlaylistSort;
    }

    public void setPlaylistSort(String mPlaylistSort) {
        this.mPlaylistSort = mPlaylistSort;
    }
}
