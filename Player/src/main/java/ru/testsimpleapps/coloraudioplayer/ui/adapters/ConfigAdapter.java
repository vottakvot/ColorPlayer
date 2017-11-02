package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.testsimpleapps.coloraudioplayer.PlayerService;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.data.DrawerDataItem;
import ru.testsimpleapps.coloraudioplayer.ui.activities.PreferencesActivity;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.ColorsDialog;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.EqualizerDialog;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.PlaylistChooseDialog;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.PlaylistCreateDialog;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.TimerDialog;


/*
* http://habrahabr.ru/post/133575/
* */
public class ConfigAdapter extends BaseAdapter implements ListView.OnItemClickListener {

    private final Context context;
    private final LayoutInflater inflater;

    /*
    * Name items of config
    * */
    private String[] nameItems;
    private ArrayList<DrawerDataItem> configList;
    private final PlaylistCreateDialog createPlaylistDialog;
    private final EqualizerDialog equalizerDialog;
    private final TimerDialog timerDialog;
    private final ColorsDialog colorsDialog;
    private final DrawerLayout drawerLayout;
    private PlaylistChooseDialog getPlaylistDialog;

    public ConfigAdapter(Context context, DrawerLayout drawerLayout) {
        super();
        this.context = context;
        this.drawerLayout = drawerLayout;
        this.configList = new ArrayList<DrawerDataItem>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        createPlaylistDialog = new PlaylistCreateDialog(context);
        equalizerDialog = new EqualizerDialog(context);
        timerDialog = new TimerDialog(context);
        colorsDialog = new ColorsDialog(context);

        nameItems = context.getResources().getStringArray(R.array.config_drawer_items);
        setConfigItems();
    }

    @Override
    public int getCount() {
        return configList.size();
    }

    @Override
    public Object getItem(int position) {
        return configList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        public ImageView imageConfig;
        public TextView nameConfig;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;

        // Fill viewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.config_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageConfig = (ImageView) view.findViewById(R.id.imageConfig);
            viewHolder.nameConfig = (TextView) view.findViewById(R.id.nameConfig);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        DrawerDataItem drawerDataItem = configList.get(position);
        viewHolder.imageConfig.setImageResource(drawerDataItem.getImage());
        viewHolder.nameConfig.setText(drawerDataItem.getName());

        return view;
    }


    /*
    * Generate data for adapter
    * */
    private void setConfigItems() {
        for (int i = 0; i < nameItems.length; i++) {
            switch (i) {
                case 0:
                    configList.add(new DrawerDataItem(R.drawable.add_playlist, nameItems[i]));
                    break;
                case 1:
                    configList.add(new DrawerDataItem(R.drawable.choose_playlist, nameItems[i]));
                    break;
                case 2:
                    configList.add(new DrawerDataItem(R.drawable.equalizer, nameItems[i]));
                    break;
                case 3:
                    configList.add(new DrawerDataItem(R.drawable.timer, nameItems[i]));
                    break;
                case 4:
                    configList.add(new DrawerDataItem(R.drawable.preference, nameItems[i]));
                    break;
                case 5:
                    configList.add(new DrawerDataItem(R.drawable.color, nameItems[i]));
                    break;
                case 6:
                    configList.add(new DrawerDataItem(R.drawable.door, nameItems[i]));
                    break;
                default:
                    configList.add(new DrawerDataItem(R.drawable.door, nameItems[i]));
                    break;
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        drawerLayout.closeDrawers();
        switch (position) {
            case 0:
                createPlaylistDialog.show();
                break;
            case 1:
                getPlaylistDialog = new PlaylistChooseDialog(context);
                getPlaylistDialog.show();
                break;
            case 2:
                equalizerDialog.show();
                break;
            case 3:
                timerDialog.show();
                break;
            case 4:
                context.startActivity(new Intent(context, PreferencesActivity.class));
                break;
            case 5:
                colorsDialog.show();
                break;
            case 6:
                context.startService(new Intent(PlayerService.ACTION_EXIT)
                        .setPackage(context.getPackageName()));
                break;
            default:
                break;
        }
    }

    public TimerDialog getTimerDialog() {
        return timerDialog;
    }

    public EqualizerDialog getEqualizerDialog() {
        return equalizerDialog;
    }
}
