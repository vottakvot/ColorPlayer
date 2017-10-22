package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;

import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.control.explorer.FindMedia;
import ru.testsimpleapps.coloraudioplayer.control.explorer.FoldersArrayList;
import ru.testsimpleapps.coloraudioplayer.control.tools.FileTool;
import ru.testsimpleapps.coloraudioplayer.model.FileDataItem;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;


/*
* http://habrahabr.ru/post/133575/
* */
public class MediaFilesAdapter
        extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    /*
    * Files in current folder
    * */
    private FoldersArrayList<FileDataItem> files;

    public MediaFilesAdapter(Context context, FoldersArrayList<FileDataItem> files) {
        super();
        this.context = context;
        this.files = files;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        public ImageView imageFile;
        public TextView nameFile;
        public TextView durationFile;
        public CheckBox checkFile;
    }

    @Override
    public int getCount() {
        return (files != null && files.size() > 0) ? files.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (files != null && files.size() > position) ? files.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;

        if (convertView == null) {
            view = inflater.inflate(R.layout.explorer_item_track, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageFile = (ImageView) view.findViewById(R.id.imageFiles);
            viewHolder.nameFile = (TextView) view.findViewById(R.id.nameFiles);
            viewHolder.durationFile = (TextView) view.findViewById(R.id.durationFiles);
            viewHolder.checkFile = (CheckBox) view.findViewById(R.id.noteFiles);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        FileDataItem fileDataItem = files.get(position);

        viewHolder.imageFile.setImageResource(R.drawable.audio);
        viewHolder.nameFile.setText(fileDataItem.getName());

        String duration = FindMedia.getDuration(fileDataItem.getDuration());
        if (duration.length() > 5) {
            duration = duration.substring(0, 1) + "H:" + duration.substring(2, 4);
        }

        viewHolder.durationFile.setText(duration);
        viewHolder.checkFile.setTag(position);
        viewHolder.checkFile.setOnCheckedChangeListener(checkFile);
        viewHolder.checkFile.setChecked(fileDataItem.isChecked());

        if (fileDataItem.isChecked()) {
            view.setBackgroundResource(R.drawable.drawable_listview_item_explorer);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }

        return view;
    }

    private CheckBox.OnCheckedChangeListener checkFile = new CheckBox.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(MainActivity.LOG_ACTIVITY, this.getClass().getName().toString() + " - OnCheckedChangeListener - Tag №" + (Integer) buttonView.getTag() + " = " + isChecked);
            files.get((Integer) buttonView.getTag()).setIsChecked(isChecked);
            notifyDataSetChanged();
        }
    };

    /*
    * For names
    * */
    class CompareFilesName implements Comparator<FileDataItem> {
        FoldersArrayList<FileDataItem> container;

        public CompareFilesName(FoldersArrayList<FileDataItem> container) {
            this.container = container;
        }

        @Override
        public int compare(FileDataItem lhs, FileDataItem rhs) {
            return FileTool.getName(lhs.getName()).compareTo(FileTool.getName(rhs.getName()));
        }
    }

    /*
    * For track length
    * */
    class CompareFilesLength implements Comparator<FileDataItem> {
        FoldersArrayList<FileDataItem> container;

        public CompareFilesLength(FoldersArrayList<FileDataItem> container) {
            this.container = container;
        }

        @Override
        public int compare(FileDataItem lhs, FileDataItem rhs) {
            return ((Long) lhs.getDuration()).compareTo((Long) rhs.getDuration());
        }
    }

    public void setSortByName() {
        Collections.sort(files, new CompareFilesName(files));
        notifyDataSetChanged();
    }

    public void setSortByLength() {
        Collections.sort(files, new CompareFilesLength(files));
        notifyDataSetChanged();
    }

    public FoldersArrayList<FileDataItem> getFiles() {
        return files;
    }
}
