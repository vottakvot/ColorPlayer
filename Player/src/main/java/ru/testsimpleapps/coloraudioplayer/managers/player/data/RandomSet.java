package ru.testsimpleapps.coloraudioplayer.managers.player.data;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class RandomSet {

    private final static int DEFAULT_SIZE = 64;
    private int mMaxSize = DEFAULT_SIZE;
    private final Set<Integer> mSet;

    public RandomSet() {
        mMaxSize = DEFAULT_SIZE;
        mSet = new HashSet<>();
    }

    public RandomSet(int maxSize) {
        this();
        mMaxSize = maxSize > 0 ? maxSize : DEFAULT_SIZE;
    }

    public void setSize(int maxSize) {
        mMaxSize = maxSize > 0 ? maxSize : mMaxSize;
        mSet.clear();
    }

    public Integer getNextRandom() {
        // Check size
        if (mSet.size() >= mMaxSize) {
            mSet.clear();
        }

        // Get random integer
        int i = 0;
        do {
            Integer position = new Random().nextInt(mMaxSize);
            if (!mSet.contains(position)) {
                mSet.add(position);
                return position;
            }
        } while (i++ < mSet.size() * 10); // for safety

        return null; // this return - must never work ;)
    }
}
