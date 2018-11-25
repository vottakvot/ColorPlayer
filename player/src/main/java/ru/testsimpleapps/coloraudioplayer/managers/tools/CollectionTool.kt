package ru.testsimpleapps.coloraudioplayer.managers.tools

object CollectionTool {

    fun <A, B> getMapEntryByPosition(map: Map<A, B>?, position: Int): kotlin.collections.Map.Entry<A, B>? {
        if (map != null && 0 <= position && position < map.size) {
            var i = 0
            for (item in map.entries) {
                if (i++ == position)
                    return item
            }
        }

        return null
    }

    fun <A> getSetValueByPosition(set: Set<A>?, position: Int): A? {
        if (set != null && 0 <= position && position < set.size) {
            var i = 0
            for (item in set) {
                if (i++ == position)
                    return item
            }
        }

        return null
    }

}
