package com.fruitsandwich.zincer.util;

import com.google.common.base.Function;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by nakac on 15/07/24.
 */
public class Utils {
    public static <E> E find(Function<E, Boolean> f, List<E> es) {
        for (E e : es) {
            Boolean b = f.apply(e);
            if (b != null && b) return e;
        }
        return null;
    }
}
