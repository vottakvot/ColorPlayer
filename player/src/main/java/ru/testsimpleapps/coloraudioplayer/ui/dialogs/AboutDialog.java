package ru.testsimpleapps.coloraudioplayer.ui.dialogs;

import android.content.Context;
import android.os.Bundle;

import ru.testsimpleapps.coloraudioplayer.R;

public class AboutDialog
        extends BaseDialog {

    private final Context context;

    public AboutDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_about);
    }

}
