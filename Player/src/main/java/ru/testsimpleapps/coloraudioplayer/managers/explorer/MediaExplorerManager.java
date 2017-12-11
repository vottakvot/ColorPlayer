package ru.testsimpleapps.coloraudioplayer.managers.explorer;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ItemData;
import ru.testsimpleapps.coloraudioplayer.managers.tools.FileTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.MathTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.TextTool;


public class MediaExplorerManager {

    public static final String TAG = MediaExplorerManager.class.getSimpleName();
    private static MediaExplorerManager sMediaExplorerManager;

    public interface OnDataReady {
        void onSuccess();
        void onError();
    }

    public static MediaExplorerManager getInstance() {
        if (sMediaExplorerManager == null) {
            sMediaExplorerManager = new MediaExplorerManager(App.getContext());
        }

        return sMediaExplorerManager;
    }

    /*
    * Common file constant
    * */
    private final static String MEDIA_ID = MediaStore.Audio.Media._ID;
    private final static String MEDIA_TITLE = MediaStore.Audio.Media.TITLE;
    private final static String MEDIA_PATH = MediaStore.Audio.Media.DATA;
    private final static String MEDIA_ALBUMS = MediaStore.Audio.Media.ALBUM;
    private final static String MEDIA_ARTIST = MediaStore.Audio.Media.ARTIST;
    private final static String MEDIA_DURATION = MediaStore.Audio.Media.DURATION;
    private final static String MEDIA_DATE_MODIFIED = MediaStore.Audio.Media.DATE_MODIFIED;
    private final static String MEDIA_DATE_ADDED = MediaStore.Audio.Media.DATE_ADDED;
    private final static String MEDIA_SIZE = MediaStore.Audio.Media.SIZE;

    private final static Uri MEDIA_USER = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private final static String MEDIA_TYPE = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    private final static String MEDIA_NAME_ORDER = MediaStore.Audio.Media.TITLE + " ASC";

    /*
    * Objects for work
    * */
    private WeakReference<OnDataReady> mOnDataReadyWeakReference;
    private AsyncFind mAsyncFind;
    private volatile boolean mIsProcessing = false;

    private final Context mContext;
    private List<FolderData> mFoldersList;
    private List<FolderData> mArtistList;
    private List<FolderData> mAlbumsList;

    private long mTotalTime;
    private long mTotalTracks;

    private MediaExplorerManager(Context context) {
        mContext = context;
        mFoldersList = new ArrayList<>();
        mArtistList = new ArrayList<>();
        mAlbumsList = new ArrayList<>();
    }

    public synchronized boolean findMedia() {
        Cursor cursor = null;
        try {
            // Get user media files
            cursor = mContext.getContentResolver().query(MEDIA_USER,
                    new String[]{MEDIA_ID,
                            MEDIA_TITLE,
                            MEDIA_PATH,
                            MEDIA_ARTIST,
                            MEDIA_ALBUMS,
                            MEDIA_DURATION,
                            MEDIA_DATE_MODIFIED,
                            MEDIA_DATE_ADDED,
                            MEDIA_SIZE},
                    MEDIA_TYPE,
                    null,
                    MEDIA_NAME_ORDER);

            // Get media data info
            mArtistList.clear();
            mAlbumsList.clear();
            mFoldersList.clear();
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    try {
                        final long id = cursor.getLong(cursor.getColumnIndex(MEDIA_ID));
                        final String path = cursor.getString(cursor.getColumnIndex(MEDIA_PATH));
                        String folder = FileTool.getFolderName(path);
                        if (TextTool.isBadName(folder)) {
                            folder = FileTool.getComplexName(path);
                        }

                        final String name = cursor.getString(cursor.getColumnIndex(MEDIA_TITLE));
                        final String artist = cursor.getString(cursor.getColumnIndex(MEDIA_ARTIST));
                        final String album = cursor.getString(cursor.getColumnIndex(MEDIA_ALBUMS));
                        final long duration = cursor.getLong(cursor.getColumnIndex(MEDIA_DURATION));
                        final long dateModified = cursor.getLong(cursor.getColumnIndex(MEDIA_DATE_MODIFIED));
                        final long dateAdded = cursor.getLong(cursor.getColumnIndex(MEDIA_DATE_ADDED));
                        final long size = cursor.getLong(cursor.getColumnIndex(MEDIA_SIZE));
                        final long bitrate = MathTool.roundToTenths(size * 8 / duration);

                        final ItemData dataItem = new ItemData(path, name, folder, album, artist, id,
                                duration, dateModified, dateAdded, bitrate);

                        // For artist grouping
                        FolderData artistsContainer = findFolderByName(mArtistList, artist);
                        if (artistsContainer == null) {
                            artistsContainer = new FolderData(artist);
                            mArtistList.add(artistsContainer);
                        }
                        artistsContainer.addItem(dataItem);

                        // For albums grouping
                        FolderData albumContainer = findFolderByName(mAlbumsList, album);
                        if (albumContainer == null) {
                            albumContainer = new FolderData(album);
                            mAlbumsList.add(albumContainer);
                        }
                        albumContainer.addItem(dataItem);


                        // For folder grouping
                        FolderData folderContainer = findFolderByName(mFoldersList, folder);
                        if (folderContainer == null) {
                            folderContainer = new FolderData(folder);
                            mFoldersList.add(folderContainer);
                        }
                        folderContainer.addItem(dataItem);

                        mTotalTime += duration;
                        mTotalTracks++;
                    } catch (RuntimeException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        } catch (RuntimeException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return true;
    }

    private FolderData findFolderByName(final List<FolderData> folderData, final String name) {
        for (FolderData item : folderData) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    synchronized public void findMediaAsync() {
        if (mAsyncFind != null && !mAsyncFind.isCancelled()) {
            mAsyncFind.cancel(true);
        }

        mAsyncFind = new AsyncFind();
        mAsyncFind.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    synchronized public void setFindCallback(@NonNull final OnDataReady onDataReady) {
        mOnDataReadyWeakReference = new WeakReference<>(onDataReady);
    }

    synchronized public void removeFindCallback() {
        mOnDataReadyWeakReference = null;
    }

    synchronized public boolean isProcessing() {
        return mIsProcessing;
    }

    private class AsyncFind extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mIsProcessing = true;
        }

        @Override
        protected Boolean doInBackground(Void[] objects) {
            return findMedia();
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            mIsProcessing = false;
            final WeakReference<OnDataReady> onDataReadyWeakReference = mOnDataReadyWeakReference;
            if (onDataReadyWeakReference != null) {
                final OnDataReady onDataReady = onDataReadyWeakReference.get();
                if (onDataReady != null) {
                    if (isSuccess.booleanValue()) {
                        onDataReady.onSuccess();
                    } else {
                        onDataReady.onError();
                    }
                }
            }
        }

    }

    synchronized public List<FolderData> getFolders() {
        return mFoldersList;
    }

    synchronized public List<FolderData> getArtists() {
        return mArtistList;
    }

    synchronized public List<FolderData> getAlbums() {
        return mAlbumsList;
    }

    synchronized public long getTotalTracks() {
        return mTotalTracks;
    }

    synchronized public long getTotalTime() {
        return mTotalTime;
    }

}
