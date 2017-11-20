package ru.testsimpleapps.coloraudioplayer.managers.tools;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeTool {

    /*
    * Pattern
    * */
    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String TIME_FORMAT = "HH:mm:ss";

    /*
    * Object
    * */
    private static SimpleDateFormat mSimpleTimeFormat = new SimpleDateFormat(TIME_FORMAT);
    private static SimpleDateFormat mSimpleDateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
    private static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

    public static String getDateTime(long ms) {
        return mSimpleDateTimeFormat.format(new Date(ms));
    }

    public static String getDate(long ms) {
        return mSimpleDateFormat.format(new Date(ms));
    }

    public static String getTime(long ms) {
        return mSimpleTimeFormat.format(new Date(ms));
    }

    public static String getDuration(long ms) {
        return ((ms / (1000 * 60 * 60)) != 0 ? String.format("%01d", (ms / (1000 * 60 * 60))) + ":" : "") +
                String.format("%02d", (ms / (1000 * 60)) % 60) + ":" +
                String.format("%02d", (ms / 1000) % 60);
    }
}
