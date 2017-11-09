package ru.testsimpleapps.coloraudioplayer.managers.explorer;


import java.util.Comparator;

import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData;

public class FoldersComparator {

    public static class Name implements Comparator<FolderData> {
        @Override
        public int compare(FolderData data1, FolderData data2) {
            return data1.getName().compareTo(data2.getName());
        }
    }

    public static class Size implements Comparator<FolderData> {
        @Override
        public int compare(FolderData data1, FolderData data2) {
            return Integer.valueOf(data1.size()).compareTo(data2.size());
        }
    }

}
