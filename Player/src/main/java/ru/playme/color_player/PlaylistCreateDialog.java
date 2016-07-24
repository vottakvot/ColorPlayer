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
import android.widget.EditText;

public class PlaylistCreateDialog
        extends Dialog
        implements View.OnClickListener {

    private final Context context;

    private Button createButton;
    private Button cancelButton;
    private EditText editText;

    private boolean isSize = false;

    PlaylistCreateDialog(Context context){
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.playlist_create_dialog);

        createButton = (Button) findViewById(R.id.playlistCreate_create);
        cancelButton = (Button) findViewById(R.id.playlistCreate_cancel);
        editText = (EditText) findViewById(R.id.playlistCreate_edit);

        createButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

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
        switch (v.getId()) {
            case R.id.playlistCreate_create:
                if( !editText.getText().toString().trim().equals("")){
                    PlayerApplication.getPlayerApplication().setPlaylistId(PlaylistUtil.createPlaylist(context.getContentResolver(), editText.getText().toString().trim()));
                    MainPages.getPlaylistAdapter().getPlaylist(PlaylistUtil.SORT_NONE);
                    MainPages.getTextPlaylistHeader().setText(editText.getText().toString().trim());
                    dismiss();
                    MainActivity.showInfoMessage(context.getResources().getString(R.string.playlist_create_done));
                    PlayerControl.setCountTracks();
                } else
                    MainActivity.showInfoMessage(context.getResources().getString(R.string.playlist_create_wrong_name));
                break;
            case R.id.playlistCreate_cancel:
                dismiss();
                break;
        }
    }
}
