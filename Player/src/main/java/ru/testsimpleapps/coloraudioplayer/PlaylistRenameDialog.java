package ru.testsimpleapps.coloraudioplayer;

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

public class PlaylistRenameDialog
        extends Dialog
        implements View.OnClickListener {

    private final Context context;

    private Button renameButton;
    private Button cancelButton;
    private EditText editText;

    private boolean isSize = false;

    PlaylistRenameDialog(Context context){
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.playlist_rename_dialog);

        renameButton = (Button) findViewById(R.id.playlistRename_create);
        cancelButton = (Button) findViewById(R.id.playlistRename_cancel);
        editText = (EditText) findViewById(R.id.playlistRename_edit);

        renameButton.setOnClickListener(this);
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

//    @Override
//    public void onWindowFocusChanged (boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if(!isSize && hasFocus) {
//            getWindow().setLayout(getWindow().getDecorView().getWidth() - 50, getWindow().getDecorView().getHeight());
//            isSize = true;
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playlistRename_create:
                if( !editText.getText().toString().trim().equals("")){
                    PlaylistUtil.renamePlaylist(context.getContentResolver(), PlayerApplication.getPlayerApplication().getPlaylistId(), editText.getText().toString());
                    if(MainPages.getTextPlaylistHeader() != null){
                        MainPages.getTextPlaylistHeader().setText(PlaylistUtil.getPlaylistNameById(context.getContentResolver(), PlayerApplication.getPlayerApplication().getPlaylistId()));
                    }
                    dismiss();
                    MainActivity.showInfoMessage(context.getResources().getString(R.string.playlist_rename_done));
                } else
                    MainActivity.showInfoMessage(context.getResources().getString(R.string.playlist_rename_wrong_name));
                break;
            case R.id.playlistRename_cancel:
                dismiss();
                break;
        }
    }
}
