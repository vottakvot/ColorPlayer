package ru.testsimpleapps.coloraudioplayer.managers.explorer

import java.util.Comparator

import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ItemData

class ItemsComparator {

    class Name(private val mSortOrder: Int) : Comparator<ItemData> {

        override fun compare(data1: ItemData, data2: ItemData): Int {
            return mSortOrder * data1.name!!.compareTo(data2.name!!)
        }
    }

    class Album(private val mSortOrder: Int) : Comparator<ItemData> {

        override fun compare(data1: ItemData, data2: ItemData): Int {
            return mSortOrder * data1.album!!.compareTo(data2.album!!)
        }
    }

    class Artist(private val mSortOrder: Int) : Comparator<ItemData> {

        override fun compare(data1: ItemData, data2: ItemData): Int {
            return mSortOrder * data1.artist!!.compareTo(data2.artist!!)
        }
    }

    class Duration(private val mSortOrder: Int) : Comparator<ItemData> {

        override fun compare(data1: ItemData, data2: ItemData): Int {
            return mSortOrder * java.lang.Long.valueOf(data1.duration).compareTo(data2.duration)
        }
    }

    class DataModified(private val mSortOrder: Int) : Comparator<ItemData> {

        override fun compare(data1: ItemData, data2: ItemData): Int {
            return mSortOrder * java.lang.Long.valueOf(data1.dateModified).compareTo(data2.dateModified)
        }
    }

    class DataAdded(private val mSortOrder: Int) : Comparator<ItemData> {

        override fun compare(data1: ItemData, data2: ItemData): Int {
            return mSortOrder * java.lang.Long.valueOf(data1.dateAdded).compareTo(data2.dateAdded)
        }
    }

    class Bitrate(private val mSortOrder: Int) : Comparator<ItemData> {

        override fun compare(data1: ItemData, data2: ItemData): Int {
            return mSortOrder * java.lang.Long.valueOf(data1.bitrate).compareTo(data2.bitrate)
        }
    }

}
