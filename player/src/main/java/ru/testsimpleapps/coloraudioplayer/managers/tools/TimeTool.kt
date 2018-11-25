package ru.testsimpleapps.coloraudioplayer.managers.tools

import java.text.SimpleDateFormat
import java.util.Date


object TimeTool {

    /*
    * Pattern
    * */
    private val DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss"
    private val DATE_PATTERN = "dd-MM-yyyy"
    private val TIME_PATTERN = "HH:mm:ss"

    /*
    * Object
    * */
    private val TIME_FORMAT = SimpleDateFormat(TIME_PATTERN)
    private val DATE_TIME_FORMAT = SimpleDateFormat(DATE_TIME_PATTERN)
    private val DATE_FORMAT = SimpleDateFormat(DATE_PATTERN)

    fun getDateTime(ms: Long): String {
        return DATE_TIME_FORMAT.format(Date(ms))
    }

    fun getDate(ms: Long): String {
        return DATE_FORMAT.format(Date(ms))
    }

    fun getTime(ms: Long): String {
        return TIME_FORMAT.format(Date(ms))
    }

    fun getDuration(ms: Long): String {
        return (if (ms / (1000 * 60 * 60) != 0L) String.format("%01d", ms / (1000 * 60 * 60)) + ":" else "") +
                String.format("%02d", ms / (1000 * 60) % 60) + ":" +
                String.format("%02d", ms / 1000 % 60)
    }
}
