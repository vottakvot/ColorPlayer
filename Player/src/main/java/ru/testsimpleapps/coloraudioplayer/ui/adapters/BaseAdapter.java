package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<D> extends RecyclerView.Adapter {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    protected List<D> mList;
    protected OnItemClickListener mOnItemClickListener;

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void addItems(final List<D> list) {
        if (mList == null) {
            mList = new ArrayList<>();
        }

        if (list != null) {
            final int positionStart = mList.size();
            mList.addAll(list);
            notifyDataSetChanged();
            notifyItemRangeInserted(positionStart, list.size());
        }
    }

    public void addItemsAtTop(final List<D> list) {
        if (mList == null) {
            mList = new ArrayList<>();
        }

        if (list != null) {
            mList.addAll(0, list);
            notifyItemRangeInserted(0, list.size());
        }
    }

    public void setItems(final List<D> list) {
        if (mList != null) {
            mList.clear();
        } else {
            mList = new ArrayList<>();
        }
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(final D item) {
        if (mList == null) {
            mList = new ArrayList<>();
        }

        mList.add(item);
        notifyItemInserted(mList.size());
    }

    public void addItemAtTop(final D item) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(0, item);
        notifyItemInserted(0);
    }

    public List<D> getItemList() {
        return mList;
    }

    public D getItem(int index) {
        return mList != null && index < mList.size() ? mList.get(index) : null;
    }

    public void clear() {
        if (mList != null) {
            mList.clear();
            notifyItemRangeRemoved(0, mList.size());
        }
    }

    public void removeItem(D item) {
        if (mList != null && !mList.isEmpty() && mList.contains(item)) {
            mList.remove(item);
            notifyDataSetChanged();
        }
    }

    public void updateItem(D item) {
        if (mList != null && !mList.isEmpty() && mList.contains(item)) {
            int position = mList.indexOf(item);
            mList.set(position, item);
            notifyDataSetChanged();
        }
    }
}