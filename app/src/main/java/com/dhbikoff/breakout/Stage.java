package com.dhbikoff.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

public interface Stage {
    Stage prepare(int screenW, int screenH);
    void update();
    void draw(Canvas canvas);
}

/** DrawStageFactory **/
class DrawStageFactory {
    private static final int OPENNING_STAGE = 66;
    private static final OpeningStage openingStage = new OpeningStage();
    private static final BuildStage buildStage = new BuildStage();
    private static final PlayStage playStage = new PlayStage();
    private static final ClosingStage olosingStage = new ClosingStage();
    private static Stage prevStage = openingStage;

    public static Stage getDrawStage(int times, int screenW, int screenH) {
        if(times<OPENNING_STAGE) {
            prevStage = openingStage.prepare(screenW, screenH);
            return prevStage;
        }
        else if(times==OPENNING_STAGE) {
            prevStage = buildStage.prepare(screenW, screenH);
            return prevStage;
        }
        else if(prevStage==openingStage/*&& OPENNING_STAGE<times*/) {
            prevStage = buildStage.prepare(screenW, screenH);
            return prevStage;
        }
        else if(prevStage==buildStage) {
            prevStage = playStage.prepare(screenW, screenH);
            return prevStage;
        }
        else if(times < Integer.MAX_VALUE) {
            prevStage = playStage.prepare(screenW, screenH);
            return prevStage;
        }
        else {
            prevStage = olosingStage.prepare(screenW, screenH);
            return prevStage;
        }
    }

    public static void sendParams(Paddle paddle, ArrayList<Block> blocksList) {
        playStage.setParams(paddle, blocksList);
    }
}

/** OpeningStage **/
class OpeningStage implements Stage {
    private final Paint paint = new Paint();
    @Override public OpeningStage prepare(int screenW, int screenH) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        paint.setTextSize(72);
        return this;
    }
    @Override public void update() {/*何もしない*/}
    @Override
    public void draw(Canvas canvas) {
        canvas.drawText("GET READY...",(float)canvas.getWidth()/2, (float)(canvas.getHeight()/2) , paint);
    }
}

/** BuildStage **/
class BuildStage implements Stage {

    @Override
    public Stage prepare(int screenW, int screenH) {
//      Ball ball = new Ball(this.getContext(), soundToggle);
//      ball.initCoords(screenW, screenH);
        Paddle paddle = new Paddle();
        paddle.init(screenW, screenH);
        ArrayList<Block> blocksList = new ArrayList<Block>();
        initBlocks(screenW, screenH, blocksList);
        DrawStageFactory.sendParams(/*ball,*/ paddle, blocksList);
        return this;
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Canvas canvas) {
    }

    private void initBlocks(int screenW, int screenH, ArrayList<Block> blocksList) {
        int blockHeight=  screenW / 36;
        int spacing    =  screenW / 144;
        int topOffset  =  screenH / 10;
        int blockWidth = (screenW / 10) - spacing;

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

/** PlayStage **/
class PlayStage implements Stage {
//  private Ball ball = null;
    private Paddle paddle = null;
    private ArrayList<Block> blocksList = null;
    private static int score = 0;
    private static int life = 0;
    public static Paint scorePaint = new Paint();
    public static Paint lifePaint = new Paint();
    public void setParams(Paddle paddle, ArrayList<Block> blocksList) {
//      this.ball = ball;
        this.paddle = paddle;
        this.blocksList = blocksList;
    }

    @Override
    public Stage prepare(int screenW, int screenH) {
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(60);
        lifePaint.setTextAlign(Paint.Align.RIGHT);
        lifePaint.setColor(Color.WHITE);
        lifePaint.setTextSize(60);
        return this;
    }

    @Override
    public void update() {
//        /* パドル移動 */
//        paddle.move((int)eventX);
    }

    @Override
    public void draw(Canvas canvas) {
        /* ブロック描画 */
        for(Block item : blocksList)
            item.draw(canvas);

        canvas.drawText("SCORE = "+ score, 0, 50, scorePaint);
        canvas.drawText("Life: " + life, canvas.getWidth(), 50, lifePaint);
    }
}

/** ClosingStage **/
class ClosingStage implements Stage {
    Paint paint = new Paint();
    public ClosingStage() {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(72);
        paint.setColor(Color.RED);
    }

    @Override
    public Stage prepare(int screenW, int screenH) {
        return this;
    }

    @Override public void update() {/*何もしない*/}
    @Override
    public void draw(Canvas canvas) {
        canvas.drawText("GAME OVER!!!", (float)canvas.getWidth()/2,(float)(canvas.getHeight()/2)-50, paint);
    }
}
