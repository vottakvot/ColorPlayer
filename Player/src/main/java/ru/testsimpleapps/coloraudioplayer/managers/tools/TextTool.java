package ru.testsimpleapps.coloraudioplayer.managers.tools;


import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class TextTool {

    private static final String PATTERN_SHORT_NAME = "^[0-9]*|[a-zA-Z]{1,2}\\s*[0-9]*|[0-9]*\\s*[a-zA-Z]{1,2}$";
    private static final String PATTERN_NUMERIC = "^[0-9]*$";

    public static boolean isBadName(@NonNull final String number) {
        return (number != null && number.trim().matches(PATTERN_SHORT_NAME) ? true : false);
    }

    public static boolean isNumeric(@NonNull final String number) {
        return (number != null && number.trim().matches(PATTERN_NUMERIC) ? true : false);
    }

    public static int measureTextWidth(@NonNull final String text, @Nullable final TextView textView) {
        Paint paint = new Paint();
        if (textView != null) {
            paint = new  Paint(textView.getPaint());
        }

        return (int)paint.measureText(text);
    }

}
