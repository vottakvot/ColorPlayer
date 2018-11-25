package ru.testsimpleapps.coloraudioplayer.managers.tools


object MathTool {

    fun roundToTenths(value: Long): Long {
        return (value + 5) / 10 * 10
    }

}
