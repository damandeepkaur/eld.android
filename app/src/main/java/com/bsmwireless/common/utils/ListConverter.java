package com.bsmwireless.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class ListConverter {
    public static String toString(List<Integer> integers) {
        StringBuilder builder = new StringBuilder();

        if (integers != null && integers.size() > 0) {
            builder.append(integers.get(0));

            for (int i = 1; i < integers.size(); i++) {
                builder.append(",");
                builder.append(integers.get(i));
            }
        }

        return builder.toString();
    }

    public static List<Integer> toIntegerList(String s) {
        List<Integer> integers = new ArrayList<>();

        try {
            if (s != null && !s.isEmpty()) {
                List<String> list = Arrays.asList(s.split("\\s*,\\s*"));

                for (String item : list) {
                    integers.add(Integer.valueOf(item));
                }
            }
        } catch (NumberFormatException e) {
            Timber.e(e);
        }

        return integers;
    }
}
