package ru.testsimpleapps.coloraudioplayer.ui.dialogs

import android.content.Context
import android.os.Bundle

import ru.testsimpleapps.coloraudioplayer.R

class AboutDialog(context: Context) : BaseDialog(context) {

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_about)
    }

}
