package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.data.DrawerItem;


public class DrawerAdapter extends BaseListAdapter<DrawerItem> {

    private final Context mContext;

    public DrawerAdapter(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE_ITEM: {
                final View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.drawer_item, viewGroup, false);
                return new ViewHolderItem(viewItem);
            }

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolderItem) {
            final ViewHolderItem mViewHolder = (ViewHolderItem) viewHolder;
            final DrawerItem drawerItem = getItem(i);
            mViewHolder.mDrawerImage.setImageResource(drawerItem.getImage());
            mViewHolder.mDrawerText.setText(drawerItem.getName());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    protected class ViewHolderItem extends RecyclerView.ViewHolder {

        @BindView(R.id.drawer_item_image)
        ImageView mDrawerImage;
        @BindView(R.id.drawer_item_name)
        TextView mDrawerText;

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
