package com.wmlive.hhvideo.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by vhawk on 2017/5/24.
 */

public class CollectionUtil {

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

}
