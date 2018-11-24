package ru.testsimpleapps.coloraudioplayer.ui.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TimePicker;

import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.service.PlayerService;

public class TimerDialog
        extends BaseDialog
        implements View.OnClickListener {

    private final Context context;
    private Button createButton;
    private Button resetButton;
    private Button cancelButton;
    private RadioButton radioButtonWake;
    private RadioButton radioButtonPlay;
    private RadioButton radioButtonPause;
    private TimePicker timePicker;

    public TimerDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timer);

        createButton = (Button) findViewById(R.id.timer_create);
        resetButton = (Button) findViewById(R.id.timer_reset);
        cancelButton = (Button) findViewById(R.id.timer_cancel);
        createButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        radioButtonWake = (RadioButton) findViewById(R.id.timer_type_wake);
        radioButtonPlay = (RadioButton) findViewById(R.id.timer_type_play);
        radioButtonPause = (RadioButton) findViewById(R.id.timer_type_pause);
        timePicker = (TimePicker) findViewById(R.id.timer_edit);
        timePicker.setIs24HourView(true);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 &&
                android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timePicker.setBackground(context.getResources().getDrawable(R.drawable.drawable_common_time_picker));
        }

        resetPicker();
    }

    private void resetPicker() {
        // Set timer to 0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker.setMinute(0);
            timePicker.setHour(0);
        } else {
            timePicker.setCurrentHour(0);
            timePicker.setCurrentMinute(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timer_create:
                String typeTimer = PlayerService.TIMER_TYPE_NONE;

                // Get timer type
                if (radioButtonWake.isChecked()) {
                    typeTimer = PlayerService.TIMER_TYPE_WAKE;
                } else if (radioButtonPlay.isChecked()) {
                    typeTimer = PlayerService.TIMER_TYPE_PLAY;
                } else if (radioButtonPause.isChecked()) {
                    typeTimer = PlayerService.TIMER_TYPE_PAUSE;
                }

                int minutes = -1;
                // Get time in minutes
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    minutes = timePicker.getHour() * 60 + timePicker.getMinute();
                } else {
                    minutes = timePicker.getCurrentHour() * 60 + timePicker.getCurrentMinute();
                }

                // Check time
                if (!typeTimer.equals(PlayerService.TIMER_TYPE_WAKE) && minutes <= 0) {

                    break;
                }

                context.startService(new Intent(PlayerService.ACTION_TIMER_START)
                        .setPackage(context.getPackageName()));

                dismiss();

                break;
            case R.id.timer_reset:
                resetPicker();
                //MainActivity.setTimerButton(App.getAppContext().initDefaultViewDataTimer());
                context.startService(new Intent(PlayerService.ACTION_TIMER_RESET)
                        .setPackage(context.getPackageName()));
                break;

            case R.id.timer_cancel:
                dismiss();
                break;
        }
    }

}
