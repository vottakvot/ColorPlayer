package ru.testsimpleapps.coloraudioplayer.managers.player.data;

import java.util.HashSet;
import java.util.Random;

public final class RandomSet extends HashSet<Integer> {

    private final static int DEFAULT_SIZE = 64;
    private int maxSize = DEFAULT_SIZE;

    public RandomSet() {
        this.maxSize = DEFAULT_SIZE;
    }

    public RandomSet(int maxSize) {
        this.maxSize = maxSize > 0 ? maxSize : DEFAULT_SIZE;
    }

    public void setSize(int maxSize) {
        this.maxSize = maxSize > 0 ? maxSize : this.maxSize;
        clear();
    }

    public Integer getNextRandom() {
        // check size
        if (size() >= maxSize) {
            clear();
        }

        // get random integer
        int i = 0;
        do {
            Integer position = new Random().nextInt(maxSize);
            if (!contains(position)) {
                add(position);
                return position;
            }
        } while (i++ < size() * 10); // for safety

        return null; // this return - must never work ;)
    }
}
