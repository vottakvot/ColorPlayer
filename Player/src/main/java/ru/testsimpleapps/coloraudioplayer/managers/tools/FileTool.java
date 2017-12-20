package ru.testsimpleapps.coloraudioplayer.managers.tools;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.util.List;


public class FileTool {

    public static String getFileName(final String path) {
        if (path != null)
            return new File(path).getName();
        return null;
    }

    public static String getFolderName(final String path) {
        if (path != null)
            return new File(path).getParentFile().getName();
        return null;
    }

    public static String getComplexName(final String path) {
        if (path != null) {
            final List<String> pathSegment = Uri.parse(path).getPathSegments();
            if (pathSegment != null && pathSegment.size() > 1) {
                return pathSegment.get(pathSegment.size() - 2) + pathSegment.get(pathSegment.size() - 1);
            }
        }

        return null;
    }

    public static String getAppPath(final Context context) {
        return context.getFilesDir().getAbsolutePath() + "/";
    }

    public static String getCachePath(final Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

}
