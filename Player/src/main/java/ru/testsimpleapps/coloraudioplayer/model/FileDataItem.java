package ru.testsimpleapps.coloraudioplayer.model;

public class FileDataItem {

    private boolean isChecked = false;
    private String name;
    private long id;
    private long duration;

    public FileDataItem(boolean isChecked, String name, long id, long duration) {
        this.isChecked = isChecked;
        this.name = name;
        this.id = id;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setId(long id) {
        this.id = id;
    }
}
