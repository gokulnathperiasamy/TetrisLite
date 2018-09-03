package com.kpgn.tetrislite.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.kpgn.tetrislite.BlockBoardView;
import com.kpgn.tetrislite.R;
import com.kpgn.tetrislite.WorkThread;
import com.kpgn.tetrislite.components.Controls;
import com.kpgn.tetrislite.components.Display;
import com.kpgn.tetrislite.components.GameState;
import com.kpgn.tetrislite.utility.PreferenceUtil;
import com.kpgn.tetrislite.utility.TextUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GameActivity extends AppCompatActivity {

    @BindView(R.id.tv_score)
    TextView mScore;

    @BindView(R.id.tv_level)
    TextView mLevel;

    public Controls controls;
    public Display display;
    public GameState game;
    private WorkThread mainThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        initGame();
    }

    private void initGame() {
        game = GameState.getNewInstance(this);
        game.setLevel(1);

        game.reconnect(this);
        controls = new Controls(this);
        display = new Display(this);

        ((BlockBoardView) findViewById(R.id.boardView)).init();
        ((BlockBoardView) findViewById(R.id.boardView)).setHost(this);
    }

    public void startGame(BlockBoardView caller) {
        mainThread = new WorkThread(this, caller.getHolder());
        mainThread.setFirstTime(false);
        game.setRunning(true);
        mainThread.setRunning(true);
        mainThread.start();
    }

    public void destroyWorkThread() {
        boolean retry = true;
        mainThread.setRunning(false);
        while (retry) {
            try {
                mainThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateScore() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mScore.setText(TextUtil.getFormattedScore(game.getScore()));
                mLevel.setText(TextUtil.getFormattedLevel(game.getLevel()));
            }
        });
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return game;
    }

    public void gameOver() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateHighScore();
                showGameOverDialog();
            }
        });
    }

    private void updateHighScore() {
        PreferenceUtil.setHighScore(this, game.getScore());
        PreferenceUtil.setMaximumLevel(this, game.getLevel());
    }

    private void showGameOverDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setPositiveButton(R.string.play_again,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                initGame();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("Home",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.setTitle(getString(R.string.game_over));
        alertDialog.setMessage(this.getString(R.string.game_over_message, game.getScore(), game.getLevel()));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.rightButton)
    public void ctaRightButton(View view) {
        controls.rightButtonPressed();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.leftButton)
    public void ctaLeftButton(View view) {
        controls.leftButtonPressed();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.rotateButton)
    public void ctaRotateButton(View view) {
        controls.rotateRightPressed();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.hardDropButton)
    public void ctaHardDropButton(View view) {
        controls.dropButtonPressed();
    }

}
