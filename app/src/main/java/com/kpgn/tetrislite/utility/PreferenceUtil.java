package com.kpgn.tetrislite.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kpgn.tetrislite.application.ApplicationConstant;

public class PreferenceUtil {

    private static SharedPreferences sharedPreferences;

    private static void initSharedPreference(Context context) {
        if (sharedPreferences != null) {
            return;
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setHighScore(Context context, long score) {
        initSharedPreference(context);
        if (score > getHighScore(context)) {
            sharedPreferences.edit().putLong(ApplicationConstant.SP_HIGH_SCORE, score).apply();
        }
    }

    public static long getHighScore(Context context) {
        initSharedPreference(context);
        return sharedPreferences.getLong(ApplicationConstant.SP_HIGH_SCORE, 0);
    }

    public static void setMaximumLevel(Context context, int cards) {
        initSharedPreference(context);
        if (cards > getMaximumLevel(context)) {
            sharedPreferences.edit().putInt(ApplicationConstant.SP_MAXIMUM_LEVEL, cards).apply();
        }
    }

    public static int getMaximumLevel(Context context) {
        initSharedPreference(context);
        return sharedPreferences.getInt(ApplicationConstant.SP_MAXIMUM_LEVEL, 0);
    }

}