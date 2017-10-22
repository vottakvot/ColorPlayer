package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.control.explorer.FindMedia;
import ru.testsimpleapps.coloraudioplayer.control.explorer.FoldersArrayList;
import ru.testsimpleapps.coloraudioplayer.model.FileDataItem;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;


/*
* http://habrahabr.ru/post/133575/
* */
public class MediaFoldersAdapter
        extends BaseAdapter
        implements ListView.OnItemClickListener {

    private final Context context;
    private final LayoutInflater inflater;

    /*
    * Link on explorer list
    * */
    private final ListView explorerList;

    /*
    * View of list's header
    * */
    private final View viewHeader;

    /*
    * Button to go to the folders
    * */
    private final ImageButton explorerButtonBack;

    /*
    * To determine the folder or files
    * */
    private boolean isFolder = true;

    /*
    * All audio files, key - album or folder. It's active content
    * */
    private LinkedHashMap<String, FoldersArrayList<FileDataItem>> mediaTree;

    /*
    * Set of active keys
    * */
    private Set<String> mediaTreeKeys;

    /*
    * Collections with different types of sorts
    * */
    private LinkedHashMap<String, FoldersArrayList<FileDataItem>> foldersTree;
    private LinkedHashMap<String, FoldersArrayList<FileDataItem>> albumsTree;
    private LinkedHashMap<String, FoldersArrayList<FileDataItem>> artistsTree;

    /*
    * Sets for order
    * */
    private LinkedHashSet<String> foldersTreeSet;
    private LinkedHashSet<String> albumsTreeSet;
    private LinkedHashSet<String> artistsTreeSet;
    private TreeSet<String> sizeTreeSet;

    /*
    * Default type icon - folder
    * */
    private int currentIcoItem = 1;

    /*
    * Key of selected folder
    * */
    private String currentFoldersKey;

    /*
    * Position for return back from files
    * */
    private int currentFolderPosition = 1;

    /*
    * Adapter for files. It get only from MediaFoldersAdapter
    * */
    private MediaFilesAdapter currentMediaFilesAdapter;

    /*
    * Current file container
    * */
    private FoldersArrayList<FileDataItem> currentFoldersArrayList;

    public MediaFoldersAdapter(Context context, ListView explorerList, ImageButton explorerButtonBack) {
        super();
        this.context = context;
        this.explorerList = explorerList;
        this.explorerButtonBack = explorerButtonBack;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewHeader = inflater.inflate(R.layout.explorer_header, null);
        setIsFolder(true);
        updateAllMusic();
    }


    public void updateAllMusic() {
        FindMedia findMedia = new FindMedia(context);
        if (findMedia.findMedia()) {
            mediaTree = foldersTree = findMedia.getMediaTreeFolders();
            albumsTree = findMedia.getMediaTreeAlbums();
            artistsTree = findMedia.getMediaTreeArtists();

            mediaTreeKeys = foldersTreeSet = new LinkedHashSet<String>(foldersTree.keySet());
            albumsTreeSet = new LinkedHashSet<String>(albumsTree.keySet());
            artistsTreeSet = new LinkedHashSet<String>(artistsTree.keySet());
            sizeTreeSet = new TreeSet<String>(new CompareFoldersSize(mediaTree));
        }
    }

    public void setSortByFolders() {
        mediaTree = foldersTree;
        mediaTreeKeys = new LinkedHashSet<String>(mediaTree.keySet());
        currentIcoItem = 1;
        notifyDataSetChanged();
    }

    public void setSortByArtists() {
        mediaTree = artistsTree;
        mediaTreeKeys = artistsTreeSet;
        currentIcoItem = 2;
        notifyDataSetChanged();
    }

    public void setSortByAlbums() {
        mediaTree = albumsTree;
        mediaTreeKeys = albumsTreeSet;
        currentIcoItem = 3;
        notifyDataSetChanged();
    }

    public void setSortByAZ() {
        mediaTreeKeys = new TreeSet<String>(mediaTreeKeys);
        notifyDataSetChanged();
    }

    public void setSortBySize() {
        sizeTreeSet = new TreeSet<String>(new CompareFoldersSize(mediaTree));
        sizeTreeSet.addAll(mediaTreeKeys);
        mediaTreeKeys = sizeTreeSet;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (mediaTree != null && mediaTree.size() > 0) ? mediaTree.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return FindMedia.getMapMediaPosition(mediaTree, position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        public ImageView imageFolder;
        public TextView countFolder;
        public TextView nameFolder;
        public CheckBox checkFolder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;

        // Fill viewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.explorer_item_folder, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageFolder = (ImageView) view.findViewById(R.id.imageFolder);
            viewHolder.countFolder = (TextView) view.findViewById(R.id.countFolder);
            viewHolder.nameFolder = (TextView) view.findViewById(R.id.nameFolder);
            viewHolder.checkFolder = (CheckBox) view.findViewById(R.id.noteFolder);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }


        // Choose icon
        switch (currentIcoItem) {
            case 1:
                viewHolder.imageFolder.setImageResource(R.drawable.folders);
                break;
            case 2:
                viewHolder.imageFolder.setImageResource(R.drawable.artist);
                break;
            case 3:
                viewHolder.imageFolder.setImageResource(R.drawable.album);
                break;
            default:
                viewHolder.imageFolder.setImageResource(R.drawable.folders);
                break;
        }

        String key = FindMedia.getKeyMediaPosition(mediaTreeKeys, position);

        viewHolder.countFolder.setText(Integer.toString(mediaTree.get(key).size()));
        viewHolder.nameFolder.setText(key);
        viewHolder.checkFolder.setTag(position);
        viewHolder.checkFolder.setOnCheckedChangeListener(checkFolder);
        viewHolder.checkFolder.setChecked(mediaTree.get(key).isChecked());

        if (mediaTree.get(key).isChecked()) {
            view.setBackgroundResource(R.drawable.drawable_listview_item_explorer);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(MainActivity.LOG_ACTIVITY, this.getClass().getName().toString() + " - onItemClick - " + position);

        if (isFolder()) {
            // Set buttonBack invisible
            explorerButtonBack.setVisibility(ImageButton.VISIBLE);

            // Change header's name
            //String folderName = (String) parent.getItemAtPosition(position);
            currentFolderPosition = position - 1;
            currentFoldersKey = (String) FindMedia.getKeyMediaPosition(mediaTreeKeys, currentFolderPosition);

            // TextView header
            TextView textView = (TextView) viewHeader.findViewById(R.id.explorer_list_header);
            textView.setText(currentFoldersKey);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.f);
            // Repaint view
            viewHeader.invalidate();

            currentFoldersArrayList = mediaTree.get(currentFoldersKey);
            // Set new adapter for explorer list
            currentMediaFilesAdapter = new MediaFilesAdapter(context, currentFoldersArrayList);
            explorerList.setAdapter(currentMediaFilesAdapter);

            // Not in folder
            setIsFolder(false);
        }
    }

    /*
    * For count files in folder
    * */
    class CompareFoldersSize implements Comparator<String> {
        LinkedHashMap<String, FoldersArrayList<FileDataItem>> container;

        public CompareFoldersSize(LinkedHashMap<String, FoldersArrayList<FileDataItem>> container) {
            this.container = container;
        }

        @Override
        public int compare(String lhs, String rhs) {
            if (container.get(lhs).size() >= container.get(rhs).size()) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    private CheckBox.OnCheckedChangeListener checkFolder = new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d(MainActivity.LOG_ACTIVITY, this.getClass().getName().toString() + " - OnCheckedChangeListener - " + isChecked);
            mediaTree.get(FindMedia.getKeyMediaPosition(mediaTreeKeys, (Integer) buttonView.getTag())).setChecked(isChecked);
            notifyDataSetChanged();
        }
    };

    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public View getListHeader() {
        return viewHeader;
    }

    public LinkedHashMap<String, FoldersArrayList<FileDataItem>> getMediaTree() {
        return mediaTree;
    }

    public Set<String> getMediaTreeKeys() {
        return mediaTreeKeys;
    }

    public MediaFilesAdapter getCurrentMediaFilesAdapter() {
        return currentMediaFilesAdapter;
    }

    public FoldersArrayList<FileDataItem> getCurrentFoldersArrayList() {
        return currentFoldersArrayList;
    }

    public String getCurrentFoldersKey() {
        return currentFoldersKey;
    }

    public int getCurrentFolderPosition() {
        return currentFolderPosition;
    }
}