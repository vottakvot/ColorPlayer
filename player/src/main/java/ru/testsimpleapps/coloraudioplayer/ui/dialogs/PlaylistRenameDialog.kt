package ru.testsimpleapps.coloraudioplayer.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

import ru.testsimpleapps.coloraudioplayer.R

class PlaylistRenameDialog(context: Context) : BaseDialog(context), View.OnClickListener {

    private var renameButton: Button? = null
    private var cancelButton: Button? = null
    private var editText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_playlist_rename)

        renameButton = findViewById<View>(R.id.playlistRename_create) as Button
        cancelButton = findViewById<View>(R.id.playlistRename_cancel) as Button
        editText = findViewById<View>(R.id.playlistRename_edit) as EditText
        renameButton!!.setOnClickListener(this)
        cancelButton!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.playlistRename_create -> {
                if (editText!!.text.toString().trim { it <= ' ' } != "") {
                    //CursorTool.renamePlaylist(instance.getContentResolver(), App.getAppContext().getPlaylistId(), editText.getText().toString());

                    dismiss()

                }

                dismiss()
            }
            R.id.playlistRename_cancel -> dismiss()
        }
    }
}
