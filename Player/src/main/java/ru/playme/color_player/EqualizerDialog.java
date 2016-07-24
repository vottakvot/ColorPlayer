package ru.playme.color_player;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EqualizerDialog
        extends Dialog
        implements SeekBar.OnSeekBarChangeListener,
                    Spinner.OnItemSelectedListener {

    public static final int NORMAL_PRIORITY = 0;
    private final int MAX_RANGE = 20;
    private final String ZERO = "|";
    private final String INFINITY = "\u221E";
    private final Context context;

    private final LayoutInflater inflater;
    private final View equalizerView;
    private final ListView setConfigListView;
    private final Spinner chooseMode;

    private Equalizer equalizer = null;
    private BassBoost bassBoost = null;
    private final EqualizerDialogAdapter equalizerDialogAdapter;

    private int minLevel = 0;
    private int maxLevel = 0;

    private boolean isSize = false;
    private HashMap<Short, String> defaultModes = null;

    public EqualizerDialog(Context context, Equalizer equalizer, BassBoost bassBoost) {
        super(context);
        this.context = context;
        this.equalizer = equalizer;
        this.bassBoost = bassBoost;

        equalizerDialogAdapter = new EqualizerDialogAdapter();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        equalizerView = (View) inflater.inflate(R.layout.equalizer_dialog, null);
        setConfigListView = (ListView) equalizerView.findViewById(R.id.equalizerSettings);
        setConfigListView.setAdapter(equalizerDialogAdapter);
        chooseMode = (Spinner) equalizerView.findViewById(R.id.equalizerChooseMode);
    }

    private void initMinMaxLevel(){
        if(equalizer != null){
            minLevel = (equalizer.getBandLevelRange()[0] != 0)? equalizer.getBandLevelRange()[0] : 1;
            maxLevel = (equalizer.getBandLevelRange()[1] != 0)? equalizer.getBandLevelRange()[1] : 1;
        }
    }

    private void initSpinner(){
        if(equalizer != null && equalizer.getNumberOfPresets() > 0){
            defaultModes = new HashMap<Short, String>();
            for(Short i = 0; i < equalizer.getNumberOfPresets(); i++){
                defaultModes.put(i, equalizer.getPresetName(i));
            }

            List<String> spinnerItems = new ArrayList<String>(defaultModes.values());
            spinnerItems.add(context.getResources().getString(R.string.equalizer_custom));

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            chooseMode.setAdapter(adapter);
            chooseMode.setOnItemSelectedListener(this);

            if(PlayerApplication.getPlayerApplication().getEqualizerPresent() != -1){
                chooseMode.setSelection(PlayerApplication.getPlayerApplication().getEqualizerPresent());
            } else {
                    chooseMode.setSelection(equalizer.getNumberOfPresets());
                }
        }
    }

    private String milliHzToString (int milliHz){
        if (milliHz < 1000)
            return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }

    private short getBandPosition(int position){
        return (short)((bassBoost != null)? (position - 1) : position);
    }

    private void setCustomeAudio() {
        if(equalizer != null){
            if(PlayerApplication.getPlayerApplication().getEqualizerBands() == null ||
                PlayerApplication.getPlayerApplication().getEqualizerBands().length != equalizer.getProperties().numBands){
                    PlayerApplication.getPlayerApplication().setEqualizerBands(new short[equalizer.getProperties().numBands]);
            }

            for(int i = 0; i < equalizer.getProperties().numBands; i++){
                PlayerApplication.getPlayerApplication().getEqualizerBands()[i] = equalizer.getProperties().bandLevels[i];
            }
        }

        PlayerApplication.getPlayerApplication().setEqualizerPresent((short)-1);
    }

    // Dialog
    @Override
    public void show() {
        equalizer = PlayService.getEqualizer();
        bassBoost = PlayService.getBassBoost();
        
        initMinMaxLevel();
        initSpinner();

        super.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(equalizerView);

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

    // Seekbar listeners
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(MainActivity.LOG_ACTIVITY, this.getClass().getName().toString() + "onStopTrackingTouch - tag: " + seekBar.getTag());

        if(bassBoost != null && (int)seekBar.getTag() == 0){
            bassBoost.setStrength((short)(seekBar.getProgress() * 50));
            PlayerApplication.getPlayerApplication().setBassBoostStrength(bassBoost.getRoundedStrength());
        } else {
            short newLevel = (short)(minLevel + (maxLevel - minLevel) * seekBar.getProgress() / seekBar.getMax());
            short newBand = getBandPosition((int)seekBar.getTag());
            equalizer.setBandLevel(newBand, newLevel);
            setCustomeAudio();
        }
    }


    // Spinner listeners
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(defaultModes != null && equalizer != null){
            if(position < equalizer.getNumberOfPresets()) {
                equalizer.usePreset((short) position);
                PlayerApplication.getPlayerApplication().setEqualizerPresent((short) position);
            } else if(PlayerApplication.getPlayerApplication().getEqualizerBands() != null &&
                        PlayerApplication.getPlayerApplication().getEqualizerBands().length == equalizer.getNumberOfBands()){
                        for(int i = 0; i < equalizer.getNumberOfBands(); i++){
                            equalizer.setBandLevel((short)i, (short) PlayerApplication.getPlayerApplication().getEqualizerBands()[i]);
                        }
                    }

            equalizerDialogAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    static class ViewHolder {
        public SeekBar equalizerSeek;
        public TextView minText;
        public TextView zeroText;
        public TextView maxText;
        public TextView bassBoost;
    }

    // Config adapter
    private class EqualizerDialogAdapter
            extends BaseAdapter {

        @Override
        public int getCount() {
            int count = 0;

            if(bassBoost != null){
                ++count;
            }

            if(equalizer != null && equalizer.getNumberOfBands() > 0){
                count += equalizer.getNumberOfBands();
            }

            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder viewHolder = null;

            // Fill viewHolder
            if (convertView == null) {
                view = inflater.inflate(R.layout.equalizer_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.equalizerSeek = (SeekBar) view.findViewById(R.id.seekEqualizer);
                viewHolder.minText = (TextView) view.findViewById(R.id.minEqualizer);
                viewHolder.zeroText = (TextView) view.findViewById(R.id.zeroEqualizer);
                viewHolder.maxText = (TextView) view.findViewById(R.id.maxEqualizer);
                viewHolder.bassBoost = (TextView) view.findViewById(R.id.bassBoostNote);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            try {
                    if(position == 0 && bassBoost != null){
                        viewHolder.bassBoost.setVisibility(TextView.VISIBLE);
                        viewHolder.bassBoost.setText("BassBoost");
                        viewHolder.equalizerSeek.setProgress(bassBoost.getRoundedStrength() / 50);
                        viewHolder.minText.setText("0");
                        viewHolder.zeroText.setText("");
                        viewHolder.maxText.setText(INFINITY);
                    } else if(equalizer != null) {
                        viewHolder.bassBoost.setVisibility(TextView.GONE);
                        Equalizer.Settings settings = equalizer.getProperties();
                        short [] bands = settings.bandLevels;
                        int progress = bands[getBandPosition(position)] / ((maxLevel - minLevel) / viewHolder.equalizerSeek.getMax()) + MAX_RANGE / 2;
                        viewHolder.equalizerSeek.setProgress(progress);
                        int [] range = equalizer.getBandFreqRange(getBandPosition(position));
                        viewHolder.minText.setText(milliHzToString(range[0]));
                        viewHolder.zeroText.setText(ZERO);
                        viewHolder.maxText.setText(milliHzToString(range[1]));
                    }

                    viewHolder.equalizerSeek.setTag(position);
                    viewHolder.equalizerSeek.setMax(MAX_RANGE);
                    viewHolder.equalizerSeek.bringToFront();
                    viewHolder.equalizerSeek.setOnSeekBarChangeListener(EqualizerDialog.this);
            } catch (RuntimeException e){
                    e.printStackTrace();
                }

            return view;
        }
    }
}
