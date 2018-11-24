package ru.testsimpleapps.coloraudioplayer.managers.tools;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeTool {

    /*
    * Pattern
    * */
    private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final String TIME_PATTERN = "HH:mm:ss";

    /*
    * Object
    * */
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(TIME_PATTERN);
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat(DATE_TIME_PATTERN);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

    public static String getDateTime(final long ms) {
        return DATE_TIME_FORMAT.format(new Date(ms));
    }

    public static String getDate(final long ms) {
        return DATE_FORMAT.format(new Date(ms));
    }

    public static String getTime(final long ms) {
        return TIME_FORMAT.format(new Date(ms));
    }

    public static String getDuration(final  long ms) {
        return ((ms / (1000 * 60 * 60)) != 0 ? String.format("%01d", (ms / (1000 * 60 * 60))) + ":" : "") +
                String.format("%02d", (ms / (1000 * 60)) % 60) + ":" +
                String.format("%02d", (ms / 1000) % 60);
    }
}
