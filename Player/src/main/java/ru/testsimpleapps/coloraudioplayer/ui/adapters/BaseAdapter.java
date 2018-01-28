package ru.testsimpleapps.coloraudioplayer.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public abstract class BaseAdapter extends RecyclerView.Adapter {

    protected static final int TYPE_HEADER = 0;
    protected static final int TYPE_ITEM = 1;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public interface OnItemCheckListener {
        void onItemCheck(View view, int position);
    }

    protected OnItemClickListener mOnItemClickListener;
    protected OnItemLongClickListener mOnItemLongClickListener;
    protected OnItemCheckListener mOnItemCheckListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    public void setOnItemCheckListener(OnItemCheckListener listener) {
        mOnItemCheckListener = listener;
    }

}