package ru.testsimpleapps.coloraudioplayer.ui.dialogs

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TimePicker

import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.service.PlayerService

class TimerDialog(context: Context) : BaseDialog(context), View.OnClickListener {

    private var createButton: Button? = null
    private var resetButton: Button? = null
    private var cancelButton: Button? = null
    private var radioButtonWake: RadioButton? = null
    private var radioButtonPlay: RadioButton? = null
    private var radioButtonPause: RadioButton? = null
    private var timePicker: TimePicker? = null

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_timer)

        createButton = findViewById<View>(R.id.timer_create) as Button
        resetButton = findViewById<View>(R.id.timer_reset) as Button
        cancelButton = findViewById<View>(R.id.timer_cancel) as Button
        createButton!!.setOnClickListener(this)
        resetButton!!.setOnClickListener(this)
        cancelButton!!.setOnClickListener(this)
        radioButtonWake = findViewById<View>(R.id.timer_type_wake) as RadioButton
        radioButtonPlay = findViewById<View>(R.id.timer_type_play) as RadioButton
        radioButtonPause = findViewById<View>(R.id.timer_type_pause) as RadioButton
        timePicker = findViewById<View>(R.id.timer_edit) as TimePicker
        timePicker!!.setIs24HourView(true)

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timePicker!!.background = context.resources.getDrawable(R.drawable.drawable_common_time_picker)
        }

        resetPicker()
    }

    private fun resetPicker() {
        // Set timer to 0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker!!.minute = 0
            timePicker!!.hour = 0
        } else {
            timePicker!!.currentHour = 0
            timePicker!!.currentMinute = 0
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.timer_create -> {
                var typeTimer = PlayerService.TIMER_TYPE_NONE

                // Get timer type
                if (radioButtonWake!!.isChecked) {
                    typeTimer = PlayerService.TIMER_TYPE_WAKE
                } else if (radioButtonPlay!!.isChecked) {
                    typeTimer = PlayerService.TIMER_TYPE_PLAY
                } else if (radioButtonPause!!.isChecked) {
                    typeTimer = PlayerService.TIMER_TYPE_PAUSE
                }

                var minutes = -1
                // Get time in minutes
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    minutes = timePicker!!.hour * 60 + timePicker!!.minute
                } else {
                    minutes = timePicker!!.currentHour * 60 + timePicker!!.currentMinute
                }

                // Check time
                if (typeTimer != PlayerService.TIMER_TYPE_WAKE && minutes <= 0) {

                }

                context.startService(Intent(PlayerService.ACTION_TIMER_START)
                        .setPackage(context.packageName))

                dismiss()
            }
            R.id.timer_reset -> {
                resetPicker()
                //MainActivity.setTimerButton(App.getAppContext().initDefaultViewDataTimer());
                context.startService(Intent(PlayerService.ACTION_TIMER_RESET)
                        .setPackage(context.packageName))
            }

            R.id.timer_cancel -> dismiss()
        }
    }

}
