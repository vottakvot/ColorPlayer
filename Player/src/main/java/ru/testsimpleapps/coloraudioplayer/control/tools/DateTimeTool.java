package ru.testsimpleapps.coloraudioplayer.control.tools;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DateTimeTool {

    public static String getDateTime(long ms){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(new Date(ms));
    }

    public static String getDate(long ms){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(new Date(ms));
    }

    public static String getTime(long ms){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(new Date(ms));
    }

    public static String getDuration(long ms){
        return ((ms / (1000 * 60 * 60)) != 0? String.format("%01d", (ms / (1000 * 60 * 60))) + ":" : "") +
                String.format("%02d", (ms / (1000 * 60)) % 60) + ":" +
                String.format("%02d", (ms / 1000) % 60);
    }
}
