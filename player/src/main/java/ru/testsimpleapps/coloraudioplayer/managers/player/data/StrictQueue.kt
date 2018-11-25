package ru.testsimpleapps.coloraudioplayer.managers.player.data

import java.util.LinkedList

class StrictQueue<T> {
    private val mQueue: LinkedList<T>
    private var mMaxSize = DEFAULT_SIZE

    constructor() {
        mQueue = LinkedList()
    }

    constructor(maxSize: Int) {
        mMaxSize = maxSize
        mQueue = LinkedList()
    }

    fun push(value: T?) {
        if (mQueue.size >= mMaxSize) {
            mQueue.removeFirst()
        }

        mQueue.addLast(value)
    }

    fun pop(): T? {
        return mQueue.pollLast()
    }

    companion object {

        private val DEFAULT_SIZE = 64
    }

}
