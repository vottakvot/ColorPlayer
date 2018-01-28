package ru.testsimpleapps.coloraudioplayer.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory;
import ru.testsimpleapps.coloraudioplayer.managers.tools.CursorTool;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.BaseAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.PlaylistChooserAdapter;

public class PlaylistChooserDialog extends BaseDialog implements BaseAdapter.OnItemClickListener,
        BaseAdapter.OnItemLongClickListener {

    private final Context mContext;

    @BindView(R.id.playlist_chooser_list)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.playlist_chooser_cancel)
    protected Button mCancelButton;

    private PlaylistChooserAdapter mPlaylistChooserAdapter;

    public PlaylistChooserDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void show() {
        super.show();
        if (mPlaylistChooserAdapter != null) {
            mPlaylistChooserAdapter.setData(CursorTool.getPlaylist(mContext.getContentResolver()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @OnClick(R.id.playlist_chooser_cancel)
    protected void onCancelClick() {
        dismiss();
    }

    private void init() {
        setContentView(R.layout.dialog_playlist_chooser);
        ButterKnife.bind(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mPlaylistChooserAdapter = new PlaylistChooserAdapter(mContext);
        mPlaylistChooserAdapter.setOnItemClickListener(this);
        mPlaylistChooserAdapter.setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mPlaylistChooserAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        final Map.Entry<Long, String> item = mPlaylistChooserAdapter.getItem(position);
        if (item != null && item.getKey() != IPlaylist.ERROR_CODE) {
            PlayerConfig.getInstance().setPlaylistId(item.getKey());
            CursorFactory.newInstance();
            dismiss();
            return;
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        final Map.Entry<Long, String> item = mPlaylistChooserAdapter.getItem(position);
        if (item != null) {
            CursorTool.deletePlaylist(mContext.getContentResolver(), item.getKey());
            mPlaylistChooserAdapter.setData(CursorTool.getPlaylist(mContext.getContentResolver()));
        }
    }
}
