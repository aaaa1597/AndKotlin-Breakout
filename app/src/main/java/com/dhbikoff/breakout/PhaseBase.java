package com.dhbikoff.breakout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

public interface PhaseBase {
    PhaseBase prepare();
    void update(float eventX);
    void draw(Canvas canvas);
}

/** PhaseManager **/
class PhaseManager {
    static OpeningPhase openingPhase;
    static BuildPhase buildPhase;
    static PlayPhase playPhase;
    static ClosingPhase closingPhase;
    static ClaarPhase clearPhase;
    static PhaseBase currentPhase = new NonePhase();

    public static void init(Context context, boolean soundflg, int screenW, int screenH) {
        openingPhase = new OpeningPhase();
        buildPhase = new BuildPhase(context, soundflg, screenW, screenH);
        playPhase = new PlayPhase();
        closingPhase = new ClosingPhase();
        clearPhase = new ClaarPhase();
        currentPhase = openingPhase;
    }

    public static PhaseBase getPhase() {
        currentPhase = currentPhase.prepare();
        return currentPhase;
    }
}

/** NonePhase(何もしないフェーズ) **/
class NonePhase implements PhaseBase {
    @Override public PhaseBase prepare() { return this; }
    @Override public void update(float eventX) { }
    @Override public void draw(Canvas canvas) { }
}
/** OpeningStage **/
class OpeningPhase implements PhaseBase {
    private final Paint paint = new Paint();
    private static int phaseCounter = 0;
    /* コンストラクタ */
    public OpeningPhase() {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        paint.setTextSize(72);
    }
    /* 準備 */
    @Override public PhaseBase prepare() {
        final int OPENING_PHASE_PERIOD = 66;
        phaseCounter++;
        if(phaseCounter < OPENING_PHASE_PERIOD)
            return this;
        else
            phaseCounter = 0;
        return PhaseManager.buildPhase;
    }
    /* 更新 */
    @Override public void update(float eventX) {/*何もしない*/}
    /* 描画 */
    @Override
    public void draw(Canvas canvas) {
        canvas.drawText("GET READY...",(float)canvas.getWidth()/2, (float)(canvas.getHeight()/2) , paint);
    }
}

/** BuildStage **/
class BuildPhase implements PhaseBase {
    private Context mContext;
    private final boolean mSoundFlg;
    private final int mScreenW;
    private final int mScreenH;
    /* コンストラクタ */
    public BuildPhase(Context context, boolean soundFlg, int screenW, int screenH) {
        mContext = context;
        mSoundFlg= soundFlg;
        mScreenW = screenW;
        mScreenH = screenH;
    }
    /* 準備 */
    @Override
    public PhaseBase prepare() {
        Ball ball = new Ball(mContext, mSoundFlg);
        ball.init(mScreenW, mScreenH);
        Paddle paddle = new Paddle();
        paddle.init(mScreenW, mScreenH);
        ArrayList<Block> blocksList = new ArrayList<>();
        initBlocks(mScreenW, mScreenH, blocksList);
        PhaseManager.playPhase.setParams(ball, paddle, blocksList);
        mContext = null;
        return PhaseManager.playPhase;
    }
    @Override public void update(float eventX) { }
    @Override public void draw(Canvas canvas) {}
    /* 準備2(ブロック初期化) */
    private void initBlocks(int screenW, int screenH, ArrayList<Block> blocksList) {
        int blockHeight=  screenW / 36;
        int spacing    =  screenW / 144;
        int topOffset  =  screenH / 10;
        int blockWidth = (screenW / 10) - spacing;

        for (int idxy = 0; idxy < 10; idxy++) {
            for (int idxx = 0; idxx < 10; idxx++) {
                int y_coordinate = (idxy * (blockHeight + spacing)) + topOffset;
                int x_coordinate = idxx * (blockWidth + spacing);

                Rect rect = new Rect();
                rect.set(x_coordinate, y_coordinate, x_coordinate + blockWidth, y_coordinate + blockHeight);

                if(idxy < 2)      blocksList.add(new Block(rect, Color.RED));
                else if(idxy < 4) blocksList.add(new Block(rect, Color.YELLOW));
                else if(idxy < 6) blocksList.add(new Block(rect, Color.GREEN));
                else if(idxy < 8) blocksList.add(new Block(rect, Color.MAGENTA));
                else              blocksList.add(new Block(rect, Color.LTGRAY));
            }
        }
    }
}

/** PlayStage **/
class PlayPhase implements PhaseBase {
    private Ball ball = null;
    private Paddle paddle = null;
    private ArrayList<Block> blocksList = null;
    private int score = 0;
    private int lifeGauge = 5;
    public final Paint scorePaint;
    public final Paint lifePaint;
    private boolean gameoverFlg = false;

    public PlayPhase() {
        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(60);
        lifePaint = new Paint();
        lifePaint.setTextAlign(Paint.Align.RIGHT);
        lifePaint.setColor(Color.WHITE);
        lifePaint.setTextSize(60);
    }

    public void setParams(Ball ball, Paddle paddle, ArrayList<Block> blocksList) {
        this.ball = ball;
        this.paddle = paddle;
        this.blocksList = blocksList;
    }

    @Override
    public PhaseBase prepare() {
        if(blocksList.isEmpty()) {
            gameoverFlg = false;
            return PhaseManager.clearPhase;
        }
        else if(!gameoverFlg)
            return this;
        else {
            gameoverFlg = false;
            return PhaseManager.closingPhase;
        }
    }

    @Override
    public void update(float eventX) {
        /* ボール更新 */
        lifeGauge -= ball.updateCoordinates();
        if(lifeGauge <= 0)
            gameoverFlg = true;
        /* パドル移動 */
        paddle.move((int)eventX);
        /* コリジョンチェック(ボール-パドル) */
        ball.checkPaddleCollision(paddle);
        /* コリジョンチェック(ボール-ブロックs) */
        score += ball.checkBlocksCollision(blocksList);
    }

    @Override
    public void draw(Canvas canvas) {
        /* ボール描画 */
        ball.draw(canvas);
        /* ブロック描画 */
        for(Block item : blocksList)
            item.draw(canvas);
        /* パドル描画 */
        paddle.draw(canvas);

        canvas.drawText("SCORE = "+ score, 0, 50, scorePaint);
        canvas.drawText("Life: " + lifeGauge, canvas.getWidth(), 50, lifePaint);
    }
}

/** ClaarStage **/
class ClaarPhase implements PhaseBase {
    final Paint paint = new Paint();
    public ClaarPhase() {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(72);
        paint.setColor(Color.GREEN);
    }

    @Override public PhaseBase prepare() { return this; }
    @Override public void update(float eventX) {/*何もしない*/}
    @Override
    public void draw(Canvas canvas) {
        canvas.drawText("Game Cleared!", (float)canvas.getWidth()/2,(float)(canvas.getHeight()/2)-50, paint);
        canvas.drawText("Good Gob!!!", (float)canvas.getWidth()/2,(float)(canvas.getHeight()/2)-50+72, paint);
    }
}

/** ClosingStage **/
class ClosingPhase implements PhaseBase {
    final Paint paint = new Paint();
    public ClosingPhase() {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(72);
        paint.setColor(Color.RED);
    }

    @Override public PhaseBase prepare() { return this; }
    @Override public void update(float eventX) {/*何もしない*/}
    @Override
    public void draw(Canvas canvas) {
        canvas.drawText("GAME OVER!!!", (float)canvas.getWidth()/2,(float)(canvas.getHeight()/2)-50, paint);
    }
}
