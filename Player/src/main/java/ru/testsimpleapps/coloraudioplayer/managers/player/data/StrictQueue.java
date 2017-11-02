package ru.testsimpleapps.coloraudioplayer.managers.player.data;

import android.support.annotation.Nullable;

import java.util.LinkedList;

public final class StrictQueue<T> extends LinkedList<T> {

    private final static int DEFAULT_SIZE = 64;
    private int maxSize = DEFAULT_SIZE;

    public StrictQueue() {
        this.maxSize = DEFAULT_SIZE;
    }

    public StrictQueue(int maxSize) {
        this.maxSize = maxSize > 0 ? maxSize : DEFAULT_SIZE;
    }

    public void push(@Nullable T value) {
        if (size() >= maxSize)
            removeLast();
        addFirst(value);
    }

    @Nullable
    public T pop() {
        T value = null;
        if (size() > 0) {
            value = getFirst();
            removeFirst();
        }

        return value;
    }

}
