package ru.testsimpleapps.coloraudioplayer.managers.explorer;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MediaExplorer {

    public static final String TAG = MediaExplorer.class.getSimpleName();

    /*
    * Get file duration
    * */
    private static final String PARSE_DATE = "HH:mm:ss";
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(PARSE_DATE);

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
    private final static int INIT_CAPACITY = 64;
    private final static float LOAD_FACTOR = 0.75f;

    /*
    * Objects for work
    * */
    private final Context mContext;
    private final ContentResolver mContentResolver;
    private LinkedHashMap<String, DataContainer<DataItem>> mFoldersMediaList;
    private LinkedHashMap<String, DataContainer<DataItem>> mArtistMediaList;
    private LinkedHashMap<String, DataContainer<DataItem>> mAlbumsMediaList;

    public MediaExplorer(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
        mFoldersMediaList = new LinkedHashMap<>(INIT_CAPACITY, LOAD_FACTOR);
        mArtistMediaList = new LinkedHashMap<>(INIT_CAPACITY, LOAD_FACTOR);
        mAlbumsMediaList = new LinkedHashMap<>(INIT_CAPACITY, LOAD_FACTOR);
    }

    /**
     * This is template for get key prom map.
     *
     * @param mediaTree - map object.
     * @param position  - get key from this positon
     * @return A - return key
     */
    public static <A extends Object, B extends Object> A getMapMediaPosition(Map<A, B> mediaTree, int position) {
        int i = 0;

        if (0 <= position && mediaTree != null && position < mediaTree.size())
            for (Map.Entry<A, B> item : mediaTree.entrySet()) {
                if (i++ == position)
                    return item.getKey();
            }

        return null;
    }

    /**
     * This is template for get key prom set.
     *
     * @param mediaTree - set object.
     * @param position  - get key from this positon
     * @return A - return key
     */
    public static <A extends Object> A getKeyMediaPosition(Set<A> mediaTree, int position) {
        int i = 0;

        if (0 <= position && mediaTree != null && position < mediaTree.size())
            for (A item : mediaTree) {
                if (i++ == position)
                    return item;
            }

        return null;
    }


    public static String getFolder(String path) {
        if (path != null) {
            List<String> pathSegment = Uri.parse(path).getPathSegments();
            return (pathSegment.size() > 1) ? pathSegment.get(pathSegment.size() - 2) : null;
        }

        return null;
    }

    public static String getByPosition(String path, int position) {
        if (path != null) {
            List<String> pathSegment = Uri.parse(path).getPathSegments();
            return (position > 0 && pathSegment.size() >= position) ? pathSegment.get(pathSegment.size() - position) : null;
        }

        return null;
    }

    /**
     * This method use for get string time from long
     *
     * @param ms - time in ms.
     * @return A - return string time
     */
    @Deprecated
    public static String getDuration(long ms) {
        return ((ms / (1000 * 60 * 60)) != 0 ? String.format("%01d", (ms / (1000 * 60 * 60))) + ":" : "") +
                String.format("%02d", (ms / (1000 * 60)) % 60) + ":" +
                String.format("%02d", (ms / 1000) % 60);
    }

    public static String getDuration2(final long time) {
        return mSimpleDateFormat.format(new Date(time));
    }

    /**
     * If this is a simple name, like as "1 or CD3" - concat with previous folder.
     *
     * @param path - path to media file
     * @return String - return full name
     */
    public static String getComplexName(String path) {
        return (path != null ? getByPosition(path, 3).toString() + " - " + getByPosition(path, 2).toString() : null);
    }

    public static boolean isNumeric(String number) {
        return (number != null && number.trim().matches("^[0-9]*$") ? true : false);
    }


    /**
     * Check if this is a simple name
     *
     *
     *
     * @param number - path to media file
     * @return boolean - true if this is simple name
     */
    public static boolean isBadName(String number) {
        return (number != null && number.trim().matches("^[0-9]*|[a-zA-Z]{1,2}\\s*[0-9]*|[0-9]*\\s*[a-zA-Z]{1,2}$") ? true : false);
    }


    /**
     * Check if this is a simple name
     *
     * @return boolean - true if this complete without errors
     */
    public boolean findMedia() {
        Cursor cursor = null;
        try {
            // Get user media files
            cursor = mContentResolver.query(MEDIA_USER,
                    new String[] {  MEDIA_ID,
                                    MEDIA_TITLE,
                                    MEDIA_PATH,
                                    MEDIA_ARTIST,
                                    MEDIA_ALBUMS,
                                    MEDIA_DURATION,
                                    MEDIA_DATE_MODIFIED,
                                    MEDIA_DATE_ADDED,
                                    MEDIA_SIZE },
                    MEDIA_TYPE,
                    null,
                    MEDIA_NAME_ORDER);

            // Get media data info
            mArtistMediaList.clear();
            mAlbumsMediaList.clear();
            mFoldersMediaList.clear();
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    try {
                        final long id = cursor.getLong(cursor.getColumnIndex(MEDIA_ID));
                        final String path = cursor.getString(cursor.getColumnIndex(MEDIA_PATH));
                        String folder = getFolder(path);
                        if (isBadName(folder)) {
                            folder = getComplexName(path);
                        }

                        final String name = cursor.getString(cursor.getColumnIndex(MEDIA_TITLE));
                        final String artist = cursor.getString(cursor.getColumnIndex(MEDIA_ARTIST));
                        final String album = cursor.getString(cursor.getColumnIndex(MEDIA_ALBUMS));
                        final long duration = cursor.getLong(cursor.getColumnIndex(MEDIA_DURATION));
                        final long dateModified = cursor.getLong(cursor.getColumnIndex(MEDIA_DATE_MODIFIED));
                        final long dateAdded = cursor.getLong(cursor.getColumnIndex(MEDIA_DATE_ADDED));
                        final long size = cursor.getLong(cursor.getColumnIndex(MEDIA_SIZE));
                        final long bitrate = roundToTenths(size * 8 / duration);

                        final DataItem dataItem = new DataItem(path, name, folder, album, artist, id,
                                duration, dateModified, dateAdded, bitrate);

                        // For artist grouping
                        DataContainer<DataItem> artistsContainer = mArtistMediaList.get(artist);
                        if (artistsContainer == null) {
                            artistsContainer = new DataContainer<>();
                            mArtistMediaList.put(artist, artistsContainer);
                        }
                        artistsContainer.add(dataItem);

                        // For albums grouping
                        DataContainer<DataItem> albumsContainer = mAlbumsMediaList.get(album);
                        if (albumsContainer == null) {
                            albumsContainer = new DataContainer<>();
                            mAlbumsMediaList.put(album, albumsContainer);
                        }
                        albumsContainer.add(dataItem);

                        // For folder grouping
                        DataContainer<DataItem> foldersContainer = mFoldersMediaList.get(folder);
                        if (foldersContainer == null) {
                            foldersContainer = new DataContainer<>();
                            mFoldersMediaList.put(folder, foldersContainer);
                        }
                        foldersContainer.add(dataItem);

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

    public static String getName(String path) {
        if (path != null)
            return new File(path).getName();
        return null;
    }

    public static long roundToTenths(final long value) {
        return ((value + 5) / 10) * 10;
    }

    public LinkedHashMap<String, DataContainer<DataItem>> getArtists() {
        return mArtistMediaList;
    }

}
