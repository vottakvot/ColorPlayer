package ru.testsimpleapps.coloraudioplayer.managers.tools;


import java.util.Map;
import java.util.Set;

public class CollectionTool {

    public static <A, B> Map.Entry<A, B> getMapEntryByPosition(final Map<A, B> map, final int position) {
        if (map != null && 0 <= position && position < map.size()) {
            int i = 0;
            for (Map.Entry<A, B> item : map.entrySet()) {
                if (i++ == position)
                    return item;
            }
        }

        return null;
    }

    public static <A> A getSetValueByPosition(final Set<A> set, final int position) {
        if (set != null && 0 <= position && position < set.size()) {
            int i = 0;
            for (A item : set) {
                if (i++ == position)
                    return item;
            }
        }

        return null;
    }

}
