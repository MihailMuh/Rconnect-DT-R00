package com.mihalis.dtr00.utils;

import java.util.Arrays;

public class CollectionsManipulator {
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

    public static String getLongestString(String... strings) {
        int indexOfLongestStr = 0;
        int maxLen = -1;

        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];

            if (string.length() > maxLen) {
                maxLen = string.length();
                indexOfLongestStr = i;
            }
        }

        return strings[indexOfLongestStr];
    }

    public interface StringAction {
        String get(int index);
    }
}
