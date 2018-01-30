package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.data.DrawerItem;
import ru.testsimpleapps.coloraudioplayer.service.PlayerService;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.BaseAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.adapters.DrawerAdapter;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.EqualizerDialog;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.PlaylistChooserDialog;
import ru.testsimpleapps.coloraudioplayer.ui.dialogs.PlaylistCreateDialog;


public class DrawerFragment extends BaseFragment implements BaseAdapter.OnItemClickListener {

    public static final String TAG = DrawerFragment.class.getSimpleName();

    private MainActivity mMainActivity;
    protected Unbinder mUnbinder;
    @BindView(R.id.drawer_list)
    protected RecyclerView mRecyclerView;
    private DrawerAdapter mDrawerAdapter;
    private final int mDrawerImages[] = new int[] {
            R.drawable.image_playlist_create,
            R.drawable.image_playlist_choose,
            R.drawable.image_equalizer,
            R.drawable.image_timer,
            R.drawable.image_color,
            R.drawable.image_settings,
            R.drawable.image_exit,
    };

    private PlaylistCreateDialog mPlaylistCreateDialog;
    private PlaylistChooserDialog mPlaylistChooserDialog;
    private EqualizerDialog mEqualizerDialog;

    public static DrawerFragment newInstance() {
        DrawerFragment fragment = new DrawerFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mMainActivity = (MainActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getClass().getSimpleName() + " must implement " +
                    MainActivity.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init(savedInstanceState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void init(final Bundle savedInstanceState) {
        final String[] drawers = getResources().getStringArray(R.array.drawer_items);
        final List<DrawerItem> drawerItems = new ArrayList<>();

        // Check sizes of images and strings arrays
        if (mDrawerImages.length != drawers.length) {
            throw new RuntimeException("Size of image array must match with string array!");
        }

        // Fill adapter
        for (int i = 0; i < mDrawerImages.length; i++) {
            drawerItems.add(new DrawerItem(mDrawerImages[i], drawers[i]));
        }

        mDrawerAdapter = new DrawerAdapter(getContext());
        mDrawerAdapter.setItems(drawerItems);
        mDrawerAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mDrawerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mPlaylistCreateDialog = new PlaylistCreateDialog(getContext());
        mPlaylistChooserDialog = new PlaylistChooserDialog(getContext());
        mEqualizerDialog = new EqualizerDialog(getContext());
    }

    @Override
    public void onItemClick(View view, int position) {
        final DrawerItem drawerItem = mDrawerAdapter.getItem(position);
        switch (drawerItem.getImage()) {
            case R.drawable.image_playlist_create:
                mPlaylistCreateDialog.show();
                break;
            case R.drawable.image_playlist_choose:
                mPlaylistChooserDialog.show();
                break;
            case R.drawable.image_equalizer:
                mEqualizerDialog.show();
                break;
            case R.drawable.image_timer:
                break;
            case R.drawable.image_color:
                break;
            case R.drawable.image_settings:
                break;
            case R.drawable.image_exit:
                PlayerService.sendCommandExit();
                getActivity().finish();
                break;
        }

        mMainActivity.closeDrawer();
    }

}
