package ru.testsimpleapps.coloraudioplayer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.TreeMap;

import ru.testsimpleapps.music_explorer.FindMedia;

public class PlaylistChooseDialog
        extends Dialog {

    private final Context context;
    private final View playlistView;
    private final ListView playlistListView;
    private final LayoutInflater inflater;
    private TreeMap<Long, String> playlistMap;
    private final PlaylistDialogAdapter playlistDialogAdapter;

    private boolean isSize = false;

    PlaylistChooseDialog(Context context){
        super(context);
        this.context = context;
        updatePlaylistList();

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        playlistDialogAdapter = new PlaylistDialogAdapter();

        playlistView = (View) inflater.inflate(R.layout.playlist_get_dialog, null);
        playlistListView = (ListView) playlistView.findViewById(R.id.playlistGet_list);

        playlistListView.setAdapter(playlistDialogAdapter);
        playlistListView.setOnItemClickListener(playlistDialogAdapter);
        playlistListView.setOnItemLongClickListener(playlistDialogAdapter);

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
    public void show() {
        super.show();
        updatePlaylistList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(playlistView);
    }

    public void updatePlaylistList(){
        playlistMap = PlaylistUtil.getPlaylists(context.getContentResolver());
        if(playlistMap == null){
            playlistMap = new TreeMap<Long, String>();
            playlistMap.put(-1L, context.getResources().getString(R.string.playlist_get_empty));
        }
    }

    private class PlaylistDialogAdapter
            extends BaseAdapter
            implements  ListView.OnItemClickListener,
                        ListView.OnItemLongClickListener {

    @Override
    public int getCount() {
        return playlistMap.size();
    }

    @Override
    public Object getItem(int position) {
        return FindMedia.getMapMediaPosition(playlistMap, position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.playlist_get_item, parent, false);
        }

        Long playlistID = FindMedia.getMapMediaPosition(playlistMap, position);
        ((TextView) view.findViewById(R.id.playlistGet_item)).setText(playlistMap.get(playlistID));

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(MainPages.getPlaylistAdapter() != null && FindMedia.getMapMediaPosition(playlistMap, position) != -1){
            PlayerApplication.getPlayerApplication().setPlaylistId(FindMedia.getMapMediaPosition(playlistMap, position));
            MainPages.getPlaylistAdapter().getPlaylist(PlaylistUtil.SORT_NONE);
            MainPages.getTextPlaylistHeader().setText(playlistMap.get(FindMedia.getMapMediaPosition(playlistMap, position)));
            MainPages.PageFragment.changePlaylistBackground();
            PlayerControl.setCountTracks();
            dismiss();
            MainActivity.showInfoMessage(context.getResources().getString(R.string.playlist_get_chose) + " " + playlistMap.get(PlayerApplication.getPlayerApplication().getPlaylistId()));
        }
    }

     @Override
     public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if(FindMedia.getMapMediaPosition(playlistMap, position) != -1){
                PlaylistUtil.deletePlaylist(context.getContentResolver(), FindMedia.getMapMediaPosition(playlistMap, position));
                playlistMap.remove(FindMedia.getMapMediaPosition(playlistMap, position));
                notifyDataSetChanged();
                return true;
            }

            return false;
        }
    }
}
