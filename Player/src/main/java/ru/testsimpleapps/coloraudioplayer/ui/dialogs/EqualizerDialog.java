package ru.testsimpleapps.coloraudioplayer.ui.dialogs;


import android.content.Context;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;

public class EqualizerDialog extends BaseDialog implements SeekBar.OnSeekBarChangeListener,
        Spinner.OnItemSelectedListener {

    public static final int NORMAL_PRIORITY = 0;
    private static final int MAX_RANGE = 20;
    private static final String ZERO = "|";
    private static final String INFINITY = "\u221E";

    private final Context mContext;
    @BindView(R.id.equalizer_choose_mode_spinner)
    protected Spinner mEqualizerChooseModeSpinner;
    @BindView(R.id.equalizer_settings_recycle)
    protected RecyclerView mEqualizerSettingsRecycle;
    @BindView(R.id.equalizer_cancel)
    protected Button mEqualizerCancel;

    private Equalizer equalizer;
    private BassBoost bassBoost;

    private int minLevel = 0;
    private int maxLevel = 0;

    private HashMap<Short, String> defaultModes;

    public EqualizerDialog(Context context) {
        super(context);
        mContext = context;
//        equalizerDialogAdapter = new EqualizerDialogAdapter();
//        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        equalizerView = (View) inflater.inflate(R.layout.dialog_equalizer, null);
//        setConfigListView = (ListView) equalizerView.findViewById(R.id.equalizerSettings);
//        setConfigListView.setAdapter(equalizerDialogAdapter);
//        chooseMode = (Spinner) equalizerView.findViewById(R.id.equalizerChooseMode);
    }

    @Override
    public void show() {
        super.show();
        initMinMaxLevel();
        initSpinner();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_equalizer);
        ButterKnife.bind(this);
    }

    private void initMinMaxLevel() {
        if (equalizer != null) {
            minLevel = (equalizer.getBandLevelRange()[0] != 0) ? equalizer.getBandLevelRange()[0] : 1;
            maxLevel = (equalizer.getBandLevelRange()[1] != 0) ? equalizer.getBandLevelRange()[1] : 1;
        }
    }

    private void initSpinner() {
        if (equalizer != null && equalizer.getNumberOfPresets() > 0) {
            defaultModes = new HashMap<>();
            for (Short i = 0; i < equalizer.getNumberOfPresets(); i++) {
                defaultModes.put(i, equalizer.getPresetName(i));
            }

            List<String> spinnerItems = new ArrayList<>(defaultModes.values());
            spinnerItems.add(mContext.getResources().getString(R.string.equalizer_custom));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, spinnerItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            chooseMode.setAdapter(adapter);
//            chooseMode.setOnItemSelectedListener(this);

//            if(App.getContext().getEqualizerPresent() != -1){
//                chooseMode.setSelection(App.getContext().getEqualizerPresent());
//            } else {
//                    chooseMode.setSelection(equalizer.getNumberOfPresets());
//                }
        }
    }

    private String milliHzToString(int milliHz) {
        if (milliHz < 1000)
            return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }

    private short getBandPosition(int position) {
        return (short) ((bassBoost != null) ? (position - 1) : position);
    }

    /*
    * Seekbar listeners
    * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(MainActivity.TAG, this.getClass().getName().toString() + "onStopTrackingTouch - tag: " + seekBar.getTag());
        if (bassBoost != null && (int) seekBar.getTag() == 0) {
            bassBoost.setStrength((short) (seekBar.getProgress() * 50));
//            App.getContext().setBassBoostStrength(bassBoost.getRoundedStrength());
        } else if (equalizer != null) {
            short newLevel = (short) (minLevel + (maxLevel - minLevel) * seekBar.getProgress() / seekBar.getMax());
            short newBand = getBandPosition((int) seekBar.getTag());
            equalizer.setBandLevel(newBand, newLevel);
        }
    }

    /*
    * Spinner listeners
    * */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (defaultModes != null && equalizer != null) {
//            if(position < equalizer.getNumberOfPresets()) {
//                equalizer.usePreset((short) position);
//                App.getContext().setEqualizerPresent((short) position);
//            } else if(App.getContext().getEqualizerBands() != null &&
//                    App.getContext().getEqualizerBands().length == equalizer.getNumberOfBands()){
//                for(int i = 0; i < equalizer.getNumberOfBands(); i++){
//                    equalizer.setBandLevel((short)i, (short) App.getContext().getEqualizerBands()[i]);
//                }
//            }
//
//            equalizerDialogAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*
    * Equalizer adapter
    * */
//    private class EqualizerDialogAdapter extends RecyclerView.Adapter {
//
//        @Override
//        public int getCount() {
//            int count = 0;
//
//            if (bassBoost != null) {
//                ++count;
//            }
//
//            if (equalizer != null && equalizer.getNumberOfBands() > 0) {
//                count += equalizer.getNumberOfBands();
//            }
//
//            return count;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return null;
//        }
//
//        @Override
//        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public int getItemCount() {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = null;
//            ViewHolder viewHolder = null;
//
//            // Fill viewHolder
//            if (convertView == null) {
//                view = inflater.inflate(R.layout.equalizer_item, parent, false);
//                viewHolder = new ViewHolder();
//                viewHolder.equalizerSeek = (SeekBar) view.findViewById(R.id.seekEqualizer);
//                viewHolder.minText = (TextView) view.findViewById(R.id.minEqualizer);
//                viewHolder.zeroText = (TextView) view.findViewById(R.id.zeroEqualizer);
//                viewHolder.maxText = (TextView) view.findViewById(R.id.maxEqualizer);
//                viewHolder.bassBoost = (TextView) view.findViewById(R.id.bassBoostNote);
//                view.setTag(viewHolder);
//            } else {
//                view = convertView;
//                viewHolder = (ViewHolder) view.getTag();
//            }
//
//            if (position == 0 && bassBoost != null) {
//                viewHolder.bassBoost.setVisibility(TextView.VISIBLE);
//                viewHolder.bassBoost.setText("BassBoost");
//                viewHolder.equalizerSeek.setProgress(bassBoost.getRoundedStrength() / 50);
//                viewHolder.minText.setText("0");
//                viewHolder.zeroText.setText("");
//                viewHolder.maxText.setText(INFINITY);
//            } else if (equalizer != null) {
//                viewHolder.bassBoost.setVisibility(TextView.GONE);
//                Equalizer.Settings settings = equalizer.getProperties();
//                short[] bands = settings.bandLevels;
//                int progress = bands[getBandPosition(position)] / ((maxLevel - minLevel) / viewHolder.equalizerSeek.getMax()) + MAX_RANGE / 2;
//                viewHolder.equalizerSeek.setProgress(progress);
//                int[] range = equalizer.getBandFreqRange(getBandPosition(position));
//                viewHolder.minText.setText(milliHzToString(range[0]));
//                viewHolder.zeroText.setText(ZERO);
//                viewHolder.maxText.setText(milliHzToString(range[1]));
//            }
//
//            viewHolder.equalizerSeek.setTag(position);
//            viewHolder.equalizerSeek.setMax(MAX_RANGE);
//            viewHolder.equalizerSeek.bringToFront();
//            viewHolder.equalizerSeek.setOnSeekBarChangeListener(EqualizerDialog.this);
//
//
//            return view;
//        }
//
//    }
//
//    static class ViewHolder {
//        public SeekBar equalizerSeek;
//        public TextView minText;
//        public TextView zeroText;
//        public TextView maxText;
//        public TextView bassBoost;
//    }
}
