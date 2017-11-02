package ru.testsimpleapps.coloraudioplayer.managers.explorer;

import java.util.ArrayList;
import java.util.List;

public class DataContainer<Item> {

    private final List<Item> mFolders;
    private boolean mIsChecked = false;
    private final String mContainerName;

    public DataContainer(final String containerName) {
        mContainerName = containerName;
        mFolders = new ArrayList<>();
    }

    public Item get(final int index) {
        if (index < 0 || index >= mFolders.size()) {
            return null;
        }
        return mFolders.get(index);
    }

    public boolean add(final Item item) {
        return mFolders.add(item);
    }

    public void clear() {
        mFolders.clear();
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        this.mIsChecked = checked;
    }

    public String getContainerName() {
        return mContainerName;
    }
}
