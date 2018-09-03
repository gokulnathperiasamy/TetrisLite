package com.kpgn.tetrislite.application;

import android.app.Application;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class TetrisLiteApplication extends Application {

    private static final String TAG = TetrisLiteApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        setFixedSize();
    }

    private void setFixedSize() {
        try {
            Configuration configuration = getResources().getConfiguration();
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            configuration.fontScale = (float) 1; // 0.85 small, 1 normal, 1.15 large (increment by .15)!

            if (windowManager != null) {
                windowManager.getDefaultDisplay().getMetrics(metrics);
                metrics.scaledDensity = configuration.fontScale * metrics.density;
                getBaseContext().getResources().updateConfiguration(configuration, metrics);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
}
