package com.mihalis.dtr00.utils;

import java.util.Arrays;

public class ArrayFiller {
    public static void fill(String[] strings, StringAction action) {
        for (int i = 0; i < strings.length; i++) {
            strings[i] = action.get(i);
        }
    }

    public static void fill(boolean[][] booleans, boolean value) {
        for (boolean[] elem : booleans) {
            Arrays.fill(elem, value);
        }
    }

    public interface StringAction {
        String get(int index);
    }
}
