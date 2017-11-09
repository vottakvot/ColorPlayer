package ru.testsimpleapps.coloraudioplayer.managers.tools;


import java.util.Map;
import java.util.Set;

public class CollectionTool {

    public static <A extends Object, B extends Object> A getMapPosition(Map<A, B> map, int position) {
        if (0 <= position && map != null && position < map.size()) {
            int i = 0;
            for (Map.Entry<A, B> item : map.entrySet()) {
                if (i++ == position)
                    return item.getKey();
            }
        }

        return null;
    }

    public static <A extends Object> A getSetPosition(Set<A> set, int position) {
        if (0 <= position && set != null && position < set.size()) {
            int i = 0;
            for (A item : set) {
                if (i++ == position)
                    return item;
            }
        }

        return null;
    }

}
