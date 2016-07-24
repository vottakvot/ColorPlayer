package ru.playme.color_player;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class ColorsDialog
        extends Dialog
        implements View.OnClickListener {

    private final Context context;

    private Button greenButton;
    private Button redButton;
    private Button blueButton;

    private boolean isSize = false;

    ColorsDialog(Context context){
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.colors_dialog);

        greenButton = (Button) findViewById(R.id.colors_green);
        redButton = (Button) findViewById(R.id.colors_red);
        blueButton = (Button) findViewById(R.id.colors_blue);

        greenButton.setOnClickListener(this);
        redButton.setOnClickListener(this);
        blueButton.setOnClickListener(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            dismiss();
            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        int current = PlayerApplication.getPlayerApplication().getNumberTheme();
        int select = 0;
        switch (v.getId()) {
            case R.id.colors_blue:
                select = PlayerApplication.getPlayerApplication().setNumberTheme(PlayerApplication.THEME_BLUE);
                break;
            case R.id.colors_green:
                select = PlayerApplication.getPlayerApplication().setNumberTheme(PlayerApplication.THEME_GREEN);
                break;
            case R.id.colors_red:
                select = PlayerApplication.getPlayerApplication().setNumberTheme(PlayerApplication.THEME_RED);
                break;
        }

        PlayerApplication.getPlayerApplication().savePreferences();
        dismiss();

        if(current != select){
            MainActivity.restartActivity();
        }
    }
}
