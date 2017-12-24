package ru.testsimpleapps.coloraudioplayer.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory;
import ru.testsimpleapps.coloraudioplayer.managers.tools.CursorTool;

public class PlaylistCreateDialog extends BaseDialog {

    private final Context mContext;

    @BindView(R.id.playlist_create_edit)
    protected EditText mNameEdit;
    @BindView(R.id.playlist_create_ok)
    protected Button mCreateButton;
    @BindView(R.id.playlist_create_cancel)
    protected Button mCancelButton;

    public PlaylistCreateDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @OnClick(R.id.playlist_create_ok)
    protected void onCreateClick() {
        final String name = mNameEdit.getText().toString().trim();
        if (!name.equals("")) {
            final long playlistId = CursorTool.createPlaylist(mContext.getContentResolver(), name);
            if (playlistId != IPlaylist.ERROR_CODE) {
                PlayerConfig.getInstance().setPlaylistId(playlistId);
                CursorFactory.newInstance();
                dismiss();
                return;
            }
        }

        mNameEdit.setError(mContext.getString(R.string.playlist_create_wrong_name));
    }

    @OnClick(R.id.playlist_create_cancel)
    protected void onCancelClick() {
        dismiss();
    }

    private void init() {
        setContentView(R.layout.dialog_playlist_create);
        ButterKnife.bind(this);
    }
}
