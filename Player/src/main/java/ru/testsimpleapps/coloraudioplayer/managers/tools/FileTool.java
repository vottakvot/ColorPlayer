package ru.testsimpleapps.coloraudioplayer.managers.tools;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.util.List;


public class FileTool {

    public static String getName(String path) {
        if (path != null)
            return new File(path).getName();
        return null;
    }

    public static String getNameSegment(String path) {
        return getSegment(path, true);
    }

    public static String getFolder(String path) {
        return getSegment(path, false);
    }

    private static String getSegment(final String path, final boolean isFile) {
        String name = null;
        if (path != null) {
            List<String> pathSegment = Uri.parse(path).getPathSegments();
            if (pathSegment != null && pathSegment.size() > 0) {
                if (isFile) {
                    name = pathSegment.get(pathSegment.size() - 1);
                } else {
                    name = pathSegment.get(pathSegment.size() - 2);
                }
            }
        }

        return name;
    }

    public static String getAppPath(Context context) {
        return context.getFilesDir().getAbsolutePath() + "/";
    }

}
