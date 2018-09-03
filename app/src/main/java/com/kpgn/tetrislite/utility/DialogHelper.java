package com.kpgn.tetrislite.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.kpgn.tetrislite.BuildConfig;
import com.kpgn.tetrislite.R;

public abstract class DialogHelper {

    public static void showAboutDialog(final Context context) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setPositiveButton(R.string.rate_us,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                launchPlayStore(context);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.share,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                share(context);
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.setTitle(context.getString(R.string.about_us));
        alertDialog.setMessage(context.getString(R.string.copyright_message));
        alertDialog.show();
    }

    public static void launchPlayStore(Context context) {
        Uri uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void share(Context context) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, context.getString(R.string.share_message) + " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "Share via..."));
    }
}