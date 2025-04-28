package com.aaa.breakout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GameSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private boolean isRunning = false;
    private Thread gameThread = null;
    private SurfaceHolder mHolder = null;
    private float eventX = 0f;
    private boolean mSoundFlg = false;

    /* Viewを継承したときのお約束 */
    public GameSurfaceView(@NonNull Context context, @Nullable AttributeSet attrs) { this(context, attrs, 0); }
    public GameSurfaceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        /* ↓kotlinの時はこれがないと描画が見えなかった。 */
//      getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        /* 描画開始 */
        PhaseManager.init(getContext(), mSoundFlg, width, height);
        mHolder = holder;
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
        /* ジェスチャバックを無効化 */
        List<Rect> rectlist = new ArrayList<>();
        rectlist.add(new Rect(0, height-650, width, height-450));
        rectlist.add(new Rect(0, height-450, width, height-250));
        rectlist.add(new Rect(0, height-250, width, height-50));
        setSystemGestureExclusionRects(rectlist);
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
        while(isRunning) {
            try {
                final int FRAMERATE = 15;
                Thread.sleep(FRAMERATE);
            } catch (InterruptedException e) {
                Log.e("aaaaa", "error!!", e);
            }

            Canvas canvas = mHolder.lockCanvas();
            if(canvas == null) continue;
            /* 一旦全消去 */
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            PhaseBase drawPhase = PhaseManager.getPhase();
            drawPhase.update(eventX);
            drawPhase.draw(canvas);

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

    public void setSoundFlg(boolean soundFlg) {
        mSoundFlg = soundFlg;
    }
}
