package ru.testsimpleapps.coloraudioplayer.control.tools;

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
        if (path != null) {
            List<String> pathSegment = Uri.parse(path).getPathSegments();
            return (pathSegment.size() > 0) ? pathSegment.get(pathSegment.size() - 1) : null;
        }

        return null;
    }

    public static String getAppPath(Context context) {
        return context.getFilesDir().getAbsolutePath() + "/";
    }

}
