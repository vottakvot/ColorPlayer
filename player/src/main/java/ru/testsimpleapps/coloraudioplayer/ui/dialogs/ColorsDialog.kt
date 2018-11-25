package ru.testsimpleapps.coloraudioplayer.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button

import ru.testsimpleapps.coloraudioplayer.R

class ColorsDialog(context: Context) : BaseDialog(context), View.OnClickListener {

    private var greenButton: Button? = null
    private var redButton: Button? = null
    private var blueButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_colors)

        greenButton = findViewById<View>(R.id.colors_green) as Button
        redButton = findViewById<View>(R.id.colors_red) as Button
        blueButton = findViewById<View>(R.id.colors_blue) as Button
        greenButton!!.setOnClickListener(this)
        redButton!!.setOnClickListener(this)
        blueButton!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {

    }
}
