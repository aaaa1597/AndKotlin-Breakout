package com.dhbikoff.breakout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
    private Paint mGetReadyPaint = null;
    private Paint scorePaint = null;
    private Paint mLifePaint = null;
    private boolean isRunning = false;
    private Thread gameThread = null;
    private final int FRAMERATE = 33;
    private SurfaceHolder mHolder = null;
    private Size mMaxSize = null;
    private boolean isGameOver = false;
    private int mScore = 0;
    private int mLife = 0;
    private int mDrwCount = 0;
    private static int READY_STAGE = 66;
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
        scorePaint.setTextSize(60);

        mLifePaint = new Paint();
        mLifePaint.setTextAlign(Paint.Align.RIGHT);
        mLifePaint.setColor(Color.WHITE);
        mLifePaint.setTextSize(60);

        mGetReadyPaint = new Paint();
        mGetReadyPaint.setTextAlign(Paint.Align.CENTER);
        mGetReadyPaint.setColor(Color.WHITE);
        mGetReadyPaint.setTextSize(72);
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

            mDrwCount++;
            if(mDrwCount < READY_STAGE) {
                if(isGameOver) {
                    mGetReadyPaint.setColor(Color.RED);
                    canvas.drawText("GAME OVER!!!", (float)canvas.getWidth()/2,(float)(canvas.getHeight()/2)-50, mGetReadyPaint);
                }
                mGetReadyPaint.setColor(Color.WHITE);
                canvas.drawText("GET READY...",(float)canvas.getWidth()/2, (float)(canvas.getHeight()/2) , mGetReadyPaint);
            }
            else if(mDrwCount == READY_STAGE) {
                mGetReadyPaint.setColor(Color.WHITE);
                canvas.drawText("GET READY...",(float)canvas.getWidth()/2, (float)(canvas.getHeight()/2) , mGetReadyPaint);
                initObjects(canvas);
            }
            else {
                /* ブロック描画 */
                for(Block item : blocksList)
                    item.drawBlock(canvas);
            }

            canvas.drawText("SCORE = "+ mScore, 0, 50, scorePaint);
            canvas.drawText("Life: " + mLife, canvas.getWidth(), 50, mLifePaint);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void initObjects(Canvas canvas) {
//        ball.initCoords(canvas.getWidth(), canvas.getHeight());
//        paddle.initCoords(canvas.getWidth(), canvas.getHeight());
        initBlocks(canvas);
    }

    private void initBlocks(Canvas canvas) {
        int blockHeight = canvas.getWidth() / 36;
        int spacing = canvas.getWidth() / 144;
        int topOffset = canvas.getHeight() / 10;
        int blockWidth = (canvas.getWidth() / 10) - spacing;

        for (int idxy = 0; idxy < 10; idxy++) {
            for (int idxx = 0; idxx < 10; idxx++) {
                int y_coordinate = (idxy * (blockHeight + spacing)) + topOffset;
                int x_coordinate = idxx * (blockWidth + spacing);

                Rect r = new Rect();
                r.set(x_coordinate, y_coordinate, x_coordinate + blockWidth, y_coordinate + blockHeight);

                if(idxy < 2)      blocksList.add(new Block(r, Color.RED));
                else if(idxy < 4) blocksList.add(new Block(r, Color.YELLOW));
                else if(idxy < 6) blocksList.add(new Block(r, Color.GREEN));
                else if(idxy < 8) blocksList.add(new Block(r, Color.MAGENTA));
                else              blocksList.add(new Block(r, Color.LTGRAY));
            }
        }
    }
}
