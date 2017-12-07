package ru.testsimpleapps.coloraudioplayer.managers.explorer;


import java.util.Comparator;

import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData;

public class FoldersComparator {

    public static class Name implements Comparator<FolderData> {

        private final int mSortOrder;

        public Name(final int sortOrder) {
            mSortOrder = sortOrder;
        }

        @Override
        public int compare(FolderData data1, FolderData data2) {
            return mSortOrder * data1.getName().compareTo(data2.getName());
        }
    }

    public static class Size implements Comparator<FolderData> {

        private final int mSortOrder;

        public Size(final int sortOrder) {
            mSortOrder = sortOrder;
        }

        @Override
        public int compare(FolderData data1, FolderData data2) {
            return mSortOrder * Integer.valueOf(data1.size()).compareTo(data2.size());
        }
    }

}
