package ru.testsimpleapps.coloraudioplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import ru.testsimpleapps.music_explorer.FindMedia;

public class PlayerControl {

    private final Context context;
    private final LayoutInflater inflater;
    private final View view;
    private final String notes = "♫♪♭♩";

    // Controls
    private static ImageButton view_play_pause = null;
    private final ImageButton view_expand;
    private final ImageButton view_random;
    private final ImageButton view_previous;
    private final ImageButton view_next;
    private final ImageButton view_repeat;

    // Seeker
    private static SeekBar view_seek_bar = null;

    //Info
    private static TextView view_end_position = null;
    private static TextView view_start_position = null;
    private static TextView view_track_name;
    private static TextView view_track_number;

    private ActionControl actionControl;

    private View.OnLongClickListener setPositionOnLongPress = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v) {
            MainPages.setPlaylistPosition();
            return true;
        }
    };

    PlayerControl(Context context){
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.main_activity, null);

        view_expand = (ImageButton) ((Activity)context).findViewById(R.id.expand);
        view_random = (ImageButton) ((Activity)context).findViewById(R.id.random);
        view_previous = (ImageButton) ((Activity)context).findViewById(R.id.previous);
        view_previous.setOnLongClickListener(setPositionOnLongPress);
        view_play_pause = (ImageButton) ((Activity)context).findViewById(R.id.play_pause);
        view_play_pause.setOnLongClickListener(setPositionOnLongPress);
        view_next = (ImageButton) ((Activity)context).findViewById(R.id.next);
        view_next.setOnLongClickListener(setPositionOnLongPress);
        view_repeat = (ImageButton) ((Activity)context).findViewById(R.id.repeat);
        view_seek_bar = (SeekBar) ((Activity)context).findViewById(R.id.seekPlayPosition);

        view_track_name = (TextView)((Activity)context).findViewById(R.id.currentTrackName);
        view_track_name.setSelected(true);
        view_track_name.setOnLongClickListener(setPositionOnLongPress);

        view_track_number = (TextView)((Activity)context).findViewById(R.id.currentTrack);
        view_start_position = (TextView)((Activity)context).findViewById(R.id.currentPositionTrack);
        view_end_position = (TextView)((Activity)context).findViewById(R.id.totalTrackTime);

        actionControl = new ActionControl();
        setViewListeners(actionControl);

        // Init view state
        setState();
        setPlayPauseImage(PlayerApplication.getPlayerApplication().isPlay());
        setCountTracks();
        setViewsDuration(1);
        setViewsPosition(0);
        setTrackNameView("");
        setRunningString();

        if(PlayerApplication.getPlayerApplication().getTrackName() != null){
            setTrackNameView(PlayerApplication.getPlayerApplication().getTrackName());
        }
    }

    private void setRunningString(){
        float [] size = MainActivity.getActivitySize();
        float width = MainActivity.getTextWidth(notes);

        if(size != null && size.length == 2 && width > 0){
            int n = (int)(size[1] / width);
            String runningString  = notes;
            if(n > 0){
                for(int i = 0; i < n + 5; i++){
                    runningString += notes;
                }
            }
            setTrackNameView(runningString);
        }
    }

    private void setViewListeners(ActionControl actionControl){
        view_expand.setOnClickListener(actionControl);
        view_random.setOnClickListener(actionControl);
        view_previous.setOnClickListener(actionControl);
        view_play_pause.setOnClickListener(actionControl);
        view_next.setOnClickListener(actionControl);
        view_repeat.setOnClickListener(actionControl);
        view_seek_bar.setOnSeekBarChangeListener(actionControl);
    }

    private void setState(){
        if(!PlayerApplication.getPlayerApplication().isExpand()){
            PlayerApplication.getPlayerApplication().setIsExpand(true);
            view_expand.performClick();
        }

        if(PlayerApplication.getPlayerApplication().isRandom()){
            view_random.setImageResource(R.drawable.shuffle_active);
        }

        setRepeatImage(PlayerApplication.getPlayerApplication().getRepeat());
    }

    private void setRepeatImage(int repeat){
        switch (repeat){
            case PlayerApplication.REPEAT_NONE:
                view_repeat.setImageResource(R.drawable.repeat_inactive);
                break;
            case PlayerApplication.REPEAT_ONE:
                view_repeat.setImageResource(R.drawable.repeat_active_one);
                break;
            case PlayerApplication.REPEAT_ALL:
                view_repeat.setImageResource(R.drawable.repeat_active);
                break;
        }
    }

    public static void setPlayPauseImage(boolean playOrPause){
        if(view_play_pause != null){
            if (!playOrPause) {
                view_play_pause.setImageResource(R.drawable.play);
            } else {
                    view_play_pause.setImageResource(R.drawable.pause);
                }
        }

        PlayerApplication.getPlayerApplication().setIsPlay(playOrPause);
    }

    public static void setTrackNameView(String name) {
        if(view_track_name != null && name != null){
            view_track_name.setText(name);
            view_track_name.setSelected(true);
            view_track_name.requestFocus();
        }
    }

    public static void setCountTracks() {
        if(view_track_number != null){
            if(PlayerApplication.getPlayerApplication().getActivePlaylist() != null){
                synchronized(PlayerApplication.getPlayerApplication().getActivePlaylist()) {
                    view_track_number.setText((PlayerApplication.getPlayerApplication().getActivePlaylist().getPosition() + 1) + "/" +
                            PlayerApplication.getPlayerApplication().getActivePlaylist().getCount());
                }
            } else {
                    view_track_number.setText("0000/0000");
                }
        }
    }

    public static void setViewsDuration(int position) {
        if(view_seek_bar != null && view_end_position != null){
            view_seek_bar.setMax(position);
            view_end_position.setText(FindMedia.getDuration(position));
        }
    }

    public static void setViewsPosition(int position) {
        if(view_seek_bar != null && view_end_position != null){
            view_seek_bar.setProgress(position);
            view_start_position.setText(FindMedia.getDuration(position));
        }
    }

    public class ActionControl
            implements  ImageButton.OnClickListener,
                        SeekBar.OnSeekBarChangeListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.expand : {
                    Log.i(MainActivity.LOG_ACTIVITY, "onClick - " + v.getId());
                    if(PlayerApplication.getPlayerApplication().isExpand()){
                        PlayerApplication.getPlayerApplication().setIsExpand(false);
                        view_track_name.setVisibility(View.GONE);
                        view_start_position.setVisibility(View.GONE);
                        view_end_position.setVisibility(View.GONE);
                        view_track_number.setVisibility(View.GONE);
                        view_expand.setImageResource(R.drawable.expand_active);
                    } else {
                            PlayerApplication.getPlayerApplication().setIsExpand(true);
                            view_track_name.setVisibility(View.VISIBLE);
                            view_start_position.setVisibility(View.VISIBLE);
                            view_end_position.setVisibility(View.VISIBLE);
                            view_track_number.setVisibility(View.VISIBLE);
                            view_expand.setImageResource(R.drawable.expand_inactive);
                        }

                    break;
                }

                case R.id.random: {
                    if(!PlayerApplication.getPlayerApplication().isRandom()){
                        PlayerApplication.getPlayerApplication().setIsRandom(true);
                        view_random.setImageResource(R.drawable.shuffle_active);
                    } else {
                        PlayerApplication.getPlayerApplication().setIsRandom(false);
                        view_random.setImageResource(R.drawable.shuffle_inactive);
                    }
                    break;
                }

                case R.id.previous : {
                    context.startService(new Intent(PlayService.ACTION_PREVIOUS)
                                                .setPackage(context.getPackageName()));
                    break;
                }

                case R.id.play_pause : {
                    context.startService(new Intent(PlayService.ACTION_PLAY_PAUSE)
                            .putExtra(PlayService.ACTION_PLAY_PAUSE, PlayService.SUB_ACTION_PLAY_PAUSE)
                            .setPackage(context.getPackageName()));
                    break;
                }

                case R.id.next : {
                    context.startService(new Intent(PlayService.ACTION_NEXT)
                            .setPackage(context.getPackageName()));
                    break;
                }

                case R.id.repeat : {
                    setRepeatImage(PlayerApplication.getPlayerApplication().setRepeatAuto());
                    break;
                }
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(MainActivity.LOG_ACTIVITY, "onStopTrackingTouch");
            context.startService(new Intent(PlayService.ACTION_SEEK)
                                        .putExtra(PlayService.ACTION_SEEK, seekBar.getProgress())
                                            .setPackage(context.getPackageName()));
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
    }
}
