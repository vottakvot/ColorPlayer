package ru.testsimpleapps.coloraudioplayer.ui.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Map;
import java.util.TreeMap;

import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.control.player.playlist.cursor.CursorTool;
import ru.testsimpleapps.coloraudioplayer.ui.MainPages;
import ru.testsimpleapps.coloraudioplayer.ui.PlayerControl;

public class PlaylistChooseDialog
        extends AbstractDialog {

    private final Context context;
    private final View playlistView;
    private final ListView playlistListView;
    private final LayoutInflater inflater;
    private Map<Long, String> playlistMap;
    private final PlaylistDialogAdapter playlistDialogAdapter;

    public PlaylistChooseDialog(Context context){
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
    }

    @Override
    public void show() {
        super.show();
        updatePlaylistList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(playlistView);
    }

    public void updatePlaylistList(){
        playlistMap = CursorTool.getPlaylists(context.getContentResolver());
        if(playlistMap == null){
            playlistMap = new TreeMap<>();
            playlistMap.put(-1L, context.getResources().getString(R.string.playlist_get_empty));
        }
    }

    /*
    * Playlist adapter
    * */
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
//            return FindMedia.getMapMediaPosition(playlistMap, position);
            return null;
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

//            Long playlistID = FindMedia.getMapMediaPosition(playlistMap, position);
//            ((TextView) view.findViewById(R.id.playlistGet_item)).setText(playlistMap.get(playlistID));

            return view;
        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            if(MainPages.getPlaylistAdapter() != null && FindMedia.getMapMediaPosition(playlistMap, position) != -1){
//                //PlayerApplication.getPlayerApplication().setPlaylistId(FindMedia.getMapMediaPosition(playlistMap, position));
//                MainPages.getPlaylistAdapter().refreshPlaylist(CursorTool.SORT_NONE);
//                MainPages.getTextPlaylistHeader().setText(playlistMap.get(FindMedia.getMapMediaPosition(playlistMap, position)));
//                MainPages.PageFragment.changePlaylistBackground();
//                PlayerControl.setCountTracks();
//                dismiss();
//                //MainActivity.showInfoMessage(context.getResources().getString(R.string.playlist_get_chose) + " " + playlistMap.get(PlayerApplication.getPlayerApplication().getPlaylistId()));
//            }
        }

         @Override
         public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                if(FindMedia.getMapMediaPosition(playlistMap, position) != -1){
//                    CursorTool.deletePlaylist(context.getContentResolver(), FindMedia.getMapMediaPosition(playlistMap, position));
//                    playlistMap.remove(FindMedia.getMapMediaPosition(playlistMap, position));
//                    notifyDataSetChanged();
//                    return true;
//                }

                return false;
        }
    }
}
