package ru.testsimpleapps.coloraudioplayer.managers.player.data

import java.util.HashSet
import java.util.Random

class RandomSet() {
    private var mMaxSize = DEFAULT_SIZE
    private val mSet: MutableSet<Int>

    var size: Int
        get() = mSet.size
        set(maxSize) {
            mMaxSize = if (maxSize > 0) maxSize else mMaxSize
            mSet.clear()
        }

    // Check size
    // Get random integer
    // for safety
    // this return - must never work ;)
    val nextRandom: Int?
        get() {
            if (mSet.size >= mMaxSize) {
                mSet.clear()
            }
            var i = 0
            do {
                val position = Random().nextInt(mMaxSize)
                if (!mSet.contains(position)) {
                    mSet.add(position)
                    return position
                }
            } while (i++ < mSet.size * 10)

            return null
        }

    init {
        mMaxSize = DEFAULT_SIZE
        mSet = HashSet()
    }

    constructor(maxSize: Int) : this() {
        mMaxSize = if (maxSize > 0) maxSize else DEFAULT_SIZE
    }

    companion object {

        private val DEFAULT_SIZE = 64
    }
}
