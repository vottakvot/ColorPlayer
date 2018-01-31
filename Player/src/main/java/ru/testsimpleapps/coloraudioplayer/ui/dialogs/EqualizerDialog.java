package ru.testsimpleapps.coloraudioplayer.ui.dialogs;


import android.content.Context;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.data.DrawerItem;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.BaseListAdapter;

public class EqualizerDialog extends BaseDialog implements SeekBar.OnSeekBarChangeListener,
        Spinner.OnItemSelectedListener {

    public static final String TAG = EqualizerDialog.class.getSimpleName();
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

    private EqualizerAdapter mEqualizerAdapter;
    private HashMap<Short, String> mDefaultModes;
    private Equalizer mEqualizer;
    private BassBoost mBassBoost;

    private int mMinLevel = 0;
    private int mMaxLevel = 0;

    public EqualizerDialog(Context context) {
        super(context);
        mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @OnClick(R.id.equalizer_cancel)
    protected void onCancelClick() {
        dismiss();
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
        Log.d(TAG, this.getClass().getName().toString() + "onStopTrackingTouch - tag: " + seekBar.getTag());
        if ((int)seekBar.getTag() == 0) {
            mBassBoost.setStrength((short) (seekBar.getProgress() * 50));
        } else {
            short newLevel = (short) (mMinLevel + (mMaxLevel - mMinLevel) * seekBar.getProgress() / seekBar.getMax());
            short newBand = getBandPosition((int) seekBar.getTag());
            mEqualizer.setBandLevel(newBand, newLevel);
        }
    }

    /*
    * Spinner listeners
    * */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void show(final int sessionId) {
        super.show();
        initSession(sessionId);
    }

    private boolean initSession(final int sessionId) {
        try {
            initEqualizer(sessionId);
            mBassBoost = new BassBoost(NORMAL_PRIORITY, sessionId);
        } catch (RuntimeException e) {
            return false;
        }

        return true;
    }

    private void init() {
        setContentView(R.layout.dialog_equalizer);
        ButterKnife.bind(this);

        mEqualizerAdapter = new EqualizerAdapter(mContext);
        mEqualizerSettingsRecycle.setLayoutManager(new LinearLayoutManager(mContext));
        mEqualizerSettingsRecycle.setAdapter(mEqualizerAdapter);
    }

    private void initEqualizer(final int sessionId) {
        mEqualizer = new Equalizer(NORMAL_PRIORITY, sessionId);
        mMinLevel = (mEqualizer.getBandLevelRange()[0] != 0) ? mEqualizer.getBandLevelRange()[0] : 1;
        mMaxLevel = (mEqualizer.getBandLevelRange()[1] != 0) ? mEqualizer.getBandLevelRange()[1] : 1;
        initEqualizerSpinner();
    }

    private void initEqualizerSpinner() {
        if (mEqualizer.getNumberOfPresets() > 0) {
            mDefaultModes = new HashMap<>();
            for (Short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
                mDefaultModes.put(i, mEqualizer.getPresetName(i));
            }

            final List<String> spinnerItems = new ArrayList<>(mDefaultModes.values());
            spinnerItems.add(mContext.getResources().getString(R.string.equalizer_custom));

            final ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, spinnerItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mEqualizerChooseModeSpinner.setAdapter(adapter);
            mEqualizerChooseModeSpinner.setOnItemSelectedListener(this);
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
        return (short) ((mBassBoost != null) ? (position - 1) : position);
    }


    /*
    * Equalizer adapter
    * */
    protected class EqualizerAdapter extends BaseListAdapter<DrawerItem> {

        private final Context mContext;

        public EqualizerAdapter(@NonNull Context context) {
            mContext = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.equalizer_item, viewGroup, false);
            return new ViewHolderItem(viewItem);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ViewHolderItem viewHolder = (ViewHolderItem) holder;

            if (position == 0 && mBassBoost != null) {
                viewHolder.mEqualizerItemText.setVisibility(TextView.VISIBLE);
                viewHolder.mEqualizerItemText.setText(mContext.getString(R.string.equalizer_bass_boost));
                viewHolder.mEqualizerItemSeek.setProgress(mBassBoost.getRoundedStrength() / 50);
                viewHolder.mEqualizerMaxText.setText(INFINITY);
            } else if (mEqualizer != null) {
                viewHolder.mEqualizerItemText.setVisibility(TextView.GONE);
                final Equalizer.Settings settings = mEqualizer.getProperties();
                final short[] bands = settings.bandLevels;
                final int progress = bands[getBandPosition(position)] / ((mMaxLevel - mMinLevel) / viewHolder.mEqualizerItemSeek.getMax()) + MAX_RANGE / 2;
                viewHolder.mEqualizerItemSeek.setProgress(progress);
                final int[] range = mEqualizer.getBandFreqRange(getBandPosition(position));
                viewHolder.mEqualizerMaxText.setText(milliHzToString(range[1]));
            }

            viewHolder.mEqualizerItemSeek.setTag(position);
            viewHolder.mEqualizerItemSeek.setMax(MAX_RANGE);
            viewHolder.mEqualizerItemSeek.bringToFront();
            viewHolder.mEqualizerItemSeek.setOnSeekBarChangeListener(EqualizerDialog.this);
        }

        @Override
        public int getItemCount() {
            int count = 0;

            if (mBassBoost != null) {
                ++count;
            }

            if (mEqualizer != null) {
                count += mEqualizer.getNumberOfBands();
            }

            return count;
        }

        protected class ViewHolderItem extends RecyclerView.ViewHolder {

            @BindView(R.id.equalizer_item_text)
            TextView mEqualizerItemText;
            @BindView(R.id.equalizer_item_seek)
            SeekBar mEqualizerItemSeek;
            @BindView(R.id.equalizer_max_text)
            TextView mEqualizerMaxText;

            public ViewHolderItem(final View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(view, getLayoutPosition());
                        }
                    }
                });
            }
        }

    }

}
