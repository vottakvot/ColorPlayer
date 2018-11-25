package ru.testsimpleapps.coloraudioplayer.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ru.testsimpleapps.coloraudioplayer.R
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory
import ru.testsimpleapps.coloraudioplayer.managers.tools.CursorTool

class PlaylistCreateDialog(private val mContext: Context) : BaseDialog(mContext) {

    @BindView(R.id.playlist_create_edit)
    lateinit var mNameEdit: EditText
    @BindView(R.id.playlist_create_ok)
    lateinit var mCreateButton: Button
    @BindView(R.id.playlist_create_cancel)
    lateinit var mCancelButton: Button

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        init()
    }

    @OnClick(R.id.playlist_create_ok)
    protected fun onCreateClick() {
        val name = mNameEdit!!.text.toString().trim { it <= ' ' }
        if (name != "") {
            val playlistId = CursorTool.createPlaylist(mContext.contentResolver, name)
            if (playlistId != IPlaylist.ERROR_CODE) {
                PlayerConfig.instance.playlistId = playlistId
                CursorFactory.newInstance()
                dismiss()
                return
            }
        }

        mNameEdit!!.error = mContext.getString(R.string.playlist_create_wrong_name)
    }

    @OnClick(R.id.playlist_create_cancel)
    protected fun onCancelClick() {
        dismiss()
    }

    private fun init() {
        setContentView(R.layout.dialog_playlist_create)
        ButterKnife.bind(this)
    }
}
