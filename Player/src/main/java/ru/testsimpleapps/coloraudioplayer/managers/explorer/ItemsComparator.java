package ru.testsimpleapps.coloraudioplayer.managers.explorer;

import java.util.Comparator;

import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ItemData;

public class ItemsComparator {

    public static class Name implements Comparator<ItemData> {

        private final int mSortOrder;

        public Name(final int sortOrder) {
            mSortOrder = sortOrder;
        }

        @Override
        public int compare(ItemData data1, ItemData data2) {
            return mSortOrder * data1.getName().compareTo(data2.getName());
        }
    }

    public static class Album implements Comparator<ItemData> {

        private final int mSortOrder;

        public Album(final int sortOrder) {
            mSortOrder = sortOrder;
        }

        @Override
        public int compare(ItemData data1, ItemData data2) {
            return mSortOrder * data1.getAlbum().compareTo(data2.getAlbum());
        }
    }

    public static class Artist implements Comparator<ItemData> {

        private final int mSortOrder;

        public Artist(final int sortOrder) {
            mSortOrder = sortOrder;
        }

        @Override
        public int compare(ItemData data1, ItemData data2) {
            return mSortOrder * data1.getArtist().compareTo(data2.getArtist());
        }
    }

    public static class Duration implements Comparator<ItemData> {

        private final int mSortOrder;

        public Duration(final int sortOrder) {
            mSortOrder = sortOrder;
        }

        @Override
        public int compare(ItemData data1, ItemData data2) {
            return mSortOrder * Long.valueOf(data1.getDuration()).compareTo(data2.getDuration());
        }
    }

    public static class DataModified implements Comparator<ItemData> {

        private final int mSortOrder;

        public DataModified(final int sortOrder) {
            mSortOrder = sortOrder;
        }

        @Override
        public int compare(ItemData data1, ItemData data2) {
            return mSortOrder * Long.valueOf(data1.getDateModified()).compareTo(data2.getDateModified());
        }
    }

    public static class DataAdded implements Comparator<ItemData> {

        private final int mSortOrder;

        public DataAdded(final int sortOrder) {
            mSortOrder = sortOrder;
        }

        @Override
        public int compare(ItemData data1, ItemData data2) {
            return mSortOrder * Long.valueOf(data1.getDateAdded()).compareTo(data2.getDateAdded());
        }
    }

    public static class Bitrate implements Comparator<ItemData> {

        private final int mSortOrder;

        public Bitrate(final int sortOrder) {
            mSortOrder = sortOrder;
        }

        @Override
        public int compare(ItemData data1, ItemData data2) {
            return mSortOrder * Long.valueOf(data1.getBitrate()).compareTo(data2.getBitrate());
        }
    }

}
