package ru.testsimpleapps.coloraudioplayer.managers.explorer.Data;

import java.io.Serializable;

public class FolderData implements Serializable {

    private boolean isChecked = false;
    private String name;
    private ContainerData<ItemData> mContainerItemData;

    public FolderData(final String name) {
        mContainerItemData = new ContainerData<>();
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addItem(final ItemData itemData) {
        mContainerItemData.add(itemData);
    }

    public ContainerData<ItemData> getContainerItemData() {
        return mContainerItemData;
    }

    public int size() {
        return mContainerItemData.size();
    }

}