package ru.testsimpleapps.coloraudioplayer.managers.explorer


import java.util.Comparator

import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData

class FoldersComparator {

    class Name(private val mSortOrder: Int) : Comparator<FolderData> {

        override fun compare(data1: FolderData, data2: FolderData): Int {
            return mSortOrder * data1.name!!.compareTo(data2.name!!)
        }
    }

    class Size(private val mSortOrder: Int) : Comparator<FolderData> {

        override fun compare(data1: FolderData, data2: FolderData): Int {
            return mSortOrder * Integer.valueOf(data1.size()).compareTo(data2.size())
        }
    }

}
