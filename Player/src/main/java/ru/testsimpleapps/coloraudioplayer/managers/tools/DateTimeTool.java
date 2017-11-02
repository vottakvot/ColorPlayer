package ru.testsimpleapps.coloraudioplayer.managers.tools;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DateTimeTool {

    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "HH:mm:ss";

    public static String getDateTime(long ms) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
        return formatter.format(new Date(ms));
    }

    public static String getDate(long ms) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        return formatter.format(new Date(ms));
    }

    public static String getTime(long ms) {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
        return formatter.format(new Date(ms));
    }

    public static String getDuration(long ms) {
        return ((ms / (1000 * 60 * 60)) != 0 ? String.format("%01d", (ms / (1000 * 60 * 60))) + ":" : "") +
                String.format("%02d", (ms / (1000 * 60)) % 60) + ":" +
                String.format("%02d", (ms / 1000) % 60);
    }
}
