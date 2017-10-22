package ru.testsimpleapps.coloraudioplayer.control.explorer;

import java.util.ArrayList;

public class FoldersArrayList<T> extends ArrayList<T> {

    private boolean isChecked = false;

    public FoldersArrayList() {
        super();
    }

    public FoldersArrayList(int capacity) {
        super(capacity);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }
}
