package com.jinivus.yMine.tools;

import java.util.Arrays;

/**
 * Created by Matt on 1/8/2015.
 */
public class Util {

    public static boolean contains(int[] array, int key) {
        return Arrays.toString(array).matches(".*[\\[ ]" + key + "[\\],].*");
    }

    public static int perHour(int value,long startTime) {
        return (int) ((value) * 3600000D / (System.currentTimeMillis() - startTime));
    }
}
