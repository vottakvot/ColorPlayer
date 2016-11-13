package ru.playme.music_explorer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.playme.data_structure.ItemFile;

public class FindMedia {

    public final static String SORT_BY_ALBUMS = MediaStore.Audio.Media.ALBUM;
    public final static String SORT_BY_ARTIST = MediaStore.Audio.Media.ARTIST;
    public final static String SORT_BY_FOLDER = "FOLDER";
    private final String LOG_FIND_MEDIA = "JUST_PLAYER_LOG";

    private Context context = null;
    private ContentResolver contentResolver = null;

    private final Uri mediaSD = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private final Uri mediaPhone = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;

    private final String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    private final String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

    private LinkedHashMap<String, FoldersArrayList<ItemFile>> mediaTreeAlbums = null;
    private LinkedHashMap<String, FoldersArrayList<ItemFile>> mediaTreeArtists = null;
    private LinkedHashMap<String, FoldersArrayList<ItemFile>> mediaTreeFolders = null;


    public FindMedia(Context context){
        this.context = context;
        this.contentResolver = context.getContentResolver();

        mediaTreeAlbums = new LinkedHashMap<String, FoldersArrayList<ItemFile>>(32, 0.75f, false);
        mediaTreeArtists = new LinkedHashMap<String, FoldersArrayList<ItemFile>>(32, 0.75f, false);
        mediaTreeFolders = new LinkedHashMap<String, FoldersArrayList<ItemFile>>(32, 0.75f, false);
    }

    /**
     * This is template for get key prom map.
     * @param mediaTree - map object.
     * @param position - get key from this positon
     * @return A - return key
     */
    public static <A extends Object, B extends Object> A getMapMediaPosition(Map<A, B> mediaTree, int position){
        int i = 0;

        if(0 <= position && mediaTree != null && position < mediaTree.size())
            for(Map.Entry<A, B> item : mediaTree.entrySet()){
                if(i++ == position)
                    return item.getKey();
            }

        return null;
    }

    /**
     * This is template for get key prom set.
     * @param mediaTree - set object.
     * @param position - get key from this positon
     * @return A - return key
     */
    public static <A extends Object>A getKeyMediaPosition(Set<A> mediaTree, int position){
        int i = 0;

        if(0 <= position && mediaTree != null && position < mediaTree.size())
            for(A item : mediaTree){
                if(i++ == position)
                    return item;
            }

        return null;
    }

    public static String getName(String path){
        if(path != null){
            List<String> pathSegment = Uri.parse(path).getPathSegments();
            return (pathSegment.size() > 0)? pathSegment.get(pathSegment.size() - 1) : null;
        }

        return null;
    }

    public static String getFolder(String path){
        if(path != null){
            List<String> pathSegment = Uri.parse(path).getPathSegments();
            return (pathSegment.size() > 1)? pathSegment.get(pathSegment.size() - 2) : null;
        }

        return null;
    }

    public static String getByPosition(String path, int position){
        if(path != null){
            List<String> pathSegment = Uri.parse(path).getPathSegments();
            return (position > 0 && pathSegment.size() >= position)? pathSegment.get(pathSegment.size() - position) : null;
        }

        return null;
    }

    /**
     * This method use for get string time from long
     * @param ms - time in ms.
     * @return A - return string time
     */
    public static String getDuration(long ms){
        return ((ms / (1000*60*60)) != 0? String.format("%01d", (ms / (1000*60*60))) + ":" : "") +
                String.format("%02d", (ms / (1000*60)) % 60) + ":" +
                String.format("%02d", (ms / 1000) % 60);
    }

    /**
     * If this is a simple name, like as "1 or CD3" - concat with previous folder.
     * @param path - path to media file
     * @return String - return full name
     */
    public static String getComplexName(String path){
        return (path != null? FindMedia.getByPosition(path, 3).toString() + " - " + FindMedia.getByPosition(path, 2).toString() : null);
    }

    public static boolean isNumeric(String number){
        return (number != null && number.trim().matches("^[0-9]*$")? true : false);
    }


    /**
     * Check if this is a simple name
     * @param number - path to media file
     * @return boolean - true if this is simple name
     */
    public static boolean isBadName(String number){
        return (number != null && number.trim().matches("^[0-9]*|[a-zA-Z]{1,2}\\s*[0-9]*|[0-9]*\\s*[a-zA-Z]{1,2}$")? true : false);
    }


    /**
     * Check if this is a simple name
     * @return boolean - true if this complete without errors
     */
    public boolean findMedia() {
        Cursor cursor = null;
        try {

            cursor = contentResolver.query(  mediaSD,
                    new String[]{   MediaStore.Audio.Media.DATA,
                                    MediaStore.Audio.Media.ARTIST,
                                    MediaStore.Audio.Media.ALBUM,
                                    MediaStore.Audio.Media.DURATION,
                                    MediaStore.Audio.Media._ID},
                    selection,
                    null,
                    sortOrder);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    // ALBUMS
                    if(mediaTreeAlbums.containsKey(cursor.getString(cursor.getColumnIndex(SORT_BY_ALBUMS)))){
                        FoldersArrayList<ItemFile> oneAlbum = mediaTreeAlbums.get(cursor.getString(cursor.getColumnIndex(SORT_BY_ALBUMS)));
                        oneAlbum.add(new ItemFile(  false,
                                                    FindMedia.getName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))),
                                                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                                                    (Long)cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                    } else {
                        FoldersArrayList<ItemFile> oneAlbum = new FoldersArrayList<ItemFile>();
                        oneAlbum.add(new ItemFile(  false,
                                                    FindMedia.getName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))),
                                                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                                                    (Long)cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                        mediaTreeAlbums.put( cursor.getString(cursor.getColumnIndex(SORT_BY_ALBUMS)), oneAlbum);
                    }

                    // ARTISTS
                    if(mediaTreeArtists.containsKey(cursor.getString(cursor.getColumnIndex(SORT_BY_ARTIST)))){
                        FoldersArrayList<ItemFile> oneAlbum = mediaTreeArtists.get(cursor.getString(cursor.getColumnIndex(SORT_BY_ARTIST)));
                        oneAlbum.add(new ItemFile(  false,
                                FindMedia.getName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))),
                                cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                                (Long)cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                    } else {
                        FoldersArrayList<ItemFile> oneAlbum = new FoldersArrayList<ItemFile>();
                        oneAlbum.add(new ItemFile(  false,
                                FindMedia.getName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))),
                                cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                                (Long)cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                        mediaTreeArtists.put( cursor.getString(cursor.getColumnIndex(SORT_BY_ARTIST)), oneAlbum);
                    }

                    // FOLDERS
                    Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    String mFolder = null;
                    // Check m.b. this is short name
                    if(FindMedia.isBadName(FindMedia.getFolder(uri.getPath())))
                        mFolder = FindMedia.getComplexName(uri.getPath());
                    else
                        mFolder = FindMedia.getFolder(uri.getPath());

                    if(mediaTreeFolders.containsKey(mFolder)){
                        FoldersArrayList<ItemFile> oneAlbum = mediaTreeFolders.get(mFolder);
                        oneAlbum.add(new ItemFile(  false,
                                FindMedia.getName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))),
                                cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                                (Long)cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                    } else {
                        FoldersArrayList<ItemFile> oneAlbum = new FoldersArrayList<ItemFile>();
                        oneAlbum.add(new ItemFile(  false,
                                FindMedia.getName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))),
                                cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                                (Long)cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                        mediaTreeFolders.put(mFolder, oneAlbum);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return true;
    }


    public LinkedHashMap<String, FoldersArrayList<ItemFile>> getMediaTreeAlbums() {
        return mediaTreeAlbums;
    }

    public LinkedHashMap<String, FoldersArrayList<ItemFile>> getMediaTreeArtists() {
        return mediaTreeArtists;
    }

    public LinkedHashMap<String, FoldersArrayList<ItemFile>> getMediaTreeFolders() {
        return mediaTreeFolders;
    }
}
