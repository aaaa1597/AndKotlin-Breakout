package com.dhbikoff.breakout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GameSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private static final String PREFS = "PREFS";
    private static final String ITEM_HIGHSCORE = "ITEM_HIGHSCORE";
    private static final String ITEM_SOUNDONOFF = "ITEM_SOUNDONOFF";
    private static final String ITEM_NEWGAME = "ITEM_NEWGAME";
    private boolean isRunning = false;
    private Thread gameThread = null;
    private final int FRAMERATE = 33;
    private SurfaceHolder mHolder = null;
    private boolean isGameOver = false;
    private float eventX = 0f;

    /* Viewを継承したときのお約束 */
    public GameSurfaceView(@NonNull Context context, @Nullable AttributeSet attrs) { this(context, attrs, 0); }
    public GameSurfaceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        /* ↓kotlinの時はこれがないと描画が見えなかった。 */
//      getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mHolder = holder;
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        isRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.d("aaaaa", "error2!!", e);
        }
        gameThread = null;
    }

    @Override
    public void run() {
        int mDrwCount = 0;

        while(isRunning) {
            try {
                Thread.sleep(FRAMERATE);
            } catch (InterruptedException e) {
                Log.e("aaaaa", "error!!", e);
            }

            Canvas canvas = mHolder.lockCanvas();
            if(canvas == null) continue;
            /* 一旦全消去 */
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            mDrwCount++;
            Stage drawstage = DrawStageFactory.getDrawStage(mDrwCount, canvas.getWidth(), canvas.getHeight());
            drawstage.update();
            drawstage.draw(canvas);

            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            eventX = event.getX();
            performClick();
            return true;
        }
        return false;
    }
}
