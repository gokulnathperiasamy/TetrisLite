package com.kpgn.tetrislite.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;

import com.kpgn.tetrislite.R;
import com.kpgn.tetrislite.utility.DialogHelper;
import com.kpgn.tetrislite.utility.PreferenceUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.b_start_game)
    public void ctaStartGame(View view) {
        startActivity(new Intent(this, GameActivity.class));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_about)
    public void ctaAbout(View view) {
        DialogHelper.showAboutDialog(this);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_high_score)
    public void ctaHighScore(View view) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setPositiveButton(R.string.dismiss,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                .create();
        alertDialog.setTitle(getString(R.string.high_score));
        alertDialog.setMessage("\nScore: " + PreferenceUtil.getHighScore(this) +
                "\n\nLevel: " + PreferenceUtil.getMaximumLevel(this));
        alertDialog.show();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_rate_us)
    public void ctaRateUs(View view) {
        DialogHelper.launchPlayStore(this);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.iv_share)
    public void ctaShare(View view) {
        DialogHelper.share(this);
    }

}
