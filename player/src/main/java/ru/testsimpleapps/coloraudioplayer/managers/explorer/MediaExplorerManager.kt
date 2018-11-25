package ru.testsimpleapps.coloraudioplayer.managers.explorer


import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Log

import java.lang.ref.WeakReference
import java.util.ArrayList

import ru.testsimpleapps.coloraudioplayer.app.App
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ItemData
import ru.testsimpleapps.coloraudioplayer.managers.tools.FileTool
import ru.testsimpleapps.coloraudioplayer.managers.tools.MathTool
import ru.testsimpleapps.coloraudioplayer.managers.tools.TextTool


class MediaExplorerManager private constructor(private val mContext: Context) {

    /*
    * Objects for work
    * */
    private var mOnDataReadyWeakReference: WeakReference<OnDataReady>? = null
    private var mAsyncFind: AsyncFind? = null
    @Volatile
    @get:Synchronized
    var isProcessing = false
        private set
    private val mFoldersList: MutableList<FolderData>
    private val mArtistList: MutableList<FolderData>
    private val mAlbumsList: MutableList<FolderData>

    @get:Synchronized
    var totalTime: Long = 0
        private set
    @get:Synchronized
    var totalTracks: Long = 0
        private set

    val folders: List<FolderData>
        @Synchronized get() = mFoldersList

    val artists: List<FolderData>
        @Synchronized get() = mArtistList

    val albums: List<FolderData>
        @Synchronized get() = mAlbumsList

    interface OnDataReady {
        fun onSuccess()
        fun onError()
    }

    init {
        mFoldersList = ArrayList()
        mArtistList = ArrayList()
        mAlbumsList = ArrayList()
    }

    @Synchronized
    fun findMedia(): Boolean {
        var cursor: Cursor? = null
        try {
            // Get user media files
            cursor = mContext.contentResolver.query(MEDIA_USER,
                    arrayOf(MEDIA_ID, MEDIA_TITLE, MEDIA_PATH, MEDIA_ARTIST, MEDIA_ALBUMS, MEDIA_DURATION, MEDIA_DATE_MODIFIED, MEDIA_DATE_ADDED, MEDIA_SIZE),
                    MEDIA_TYPE, null,
                    MEDIA_NAME_ORDER)

            // Get media data info
            mArtistList.clear()
            mAlbumsList.clear()
            mFoldersList.clear()
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    try {
                        val id = cursor.getLong(cursor.getColumnIndex(MEDIA_ID))
                        val path = cursor.getString(cursor.getColumnIndex(MEDIA_PATH))
                        var folder = FileTool.getFolderName(path)
                        if (TextTool.isBadName(folder!!)) {
                            folder = FileTool.getComplexName(path)
                        }

                        val name = cursor.getString(cursor.getColumnIndex(MEDIA_TITLE))
                        val artist = cursor.getString(cursor.getColumnIndex(MEDIA_ARTIST))
                        val album = cursor.getString(cursor.getColumnIndex(MEDIA_ALBUMS))
                        val duration = cursor.getLong(cursor.getColumnIndex(MEDIA_DURATION))
                        val dateModified = cursor.getLong(cursor.getColumnIndex(MEDIA_DATE_MODIFIED))
                        val dateAdded = cursor.getLong(cursor.getColumnIndex(MEDIA_DATE_ADDED))
                        val size = cursor.getLong(cursor.getColumnIndex(MEDIA_SIZE))
                        val bitrate = MathTool.roundToTenths(size * 8 / duration)

                        val dataItem = ItemData(path, name, folder, album, artist, id,
                                duration, dateModified, dateAdded, bitrate)

                        // For artist grouping
                        var artistsContainer = findFolderByName(mArtistList, artist)
                        if (artistsContainer == null) {
                            artistsContainer = FolderData(artist)
                            mArtistList.add(artistsContainer)
                        }
                        artistsContainer.addItem(dataItem)

                        // For albums grouping
                        var albumContainer = findFolderByName(mAlbumsList, album)
                        if (albumContainer == null) {
                            albumContainer = FolderData(album)
                            mAlbumsList.add(albumContainer)
                        }
                        albumContainer.addItem(dataItem)


                        // For folder grouping
                        var folderContainer = findFolderByName(mFoldersList, folder)
                        if (folderContainer == null) {
                            folderContainer = FolderData(folder)
                            mFoldersList.add(folderContainer)
                        }
                        folderContainer.addItem(dataItem)

                        totalTime += duration
                        totalTracks++
                    } catch (e: RuntimeException) {
                        Log.e(TAG, e.message)
                    }

                }
            }
        } catch (e: RuntimeException) {
            Log.e(TAG, e.message)
            return false
        } finally {
            if (cursor != null && !cursor.isClosed)
                cursor.close()
        }
        return true
    }

    private fun findFolderByName(folderData: List<FolderData>, name: String?): FolderData? {
        for (item in folderData) {
            if (item.name!!.equals(name!!, ignoreCase = true)) {
                return item
            }
        }
        return null
    }

    @Synchronized
    fun findMediaAsync() {
        if (mAsyncFind != null && !mAsyncFind!!.isCancelled) {
            mAsyncFind!!.cancel(true)
        }

        mAsyncFind = AsyncFind()
        mAsyncFind!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    @Synchronized
    fun setFindCallback(onDataReady: OnDataReady) {
        mOnDataReadyWeakReference = WeakReference(onDataReady)
    }

    @Synchronized
    fun removeFindCallback() {
        mOnDataReadyWeakReference = null
    }

    private inner class AsyncFind : AsyncTask<Void, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            isProcessing = true
        }

        override fun doInBackground(objects: Array<Void>): Boolean? {
            return findMedia()
        }

        override fun onPostExecute(isSuccess: Boolean) {
            super.onPostExecute(isSuccess)
            isProcessing = false
            val onDataReadyWeakReference = mOnDataReadyWeakReference
            if (onDataReadyWeakReference != null) {
                val onDataReady = onDataReadyWeakReference.get()
                if (onDataReady != null) {
                    if (isSuccess) {
                        onDataReady.onSuccess()
                    } else {
                        onDataReady.onError()
                    }
                }
            }
        }

    }

    companion object {

        val TAG = MediaExplorerManager::class.java.simpleName
        private var sMediaExplorerManager: MediaExplorerManager? = null

        val instance: MediaExplorerManager
            get() {
                if (sMediaExplorerManager == null) {
                    sMediaExplorerManager = MediaExplorerManager(App.instance)
                }

                return sMediaExplorerManager!!
            }

        /*
    * Common file constant
    * */
        private val MEDIA_ID = MediaStore.Audio.Media._ID
        private val MEDIA_TITLE = MediaStore.Audio.Media.TITLE
        private val MEDIA_PATH = MediaStore.Audio.Media.DATA
        private val MEDIA_ALBUMS = MediaStore.Audio.Media.ALBUM
        private val MEDIA_ARTIST = MediaStore.Audio.Media.ARTIST
        private val MEDIA_DURATION = MediaStore.Audio.Media.DURATION
        private val MEDIA_DATE_MODIFIED = MediaStore.Audio.Media.DATE_MODIFIED
        private val MEDIA_DATE_ADDED = MediaStore.Audio.Media.DATE_ADDED
        private val MEDIA_SIZE = MediaStore.Audio.Media.SIZE

        private val MEDIA_USER = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        private val MEDIA_TYPE = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        private val MEDIA_NAME_ORDER = MediaStore.Audio.Media.TITLE + " ASC"
    }

}
