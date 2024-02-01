package org.labyrinth.common;

import java.util.List;

public class ListUtils {

    public static <T> boolean isValidIndex(final List<T> ts, final int index) {
        return 0 <= index && index < ts.size();
    }
}
