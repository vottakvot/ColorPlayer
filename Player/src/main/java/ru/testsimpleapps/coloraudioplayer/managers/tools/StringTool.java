package ru.testsimpleapps.coloraudioplayer.managers.tools;


public class StringTool {

    private static final String PATTERN_SHORT_NAME = "^[0-9]*|[a-zA-Z]{1,2}\\s*[0-9]*|[0-9]*\\s*[a-zA-Z]{1,2}$";
    private static final String PATTERN_NUMERIC = "^[0-9]*$";

    public static boolean isBadName(String number) {
        return (number != null && number.trim().matches(PATTERN_SHORT_NAME) ? true : false);
    }

    public static boolean isNumeric(String number) {
        return (number != null && number.trim().matches(PATTERN_NUMERIC) ? true : false);
    }

}
