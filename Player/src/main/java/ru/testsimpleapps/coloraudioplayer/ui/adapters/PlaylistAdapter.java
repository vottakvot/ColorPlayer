package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.PlayerService;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.TimeTool;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;
import ru.testsimpleapps.coloraudioplayer.ui.views.VisualizerView;

public class PlaylistAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;

    private static VisualizerView playerVisualizer;
    private IPlaylist mPlaylist;
    private String currentSortBy = CursorTool.SORT_NONE;

    private final View viewHeader;
    private int searchPosition = -1;

    public PlaylistAdapter(Context context) {
        super();
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        refreshPlaylist();
        viewHeader = inflater.inflate(R.layout.playlist_header, null);
    }

    public static boolean updateVisualizer(byte[] bytes, boolean isPlaying) {
        if (playerVisualizer != null) {
            playerVisualizer.updateVisualizer(bytes, isPlaying);
            return true;
        }

        return false;
    }

    public void sortByName() {
        currentSortBy = CursorTool.SORT_NAME + CursorTool.SORT_ASC;
        refreshPlaylist();
    }

    public void sortByDuration() {
        currentSortBy = CursorTool.SORT_DURATION + CursorTool.SORT_ASC;
        refreshPlaylist();
    }

    public void sortByModify() {
        currentSortBy = CursorTool.SORT_MODIFY + CursorTool.SORT_ASC;
        refreshPlaylist();
    }

    public void refreshPlaylist() {
        mPlaylist = CursorFactory.getCursorPlaylistForView();
        notifyDataSetChanged();
    }

    public int getSearchPosition() {
        return searchPosition;
    }

    public void deleteTrack(int position) {
//        if(App.getContext().getPlaylistId() != -1) {
//            mPlaylist.goTo(position);
//            Long trackId = mPlaylist.getTrackId();
//            CursorTool.deleteTrackFromPlaylist(context.getContentResolver(), App.getContext().getPlaylistId(), trackId);
//            refreshPlaylist(currentSortBy);
//        }
    }

    public void setSearchPosition(int searchPosition) {
        this.searchPosition = searchPosition;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        long trackId = IPlaylist.NOT_INIT;
        if (mPlaylist != null) {
            mPlaylist.goTo(position);
            trackId = mPlaylist.getTrackId();
        }

        return trackId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_item_track, parent, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final AdapterViewHolder viewHolder = (AdapterViewHolder) holder;

        mPlaylist.goTo(position);
        viewHolder.imageTrack.setBackgroundResource(R.drawable.item_track);
        viewHolder.imageTrack.bringToFront();
        viewHolder.numberTrack.setText(Integer.toString(position + 1));
        viewHolder.numberTrack.bringToFront();
        viewHolder.nameTrack.setText(mPlaylist.getTrackName());
        viewHolder.nameTrack.bringToFront();
        viewHolder.durationTrack.setText(TimeTool.getDuration(mPlaylist.getTrackDuration()));
        viewHolder.durationTrack.bringToFront();

        if (viewHolder.artistTrack != null) {
            viewHolder.artistTrack.setText(mPlaylist.getTrackArtist());
            viewHolder.artistTrack.bringToFront();
        }

        if (viewHolder.albumTrack != null) {
            viewHolder.albumTrack.setText(mPlaylist.getTrackAlbum());
            viewHolder.albumTrack.bringToFront();
        }

        if (viewHolder.titleTrack != null) {
            viewHolder.titleTrack.setText(mPlaylist.getTrackTitle());
            viewHolder.titleTrack.bringToFront();
        }

        if (viewHolder.dateTrack != null) {
            viewHolder.dateTrack.setText(TimeTool.getDateTime(mPlaylist.getTrackDateModified()));
            viewHolder.dateTrack.bringToFront();
        }

        // For select playing position. If cursor doesn't init, select first position.
//        long sonID = App.getContext().getSongId();
//        if(sonID == -1){
//            IPlaylist mainActivePlaylist = App.getContext().getPlayerConfig().refreshPlaylist();
//            if(mainActivePlaylist.position() == -1){
//                mainActivePlaylist.toFirst();
//                App.getContext().setSongId(mainActivePlaylist.getTrackId());
//            }
//            sonID = mainActivePlaylist.getTrackId();
//        }

//        if(sonID == mPlaylist.getTrackId()){
//            view.setBackgroundResource(R.drawable.drawable_listview_item_selection);
//            ((GradientDrawable)view.getBackground()).setColor(App.getSelectionItemColor());
//            viewHolder.visualizerTrack.setVisibility(View.VISIBLE);
//            playerVisualizer = viewHolder.visualizerTrack;
//        } else {
//                view.setBackgroundColor(Color.TRANSPARENT);
//                viewHolder.visualizerTrack.setVisibility(View.INVISIBLE);
//            }
//
//        // For custom search selection
//        if( searchPosition == position && searchPosition != -1 &&
//            searchPosition != App.getContext().getPlayerConfig().refreshPlaylist().position()){
//            view.setBackgroundResource(R.drawable.drawable_listview_item_find);
//        } else if(sonID != mPlaylist.getTrackId()){
//                view.setBackgroundColor(Color.TRANSPARENT);
//            }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private class AdapterViewHolder extends RecyclerView.ViewHolder {
        public VisualizerView visualizerTrack;
        public ImageView imageTrack;
        public TextView numberTrack;
        public TextView nameTrack;
        public TextView artistTrack;
        public TextView albumTrack;
        public TextView dateTrack;
        public TextView titleTrack;
        public TextView durationTrack;


        public AdapterViewHolder(final View view) {
            super(view);
            visualizerTrack = (VisualizerView) view.findViewById(R.id.visualizerTrack);
            imageTrack = (ImageView) view.findViewById(R.id.imageTrack);
            numberTrack = (TextView) view.findViewById(R.id.numberTrack);
            nameTrack = (TextView) view.findViewById(R.id.nameTrack);
            artistTrack = (TextView) view.findViewById(R.id.artistTrack);
            durationTrack = (TextView) view.findViewById(R.id.durationTrack);
            albumTrack = (TextView) view.findViewById(R.id.albumTrack);
            titleTrack = (TextView) view.findViewById(R.id.titleTrack);
            dateTrack = (TextView) view.findViewById(R.id.dateTrack);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, getLayoutPosition());
                        onItemClick(view, getLayoutPosition());
                    }
                }
            });
        }
    }

    public void onItemClick(final View view, final int position) {
        IPlaylist mainActivePlaylist = App.getContext().getPlayerConfig().getPlaylist();
        mainActivePlaylist.goTo(position - 1);
        App.getContext().getPlayerConfig().setTrackPathFromPlaylist();
        context.startService(new Intent(PlayerService.ACTION_PLAY)
                .putExtra(PlayerService.KEY_PLAY_NEW, PlayerService.KEY_PLAY_NEW)
                .setPackage(context.getPackageName()));
        // Save current track id for select item view
        //App.getContext().setSongId(mainActivePlaylist.getTrackId());
        view.setBackgroundColor(MainActivity.getColor(context, R.color.common_select_list_item));
        notifyDataSetChanged();
    }

    public View getPlaylistHeader() {
        return viewHeader;
    }
}
