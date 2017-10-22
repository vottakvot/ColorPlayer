package ru.testsimpleapps.coloraudioplayer.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.PlayerService;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.control.explorer.FindMedia;
import ru.testsimpleapps.coloraudioplayer.control.player.AudioPlayer;
import ru.testsimpleapps.coloraudioplayer.control.player.playlist.IPlaylist;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;

public class PlayerControl {

    private final Context context;
    private final LayoutInflater inflater;
    private final View view;
    private final String notes = "♫♪♭♩";

    public ImageButton getView_play_pause() {
        return view_play_pause;
    }

    /*
        * Controls
        * */
    private ImageButton view_play_pause;
    private  ImageButton view_expand;
    private  ImageButton view_random;
    private  ImageButton view_previous;
    private  ImageButton view_next;
    private  ImageButton view_repeat;

    /*
    * Seeker
    * */
    private static SeekBar view_seek_bar;

    /*
    * Info
    * */
    private static TextView view_end_position;
    private static TextView view_start_position;
    private static TextView view_track_name;
    private static TextView view_track_number;

    private ActionControl actionControl;

    private View.OnLongClickListener setPositionOnLongPress = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            MainPages.setPlaylistPosition();
            return true;
        }
    };

    public PlayerControl(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.activity_main, null);

//        view_expand = (ImageButton) ((Activity) context).findViewById(R.id.expand);
//        view_random = (ImageButton) ((Activity) context).findViewById(R.id.random);
//        view_previous = (ImageButton) ((Activity) context).findViewById(R.id.previous);
//        view_previous.setOnLongClickListener(setPositionOnLongPress);
//        view_play_pause = (ImageButton) ((Activity) context).findViewById(R.id.play_pause);
//        view_play_pause.setOnLongClickListener(setPositionOnLongPress);
//        view_next = (ImageButton) ((Activity) context).findViewById(R.id.next);
//        view_next.setOnLongClickListener(setPositionOnLongPress);
//        view_repeat = (ImageButton) ((Activity) context).findViewById(R.id.repeat);
//        view_seek_bar = (SeekBar) ((Activity) context).findViewById(R.id.seekPlayPosition);
//
//        view_track_name = (TextView) ((Activity) context).findViewById(R.id.currentTrackName);
//        view_track_name.setSelected(true);
//        view_track_name.setOnLongClickListener(setPositionOnLongPress);
//
//        view_track_number = (TextView) ((Activity) context).findViewById(R.id.currentTrack);
//        view_start_position = (TextView) ((Activity) context).findViewById(R.id.currentPositionTrack);
//        view_end_position = (TextView) ((Activity) context).findViewById(R.id.totalTrackTime);

        actionControl = new ActionControl();
        setViewListeners(actionControl);

        // Init view state
        setState();
        //setPlayPauseImage(App.getAppContext().isPlay());
        setCountTracks();
        setViewsDuration(AudioPlayer.MAX_SEEK_POSITION);
        setViewsPosition(AudioPlayer.MIN_SEEK_POSITION);
        setTrackNameView("");
        setRunningString();

//        if(App.getAppContext().getTrackName() != null){
//            setTrackNameView(App.getAppContext().getTrackName());
//        }
    }

    private void setRunningString() {
        float[] size = MainActivity.getActivitySize();
        float width = MainActivity.getTextWidth(notes);

        if (size != null && size.length == 2 && width > 0) {
            int n = (int) (size[1] / width);
            String runningString = notes;
            if (n > 0) {
                for (int i = 0; i < n + 5; i++) {
                    runningString += notes;
                }
            }
            setTrackNameView(runningString);
        }
    }

    private void setViewListeners(ActionControl actionControl) {
//        view_expand.setOnClickListener(actionControl);
//        view_random.setOnClickListener(actionControl);
//        view_previous.setOnClickListener(actionControl);
//        view_play_pause.setOnClickListener(actionControl);
//        view_next.setOnClickListener(actionControl);
//        view_repeat.setOnClickListener(actionControl);
//        view_seek_bar.setOnSeekBarChangeListener(actionControl);
    }

    private void setState() {
//        if(!App.getAppContext().isExpand()){
//            App.getAppContext().setIsExpand(true);
//            view_expand.performClick();
//        }

//        if(App.getAppContext().isRandom()){
//            view_random.setImageResource(R.drawable.shuffle_active);
//        }

//        setRepeatImage(App.getAppContext().getRepeat());
    }

    private void setRepeatImage(int repeat) {
//        switch (repeat){
//            case App.REPEAT_NONE:
//                view_repeat.setImageResource(R.drawable.repeat_inactive);
//                break;
//            case App.REPEAT_ONE:
//                view_repeat.setImageResource(R.drawable.repeat_active_one);
//                break;
//            case App.REPEAT_ALL:
//                view_repeat.setImageResource(R.drawable.repeat_active);
//                break;
//        }
    }

    public void setPlayPauseImage(boolean playOrPause) {

        if (!playOrPause) {
            view_play_pause.setImageResource(R.drawable.play);
        } else {
            view_play_pause.setImageResource(R.drawable.pause);
        }
    }

    public static void setTrackNameView(String name) {
        if (view_track_name != null && name != null) {
            view_track_name.setText(name);
            view_track_name.setSelected(true);
            view_track_name.requestFocus();
        }
    }

    public static void setCountTracks() {
        if (view_track_number != null) {
            IPlaylist playlist = App.getAppContext().getPlayerConfig().getPlaylist();
            if (playlist != null) {
                synchronized (playlist) {
                    view_track_number.setText((playlist.position() + 1) + "/" + playlist.size());
                }
            } else {
                view_track_number.setText("0000/0000");
            }
        }
    }

    public void setViewsDuration(int position) {
        if (view_seek_bar != null && view_end_position != null) {
            view_seek_bar.setMax(position);
            view_end_position.setText(FindMedia.getDuration(position));
        }
    }

    public void setViewsPosition(int position) {
        if (view_seek_bar != null && view_end_position != null) {
            view_seek_bar.setProgress(position);
            view_start_position.setText(FindMedia.getDuration(position));
        }
    }


    /*
    * Action control
    * */
    public class ActionControl
            implements ImageButton.OnClickListener,
            SeekBar.OnSeekBarChangeListener {

        @Override
        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.expand: {
//                    Log.i(MainActivity.LOG_ACTIVITY, "onClick - " + v.getId());
////                    if(App.getAppContext().isExpand()){
////                        App.getAppContext().setIsExpand(false);
////                        view_track_name.setVisibility(View.GONE);
////                        view_start_position.setVisibility(View.GONE);
////                        view_end_position.setVisibility(View.GONE);
////                        view_track_number.setVisibility(View.GONE);
////                        view_expand.setImageResource(R.drawable.expand_active);
////                    } else {
////                            App.getAppContext().setIsExpand(true);
////                            view_track_name.setVisibility(View.VISIBLE);
////                            view_start_position.setVisibility(View.VISIBLE);
////                            view_end_position.setVisibility(View.VISIBLE);
////                            view_track_number.setVisibility(View.VISIBLE);
////                            view_expand.setImageResource(R.drawable.expand_inactive);
////                        }
//
//                    break;
//                }
//
//                case R.id.random: {
////                    if(!App.getAppContext().isRandom()){
////                        App.getAppContext().setIsRandom(true);
////                        view_random.setImageResource(R.drawable.shuffle_active);
////                    } else {
////                        App.getAppContext().setIsRandom(false);
////                        view_random.setImageResource(R.drawable.shuffle_inactive);
////                    }
//                    break;
//                }
//
//                case R.id.previous: {
//                    context.startService(new Intent(PlayerService.ACTION_PREVIOUS)
//                            .setPackage(context.getPackageName()));
//                    break;
//                }
//
//                case R.id.play_pause: {
//                    context.startService(new Intent(PlayerService.ACTION_PLAY)
//                            .putExtra(PlayerService.KEY_PLAY_PAUSE, PlayerService.KEY_PLAY_PAUSE)
//                            .setPackage(context.getPackageName()));
//                    break;
//                }
//
//                case R.id.next: {
//                    context.startService(new Intent(PlayerService.ACTION_NEXT)
//                            .setPackage(context.getPackageName()));
//                    break;
//                }
//
//                case R.id.repeat: {
////                    setRepeatImage(App.getAppContext().setRepeatAuto());
//                    break;
//                }
//            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(MainActivity.LOG_ACTIVITY, "onStopTrackingTouch");
            context.startService(new Intent(PlayerService.ACTION_SEEK)
                    .putExtra(PlayerService.KEY_SEEK, seekBar.getProgress())
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
