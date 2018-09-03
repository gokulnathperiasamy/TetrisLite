package com.kpgn.tetrislite.utility;

import java.util.Locale;

public abstract class TextUtil {

    public static String getFormattedScore(long doubleValue) {
        return String.format(Locale.US, "%06d", doubleValue);
    }

    public static String getFormattedLevel(long doubleValue) {
        return String.format(Locale.US, "%02d", doubleValue);
    }
}