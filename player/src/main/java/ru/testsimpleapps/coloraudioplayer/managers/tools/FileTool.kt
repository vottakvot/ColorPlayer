package ru.testsimpleapps.coloraudioplayer.managers.tools

import android.content.Context
import android.net.Uri
import android.os.Environment

import java.io.File


object FileTool {

    fun getFileName(path: String?): String? {
        return if (path != null) File(path).name else null
    }

    fun getFolderName(path: String?): String? {
        return if (path != null) File(path).parentFile.name else null
    }

    fun getComplexName(path: String?): String? {
        if (path != null) {
            val pathSegment = Uri.parse(path).pathSegments
            if (pathSegment != null && pathSegment.size > 1) {
                return pathSegment[pathSegment.size - 2] + pathSegment[pathSegment.size - 1]
            }
        }

        return null
    }

    fun getAppPath(context: Context): String {
        return context.filesDir.absolutePath + "/"
    }

    fun getCachePath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
            context.externalCacheDir!!.path
        } else {
            context.cacheDir.path
        }
    }

}
