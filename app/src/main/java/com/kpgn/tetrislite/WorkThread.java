package com.kpgn.tetrislite;

import com.kpgn.tetrislite.activity.GameActivity;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class WorkThread extends Thread {

    private SurfaceHolder surfaceHolder;
    private boolean runFlag = false;
    private boolean firstTime = true;
    private GameActivity host;

    public WorkThread(GameActivity ga, SurfaceHolder sh) {
        host = ga;
        this.surfaceHolder = sh;
    }

    public void setRunning(boolean run) {
        this.runFlag = run;
    }

    @Override
    public void run() {
        Canvas c;
        long tempTime = System.currentTimeMillis();

        long fpsUpdateTime = tempTime + 200;
        int frameCounter[] = {0, 0, 0, 0, 0};
        int i = 0;

        while (this.runFlag) {
            if (firstTime) {
                firstTime = false;
                continue;
            }

            tempTime = System.currentTimeMillis();
            if (tempTime >= fpsUpdateTime) {
                i = (i + 1) % 5;
                fpsUpdateTime += 200;
                frameCounter[i] = 0;
            }
            frameCounter[i]++;

            if (host.game.cycle(tempTime))
                host.controls.cycle(tempTime);

            host.game.getBoard().cycle(tempTime);

            c = null;
            try {
                c = this.surfaceHolder.lockCanvas(null);
                synchronized (this.surfaceHolder) {
                    host.display.doDraw(c);
                }
            } finally {
                if (c != null) {
                    this.surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }

    public void setFirstTime(boolean b) {
        firstTime = b;
    }

}