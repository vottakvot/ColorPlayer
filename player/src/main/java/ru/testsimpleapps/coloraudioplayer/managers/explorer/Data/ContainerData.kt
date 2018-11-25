package ru.testsimpleapps.coloraudioplayer.managers.explorer.Data

import java.io.Serializable
import java.util.ArrayList

class ContainerData<Item> : Serializable {

    private val mContainer: MutableList<Item>

    val list: List<Item>
        get() = mContainer

    init {
        mContainer = ArrayList()
    }

    operator fun get(index: Int): Item? {
        return if (index < 0 || index >= mContainer.size) {
            null
        } else mContainer[index]
    }

    fun add(item: Item): Boolean {
        return mContainer.add(item)
    }

    fun clear() {
        mContainer.clear()
    }

    fun size(): Int {
        return mContainer.size
    }

}
