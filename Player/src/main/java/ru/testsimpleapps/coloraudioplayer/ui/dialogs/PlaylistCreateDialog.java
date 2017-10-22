package ru.testsimpleapps.coloraudioplayer.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.control.player.playlist.cursor.CursorTool;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;
import ru.testsimpleapps.coloraudioplayer.ui.MainPages;
import ru.testsimpleapps.coloraudioplayer.ui.PlayerControl;

public class PlaylistCreateDialog
        extends AbstractDialog
        implements View.OnClickListener {

    private final Context context;
    private Button createButton;
    private Button cancelButton;
    private EditText editText;

    public PlaylistCreateDialog(Context context){
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_create_dialog);

        createButton = (Button) findViewById(R.id.playlistCreate_create);
        cancelButton = (Button) findViewById(R.id.playlistCreate_cancel);
        editText = (EditText) findViewById(R.id.playlistCreate_edit);
        createButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playlistCreate_create:
                if( !editText.getText().toString().trim().equals("")){
                    //PlayerApplication.getPlayerApplication().setPlaylistId(CursorTool.createPlaylist(context.getContentResolver(), editText.getText().toString().trim()));
//                    MainPages.getPlaylistAdapter().refreshPlaylist(CursorTool.SORT_NONE);
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
