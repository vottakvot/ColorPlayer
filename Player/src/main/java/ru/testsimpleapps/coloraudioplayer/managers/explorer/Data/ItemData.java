package ru.testsimpleapps.coloraudioplayer.managers.explorer.Data;

public class ItemData {

    private boolean isChecked = false;

    private String path;
    private String name;
    private String folder;
    private String album;
    private String artist;
    private long id;
    private long duration;
    private long dateModified;
    private long dateAdded;
    private long bitrate;

    public ItemData(final String path,
                    final String name,
                    final String folder,
                    final String album,
                    final String artist,
                    final long id,
                    final long duration,
                    final long dateModified,
                    final long dateAdded,
                    final long bitrate) {
        this.path = path;
        this.name = name;
        this.folder = folder;
        this.album = album;
        this.artist = artist;
        this.id = id;
        this.duration = duration;
        this.dateModified = dateModified;
        this.dateAdded = dateAdded;
        this.bitrate = bitrate;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getBitrate() {
        return bitrate;
    }

    public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }


}