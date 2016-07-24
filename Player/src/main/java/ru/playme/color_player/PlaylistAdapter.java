package ru.playme.color_player;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ru.playme.music_explorer.FindMedia;

public class PlaylistAdapter
        extends BaseAdapter
        implements  ListView.OnItemClickListener {

    private final Context context;
    private final LayoutInflater inflater;

    private static PlayerVisualizer playerVisualizer = null;
    private Cursor activeViewPlaylist = null;
    private String currentSortBy = PlaylistUtil.SORT_NONE;

    private final View viewHeader;
    private int searchPosition = -1;

    PlaylistAdapter(Context context) {
        super();
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        getPlaylist(currentSortBy);
        viewHeader = inflater.inflate(R.layout.playlist_header, null);
    }

    public static boolean updateVisualizer(byte[] bytes, boolean isPlaying){
        if(playerVisualizer != null){
            playerVisualizer.updateVisualizer(bytes, isPlaying);
            return true;
        }

        return false;
    }

    public int searchInName(int position, String forSearch){
        if(activeViewPlaylist != null && activeViewPlaylist.getCount() > 0 && 0 <= position && position < activeViewPlaylist.getCount()){
            activeViewPlaylist.moveToPosition(position);
            do {
                if(FindMedia.getName(activeViewPlaylist.getString(activeViewPlaylist.getColumnIndex(MediaStore.Audio.Media.DATA))).matches("(?i).*(" + forSearch + ").*"))
                    return activeViewPlaylist.getPosition();
            } while(activeViewPlaylist.moveToNext());
        }

        return -1;
    }

    public void sortByName(){
        currentSortBy = PlaylistUtil.SORT_NAME + PlaylistUtil.SORT_ASC;
        getPlaylist(currentSortBy);
    }

    public void sortByDuration(){
        currentSortBy = PlaylistUtil.SORT_DURATION + PlaylistUtil.SORT_ASC;
        getPlaylist(currentSortBy);
    }

    public void getPlaylist(final String sortBy){
        activeViewPlaylist = PlayerApplication.getPlayerApplication().setActivePlaylist(sortBy, true);
        notifyDataSetChanged();
    }

    public int getSearchPosition() {
        return searchPosition;
    }

    public void deleteTrack(int position){
        if(PlayerApplication.getPlayerApplication().getPlaylistId() != -1) {
            activeViewPlaylist.moveToPosition(position);
            Long trackId = activeViewPlaylist.getLong(activeViewPlaylist.getColumnIndex(MediaStore.Audio.Media._ID));
            PlaylistUtil.deleteTrackFromPlaylist(context.getContentResolver(), PlayerApplication.getPlayerApplication().getPlaylistId(), trackId);
            getPlaylist(currentSortBy);
        }
    }

    public void setSearchPosition(int searchPosition) {
        this.searchPosition = searchPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (activeViewPlaylist != null)? activeViewPlaylist.getCount() : 0;
    }

    @Override
    public Object getItem(int position) {
        activeViewPlaylist.moveToPosition(position);
        return activeViewPlaylist.getLong(activeViewPlaylist.getColumnIndex(MediaStore.Audio.Media._ID));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        public PlayerVisualizer visualizerTrack;
        public ImageView imageTrack;
        public TextView numberTrack;
        public TextView nameTrack;
        public TextView artistTrack;
        public TextView durationTrack;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;

        // Fill viewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.playlist_item_track, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.visualizerTrack = (PlayerVisualizer) view.findViewById(R.id.visualizerTrack);
            viewHolder.imageTrack = (ImageView) view.findViewById(R.id.imageTrack);
            viewHolder.numberTrack = (TextView) view.findViewById(R.id.numberTrack);
            viewHolder.nameTrack = (TextView) view.findViewById(R.id.nameTrack);
            viewHolder.artistTrack = (TextView) view.findViewById(R.id.artistTrack);
            viewHolder.durationTrack = (TextView) view.findViewById(R.id.durationTrack);
            view.setTag(viewHolder);
        } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

        try {
                activeViewPlaylist.moveToPosition(position);

                viewHolder.imageTrack.setBackgroundResource(R.drawable.item_track);
                viewHolder.numberTrack.setText(Integer.toString(position + 1));
                viewHolder.nameTrack.setText(FindMedia.getName(activeViewPlaylist.getString(activeViewPlaylist.getColumnIndex(MediaStore.Audio.Media.DATA))));
                viewHolder.artistTrack.setText(activeViewPlaylist.getString(activeViewPlaylist.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                viewHolder.durationTrack.setText(FindMedia.getDuration(activeViewPlaylist.getLong(activeViewPlaylist.getColumnIndex(MediaStore.Audio.Media.DURATION))));

                viewHolder.imageTrack.bringToFront();
                viewHolder.numberTrack.bringToFront();
                viewHolder.nameTrack.bringToFront();
                viewHolder.artistTrack.bringToFront();
                viewHolder.durationTrack.bringToFront();

                // For select playing position. If cursor doesn't init, select first position.
                long sonID = PlayerApplication.getPlayerApplication().getSongId();
                if(sonID == -1){
                    Cursor mainActivePlaylist = PlayerApplication.getPlayerApplication().getActivePlaylist();
                    if(mainActivePlaylist.getPosition() == -1){
                        mainActivePlaylist.moveToFirst();
                        PlayerApplication.getPlayerApplication().setSongId(mainActivePlaylist.getLong(mainActivePlaylist.getColumnIndex(MediaStore.Audio.Media._ID)));
                    }
                    sonID = mainActivePlaylist.getLong(mainActivePlaylist.getColumnIndex(MediaStore.Audio.Media._ID));
                }

                if(sonID == activeViewPlaylist.getLong(activeViewPlaylist.getColumnIndex(MediaStore.Audio.Media._ID))){
                    view.setBackgroundColor(MainActivity.getColor(context, R.color.common_select_list_item));
                    viewHolder.visualizerTrack.setVisibility(View.VISIBLE);
                    playerVisualizer = viewHolder.visualizerTrack;
                } else {
                        view.setBackgroundColor(Color.WHITE);
                        viewHolder.visualizerTrack.setVisibility(View.INVISIBLE);
                    }

                // For custom search selection
                if(searchPosition == position && searchPosition != -1 &&
                        searchPosition != PlayerApplication.getPlayerApplication().getActivePlaylist().getPosition()){
                    view.setBackgroundColor(MainActivity.getColor(context, R.color.common_find_list_item));
                } else if(sonID != activeViewPlaylist.getLong(activeViewPlaylist.getColumnIndex(MediaStore.Audio.Media._ID))){
                        view.setBackgroundColor(MainActivity.getColor(context, R.color.common_unselected_item));
                    }

        } catch (RuntimeException e){
                e.printStackTrace();
                new BugReportDialog(context, e.getMessage()).show();
            }

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PlayerApplication.getPlayerApplication().getActivePlaylist().moveToPosition(position - 1);
        context.startService(new Intent(PlayService.ACTION_PLAY_PAUSE)
                                            .putExtra(PlayService.ACTION_PLAY_PAUSE, PlayService.SUB_ACTION_NEW_PLAY)
                                                .setPackage(context.getPackageName()));

        // Save current track id for select item view
        PlayerApplication.getPlayerApplication().setSongId(
                PlayerApplication.getPlayerApplication().getActivePlaylist().getLong(
                        PlayerApplication.getPlayerApplication().getActivePlaylist().getColumnIndex(MediaStore.Audio.Media._ID)));

        view.setBackgroundColor(MainActivity.getColor(context, R.color.common_select_list_item));
        notifyDataSetChanged();
    }

    public View getPlaylistHeader() {
        return viewHeader;
    }
}
