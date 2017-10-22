package ru.testsimpleapps.coloraudioplayer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import ru.testsimpleapps.coloraudioplayer.PlayerApplication;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.PlaylistAdapter;
import ru.testsimpleapps.coloraudioplayer.control.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.control.player.playlist.IPlaylist;
import ru.testsimpleapps.coloraudioplayer.control.player.playlist.cursor.CursorPlaylist;
import ru.testsimpleapps.coloraudioplayer.control.player.playlist.cursor.CursorTool;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.PlaylistRenameDialog;
import ru.testsimpleapps.coloraudioplayer.model.FileDataItem;
import ru.testsimpleapps.coloraudioplayer.control.explorer.FoldersArrayList;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.MediaFoldersAdapter;

public class MainPages {

    private static final int PAGE_COUNT = 2;
    private static Context context;
    private final ViewPager viewPager;
    private final MainPages.AdapterForPages tabsAdapter;
    private final FragmentManager fragmentManager;

    private static View playlistHeader;
    private static TextView textPlaylistHeader;
    private static RecyclerView playlistView;
    private static ImageButton playlistSearchButton;
    private static EditText playlistSearchInput;
    private static RelativeLayout playlistSearchLayout;
    private static View explorerHeader;
    private static TextView textExplorerHeader;
    private static ListView explorerListView;
    private static ImageButton explorerButtonBack;
    private static ImageButton explorerButtonAdd;

    private static PlayerConfig mPlayerConfig;

    /*
    * Playlist adapter
    * */
    private static PlaylistAdapter playlistAdapter;
    private static MediaFoldersAdapter folderlistAdapter;

    private static PlaylistRenameDialog playlistRenameDialog;

    public MainPages(Context context, ViewPager viewPager, FragmentManager fragmentManager) {
        this.context = context;
        this.viewPager = viewPager;
        this.fragmentManager = fragmentManager;
        this.tabsAdapter = new AdapterForPages(fragmentManager);

        mPlayerConfig = PlayerApplication.getPlayerApplication().getPlayerConfig();
        viewPager.setAdapter(tabsAdapter);
    }

    /*
    * Fragments for ViewPager
    * */
    public static class PageFragment extends Fragment {
        static final String PAGE_NUMBER = "page_number";
        int pageNumber;
        View view;
        int playlistSelectPosition = -1;

        static PageFragment newInstance(int page) {
            PageFragment pageFragment = new PageFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(PAGE_NUMBER, page);
            pageFragment.setArguments(arguments);
            return pageFragment;
        }

        static public void changePlaylistBackground(){
            if(playlistView != null && playlistAdapter != null){
                if(playlistAdapter.getItemCount() <= 0){
                    if(MainActivity.getOrientation()){
                        playlistView.setBackgroundResource(R.drawable.hints_vertical_playlist);
                    } else {
                        playlistView.setBackgroundResource(R.drawable.hints_horizontal_playlist);
                    }
                } else {
                    playlistView.setBackgroundResource(R.drawable.drawable_playlist_background);
                }
            }
        }

        static public void changeExplorerBackground(){
            if(explorerListView != null && folderlistAdapter != null){
                if(folderlistAdapter.getCount() <= 0){
                    if(MainActivity.getOrientation()){
                        explorerListView.setBackgroundResource(R.drawable.hints_vertical_explorer);
                    } else {
                        explorerListView.setBackgroundResource(R.drawable.hints_horizontal_explorer);
                    }
                } else {
                    explorerListView.setBackgroundResource(R.drawable.drawable_explorer_background);
                }
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            pageNumber = getArguments().getInt(PAGE_NUMBER);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {

            switch(pageNumber){
                // First page with playlist
                case 0 : {
                    view = inflater.inflate(R.layout.playlist_list_tracks, null);
                    playlistView = (RecyclerView) view.findViewById(R.id.playlist_list_fragment);
                    playlistAdapter = new PlaylistAdapter(context);
                    playlistHeader = playlistAdapter.getPlaylistHeader();
                    playlistHeader.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            playlistRenameDialog.show();
                            return true;
                        }
                    });

                    textPlaylistHeader = (TextView)playlistHeader.findViewById(R.id.playlist_header);
                    textPlaylistHeader.setText(CursorTool.getPlaylistNameById(context.getContentResolver(), mPlayerConfig.getPlaylistId()));

                    playlistRenameDialog = new PlaylistRenameDialog(context);
                    playlistSearchButton = (ImageButton) view.findViewById(R.id.searchTrackButton);
                    playlistSearchInput = (EditText) view.findViewById(R.id.searchTrackInput);
                    playlistSearchLayout = (RelativeLayout) view.findViewById(R.id.searchPlaylistLayout);

                    playlistSearchInput.setVisibility(EditText.INVISIBLE);
                    playlistSearchInput.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                playlistSearchInput.setText("");
                                playlistSearchInput.setVisibility(EditText.INVISIBLE);
                                playlistAdapter.setSearchPosition(-1);
                            }
                        }
                    });

                    // Search by number of track or words
                    playlistSearchButton.setOnClickListener(new ImageButton.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (playlistSearchInput.getVisibility() == EditText.INVISIBLE) {
                                playlistSearchInput.setVisibility(EditText.VISIBLE);
                                playlistSearchInput.requestFocus();
                            } else {
                                try{
                                    if (!playlistSearchInput.getText().toString().trim().equals("")) {
                                        // If is number, then to to position
//                                        if (playlistSearchInput.getText().toString().trim().matches("^\\d+$") &&
//                                            Integer.parseInt(playlistSearchInput.getText().toString().trim()) <= playlistView.getCount()) {
//                                                playlistAdapter.setSearchPosition(Integer.parseInt(playlistSearchInput.getText().toString().trim()) - 1);
//                                                playlistView.setSelection(Integer.parseInt(playlistSearchInput.getText().toString().trim()) - 1);
//                                        }
//                                        else if (playlistView.getCount() > 1) {
//                                            int find = (int)playlistAdapter.refreshPlaylist().find(playlistAdapter.getSearchPosition() + 1, playlistSearchInput.getText().toString().trim());
//                                            if (find != -1) {
//                                                playlistAdapter.setSearchPosition(find);
//                                                playlistView.setSelection(find);
//                                            } else {
//                                                MainActivity.showInfoMessage(context.getResources().getString(R.string.playlist_search_no_match));
//                                            }
//                                        }
                                    } else {
                                            MainActivity.showInfoMessage(context.getResources().getString(R.string.playlist_search_empty));
                                        }

                            } catch (RuntimeException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    playlistSearchButton.setOnLongClickListener(new ImageButton.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
//                            if (playlistView.getCount() > 0) {
//                                playlistView.setSelection(0);
//                                MainActivity.showInfoMessage(context.getResources().getString(R.string.playlist_search_first_position));
//                            }

                            return true;
                        }
                    });

//                    playlistView.addHeaderView(playlistHeader);
                    playlistView.setAdapter(playlistAdapter);
                    registerForContextMenu(playlistView);
                    changePlaylistBackground();

                    break;
                }
                // Second page with explorer
                case 1 : {
                    view = inflater.inflate(R.layout.fragment_explorer, null);

                    // Button back
                    // If root - do nothing
                    explorerButtonBack = (ImageButton) view.findViewById(R.id.explorer_back);
                    explorerButtonBack.setVisibility(ImageButton.INVISIBLE);

                    // Set action for back to root and invisible
                    explorerButtonBack.setOnClickListener(new ImageButton.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (!folderlistAdapter.isFolder()) {
                                    // Change list's adapter
                                    explorerListView.setAdapter(folderlistAdapter);

                                    // Change header name
                                    TextView textView = (TextView) explorerHeader.findViewById(R.id.explorer_list_header);
                                    textView.setText(R.string.explorer_list_header);
                                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.f);

                                    // Repaint view
                                    explorerHeader.invalidate();
                                    explorerButtonBack.setVisibility(ImageButton.INVISIBLE);

                                    // Set current position
                                    explorerListView.setSelection(folderlistAdapter.getCurrentFolderPosition());

                                    // Now we're in root
                                    folderlistAdapter.setIsFolder(true);
                                }
                            } catch(RuntimeException e){
                                    e.printStackTrace();
                                }
                        }
                    });

                    // Button add
                    // If checked - send in playlist
                    explorerButtonAdd = (ImageButton) view.findViewById(R.id.explorer_add);
                    explorerButtonAdd.setOnClickListener(new ImageButton.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                // If playlist not found - create temp playlist
                                IPlaylist playlist = mPlayerConfig.getPlaylist();
                                if (playlist.getPlaylistId() == -1) {
                                    long idPlaylist = CursorTool.getPlaylistIdByName(context.getContentResolver(), context.getResources().getString(R.string.temp_playlist));
                                    if(idPlaylist == -1)
                                        idPlaylist = CursorTool.createPlaylist(context.getContentResolver(), context.getResources().getString(R.string.temp_playlist));

                                    ((CursorPlaylist) playlist).setCursor(idPlaylist, CursorTool.SORT_NONE);
                                    getPlaylistAdapter().refreshPlaylist();
                                    getTextPlaylistHeader().setText(context.getResources().getString(R.string.temp_playlist));
                                    PlayerControl.setCountTracks();
                                    MainActivity.showInfoMessage(context.getResources().getString(R.string.playlist_get_no_choice));
                                }

                                int totalAddTrack = 0;
                                ArrayList<Long> listSonsAdd = new ArrayList<Long>();

                                // If this is folder, else - file
                                if (folderlistAdapter.isFolder()) {
                                    // Folders container
                                    for (LinkedHashMap.Entry<String, FoldersArrayList<FileDataItem>> item : folderlistAdapter.getMediaTree().entrySet()) {
                                        if (item.getValue().isChecked()) {
                                            for (FileDataItem curFile : item.getValue()) {
                                                listSonsAdd.add(curFile.getId());
                                            }
                                            item.getValue().setChecked(false);
                                        }
                                    }

                                    // Refresh folders adapter
                                    folderlistAdapter.notifyDataSetChanged();

                                } else {
                                    // Files container
                                    for (FileDataItem item : folderlistAdapter.getCurrentFoldersArrayList()) {
                                        if (item.isChecked()) {
                                            item.setIsChecked(false);
                                            listSonsAdd.add(item.getId());
                                        }
                                    }
                                    // Refresh files adapter
                                    folderlistAdapter.getCurrentMediaFilesAdapter().notifyDataSetChanged();
                                }

                                if (listSonsAdd.size() > 0)
                                    totalAddTrack = CursorTool.addToPlaylist(context.getContentResolver(), mPlayerConfig.getPlaylistId(), listSonsAdd);

                                if (playlistAdapter != null)
                                    playlistAdapter.refreshPlaylist();

                                // Update textview with count tracks
                                PlayerControl.setCountTracks();

                                if (totalAddTrack != 0){
                                    playlistView.setBackgroundResource(R.drawable.drawable_playlist_background);
                                    MainActivity.showInfoMessage(context.getResources().getString(R.string.explorer_add_to_playlist) + " " + totalAddTrack);
                                } else
                                    MainActivity.showInfoMessage(context.getResources().getString(R.string.explorer_add_to_playlist_nothing));

                            } catch (RuntimeException e){
                                    e.printStackTrace();
                                }
                        }
                    });

                    explorerButtonAdd.setOnLongClickListener(new ImageButton.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            try {
                                clearCheck();
                                MainActivity.showInfoMessage(context.getResources().getString(R.string.explorer_clear_check));
                            } catch (RuntimeException e){
                                    e.printStackTrace();
                                }

                            return true;
                        }
                    });

                    // Explorer's list
                    explorerListView = (ListView) view.findViewById(R.id.explorer_list_fragment);
                    folderlistAdapter = new MediaFoldersAdapter(context, explorerListView, explorerButtonBack);
                    explorerHeader = folderlistAdapter.getListHeader();
                    textExplorerHeader = (TextView)explorerHeader.findViewById(R.id.explorer_list_header);
                    explorerListView.addHeaderView(explorerHeader);
                    explorerListView.setAdapter(folderlistAdapter);
                    explorerListView.setOnItemClickListener(folderlistAdapter);
                    registerForContextMenu(explorerListView);
                    changeExplorerBackground();

                    break;
                }
            }

            return view;
        }

        private void clearCheck() throws RuntimeException{
            if (folderlistAdapter.isFolder()) {
                for (LinkedHashMap.Entry<String, FoldersArrayList<FileDataItem>> folder : folderlistAdapter.getMediaTree().entrySet()) {
                    folder.getValue().setChecked(false);
                }
                folderlistAdapter.notifyDataSetInvalidated();
            } else {
                for (FileDataItem folder : folderlistAdapter.getCurrentFoldersArrayList()) {
                    folder.setIsChecked(false);
                }
                folderlistAdapter.getCurrentMediaFilesAdapter().notifyDataSetInvalidated();
            }

            if(explorerListView != null){
                explorerListView.setSelection(0);
            }
        }

        @Override
        public void onResume(){
            super.onResume();
            //PlayerControl.setPlayPauseImage(PlayerApplication.getPlayerApplication().isPlay());
            PlayerControl.setCountTracks();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            switch(v.getId()){
                case R.id.explorer_list_fragment:
                    if(folderlistAdapter.isFolder()){
                        getActivity().getMenuInflater().inflate(R.menu.explorer_popup_menu_folders, menu);
                    } else {
                        getActivity().getMenuInflater().inflate(R.menu.explorer_popup_menu_files, menu);
                    }
                    break;

                case R.id.playlist_list_fragment:
                    playlistSelectPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position - 1;
                    getActivity().getMenuInflater().inflate(R.menu.playlist_popup_menu, menu);
                    break;
            }
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            try {
                switch (item.getItemId()) {
                    //Folders menu
                    case R.id.sortByFolders:
                        clearCheck();
                        folderlistAdapter.setSortByFolders();
                        textExplorerHeader.setText(context.getResources().getString(R.string.explorer_sort_by_folders));
                        return true;
                    case R.id.sortByAlbums:
                        clearCheck();
                        folderlistAdapter.setSortByAlbums();
                        textExplorerHeader.setText(context.getResources().getString(R.string.explorer_sort_by_albums));
                        return true;
                    case R.id.sortByArtists:
                        clearCheck();
                        folderlistAdapter.setSortByArtists();
                        textExplorerHeader.setText(context.getResources().getString(R.string.explorer_sort_by_artists));
                        return true;
                    case R.id.sortByAZ:
                        folderlistAdapter.setSortByAZ();
                        return true;
                    case R.id.sortBySize:
                        folderlistAdapter.setSortBySize();
                        return true;
                    case R.id.updateMusicExplorer:
                        folderlistAdapter.updateAllMusic();
                        return true;

                    //Files menu
                    case R.id.sortByFiles:
                        folderlistAdapter.getCurrentMediaFilesAdapter().setSortByName();
                        return true;
                    case R.id.sortByFileDuration:
                        folderlistAdapter.getCurrentMediaFilesAdapter().setSortByLength();
                        return true;

                    // Playlist menu
                    case R.id.sortByTracks:
                        playlistAdapter.sortByName();
                        setPlaylistPosition();
                        return true;
                    case R.id.sortByTrackDuration:
                        playlistAdapter.sortByDuration();
                        setPlaylistPosition();
                        return true;
                    case R.id.sortByTrackModify:
                        playlistAdapter.sortByModify();
                        setPlaylistPosition();
                        return true;
                    case R.id.deleteTrack:
                        playlistAdapter.deleteTrack(playlistSelectPosition);
                        return true;
                }

            } catch (RuntimeException e){
                    e.printStackTrace();
                }

            return super.onContextItemSelected(item);
        }
    }


    public static PlaylistAdapter getPlaylistAdapter() {
        return playlistAdapter;
    }

    public static void setPlaylistPosition() throws RuntimeException{
        IPlaylist playlist = PlayerApplication.getPlayerApplication().getPlayerConfig().getPlaylist();
        if(playlistView != null && playlist != null && playlist.position() != -1){
            playlistAdapter.notifyDataSetChanged();
//            playlistView.setSelection((int)playlist.position());
        }
    }

    public static TextView getTextPlaylistHeader() {
        return textPlaylistHeader;
    }

    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    /*
    * Adapter and actions for pages
    * */
    public class AdapterForPages
            extends FragmentPagerAdapter {

        public AdapterForPages(FragmentManager mFragmentManager) {
            super(mFragmentManager);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0 : return context.getResources().getString(R.string.pagePlaylist);
                case 1 : return context.getResources().getString(R.string.pageExplorer);
            }

            return "Not_named_№" + position;
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }
}



