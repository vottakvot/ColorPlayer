package ru.testsimpleapps.coloraudioplayer.managers.explorer.Data

import java.io.Serializable

class FolderData(var name: String?) : Serializable {

    var isChecked = false
    val containerItemData: ContainerData<ItemData>

    init {
        containerItemData = ContainerData()
    }

    fun addItem(itemData: ItemData) {
        containerItemData.add(itemData)
    }

    fun size(): Int {
        return containerItemData.size()
    }

}