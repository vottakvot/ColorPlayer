package ru.testsimpleapps.coloraudioplayer.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;

public class PlaylistRenameDialog
        extends BaseDialog
        implements View.OnClickListener {

    private final Context context;
    private Button renameButton;
    private Button cancelButton;
    private EditText editText;

    public PlaylistRenameDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_rename_dialog);

        renameButton = (Button) findViewById(R.id.playlistRename_create);
        cancelButton = (Button) findViewById(R.id.playlistRename_cancel);
        editText = (EditText) findViewById(R.id.playlistRename_edit);
        renameButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playlistRename_create:
                if (!editText.getText().toString().trim().equals("")) {
                    //CursorTool.renamePlaylist(context.getContentResolver(), App.getAppContext().getPlaylistId(), editText.getText().toString());

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
