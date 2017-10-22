package ru.testsimpleapps.coloraudioplayer.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ru.testsimpleapps.coloraudioplayer.PlayerApplication;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;

public class ColorsDialog
        extends AbstractDialog
        implements View.OnClickListener {

    private final Context context;
    private Button greenButton;
    private Button redButton;
    private Button blueButton;

    public ColorsDialog(Context context){
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colors_dialog);

        greenButton = (Button) findViewById(R.id.colors_green);
        redButton = (Button) findViewById(R.id.colors_red);
        blueButton = (Button) findViewById(R.id.colors_blue);
        greenButton.setOnClickListener(this);
        redButton.setOnClickListener(this);
        blueButton.setOnClickListener(this);
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

        //PlayerApplication.getPlayerApplication().savePreferences();
        dismiss();

        if(current != select){
            MainActivity.restartActivity();
        }
    }
}
