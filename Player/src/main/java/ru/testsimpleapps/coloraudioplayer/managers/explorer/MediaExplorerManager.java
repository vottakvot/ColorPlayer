package ru.testsimpleapps.coloraudioplayer.managers.explorer;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.FolderData;
import ru.testsimpleapps.coloraudioplayer.managers.explorer.Data.ItemData;
import ru.testsimpleapps.coloraudioplayer.managers.tools.FileTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.MathTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.StringTool;


public class MediaExplorerManager {

    public static final String TAG = MediaExplorerManager.class.getSimpleName();
    private static MediaExplorerManager sMediaExplorerManager;

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
    private final Context mContext;
    private List<FolderData> mFoldersList;
    private List<FolderData> mArtistList;
    private List<FolderData> mAlbumsList;

    private MediaExplorerManager(Context context) {
        mContext = context;
        mFoldersList = new ArrayList<>();
        mArtistList = new ArrayList<>();
        mAlbumsList = new ArrayList<>();
    }

    public boolean findMedia() {
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
                        if (StringTool.isBadName(folder)) {
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

    public List<FolderData> getFolders() {
        return mFoldersList;
    }

    public List<FolderData> getArtists() {
        return mArtistList;
    }

    public List<FolderData> getAlbums() {
        return mAlbumsList;
    }

}
