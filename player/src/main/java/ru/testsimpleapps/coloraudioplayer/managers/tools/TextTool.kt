package ru.testsimpleapps.coloraudioplayer.managers.tools


import android.graphics.Paint
import android.widget.TextView

object TextTool {

    private val PATTERN_SHORT_NAME = "^[0-9]*|[a-zA-Z]{1,2}\\s*[0-9]*|[0-9]*\\s*[a-zA-Z]{1,2}$"
    private val PATTERN_NUMERIC = "^[0-9]*$"

    fun isBadName(number: String): Boolean {
        return if (number != null && number.trim { it <= ' ' }.matches(PATTERN_SHORT_NAME.toRegex())) true else false
    }

    fun isNumeric(number: String): Boolean {
        return if (number != null && number.trim { it <= ' ' }.matches(PATTERN_NUMERIC.toRegex())) true else false
    }

    fun measureTextWidth(text: String, textView: TextView?): Int {
        var paint = Paint()
        if (textView != null) {
            paint = Paint(textView.paint)
        }

        return paint.measureText(text).toInt()
    }

}
