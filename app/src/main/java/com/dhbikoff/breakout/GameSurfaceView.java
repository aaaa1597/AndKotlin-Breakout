package com.dhbikoff.breakout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GameSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private static final String PREFS = "PREFS";
    private static final String ITEM_HIGHSCORE = "ITEM_HIGHSCORE";
    private static final String ITEM_SOUNDONOFF = "ITEM_SOUNDONOFF";
    private static final String ITEM_NEWGAME = "ITEM_NEWGAME";
    private static boolean fst = true;
    private Paint getReadyPaint = null;
    private Paint scorePaint = null;
    private Paint turnsPaint = null;
    private boolean isRunning = false;
    private Thread gameThread = null;
    private final int FRAMERATE = 33;
    private SurfaceHolder mHolder = null;
    private Size mMaxSize = null;
//    private Ball ball;
//    private Paddle paddle;
    private ArrayList<Block> blocksList;

    /* Viewを継承したときのお約束 */
    public GameSurfaceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public GameSurfaceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        if(!fst) return;
        fst = false;

//        ball = new Ball(this.getContext(), soundToggle);
//        paddle = new Paddle();
        blocksList = new ArrayList<Block>();

        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(25);

        turnsPaint = new Paint();
        turnsPaint.setTextAlign(Paint.Align.RIGHT);
        turnsPaint.setColor(Color.WHITE);
        turnsPaint.setTextSize(25);

        getReadyPaint = new Paint();
        getReadyPaint.setTextAlign(Paint.Align.CENTER);
        getReadyPaint.setColor(Color.WHITE);
        getReadyPaint.setTextSize(45);
    }

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
        mMaxSize = new Size(width,height);
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

    private class Pos {
        public int x;
        public int y;
        public Pos(int xpos, int ypos) {
            x = xpos;
            y = ypos;
        }
    }
    private Pos mCurPos = new Pos(0,0);

    @Override
    public void run() {
        while(isRunning) {
            try {
                Thread.sleep(FRAMERATE);
            } catch (InterruptedException e) {
                Log.e("aaaaa", "error!!", e);
            }

            /* お試し描画 */
            mCurPos.x++;
            if(mCurPos.x > mMaxSize.getWidth())
                mCurPos.x = 0;
            mCurPos.y++;
            if(mCurPos.y > mMaxSize.getHeight())
                mCurPos.y = 0;

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);

            Canvas canvas = mHolder.lockCanvas();
            if(canvas == null) continue;
            canvas.drawColor(Color.BLACK);
            canvas.drawCircle(mCurPos.x, mCurPos.y, 50, paint);
            /* お試し描画 */

            

            mHolder.unlockCanvasAndPost(canvas);
        }
    }
}
