package ru.testsimpleapps.coloraudioplayer.managers.player.data;

import android.support.annotation.Nullable;

import java.util.LinkedList;

public final class StrictQueue<T> {

    private final static int DEFAULT_SIZE = 64;
    private final LinkedList<T> mQueue;
    private int mMaxSize = DEFAULT_SIZE;

    public StrictQueue() {
        mQueue = new LinkedList<>();
    }

    public StrictQueue(int maxSize) {
        mMaxSize = maxSize;
        mQueue = new LinkedList<>();
    }

    public void push(@Nullable T value) {
        if (mQueue.size() >= mMaxSize) {
            mQueue.removeFirst();
        }

        mQueue.addLast(value);
    }

    @Nullable
    public T pop() {
        return mQueue.pollLast();
    }

}
