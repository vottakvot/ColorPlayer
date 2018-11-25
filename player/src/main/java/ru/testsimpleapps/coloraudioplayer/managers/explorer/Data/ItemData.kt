package ru.testsimpleapps.coloraudioplayer.managers.explorer.Data

import java.io.Serializable

class ItemData(var path: String?,
               var name: String?,
               var folder: String?,
               var album: String?,
               var artist: String?,
               var id: Long,
               var duration: Long,
               var dateModified: Long,
               var dateAdded: Long,
               var bitrate: Long) : Serializable {

    var isChecked = false


}