package com.dhbikoff.breakout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GameSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private static final String PREFS = "PREFS";
    private static final String ITEM_HIGHSCORE = "ITEM_HIGHSCORE";
    private static final String ITEM_SOUNDONOFF = "ITEM_SOUNDONOFF";
    private static final String ITEM_NEWGAME = "ITEM_NEWGAME";
    private static boolean fst = true;
    private Paint getReadyPaint = null;
    private Paint scorePaint = null;
    private Paint turnsPaint = null;

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
//        blocksList = new ArrayList<Block>();

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
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.BLACK);
        canvas.drawCircle(100, 200, 50, paint);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
    }

    @Override
    public void run() {

    }

}
