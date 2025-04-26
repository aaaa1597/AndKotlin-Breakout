package com.dhbikoff.breakout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Function;

public class Ball extends ShapeDrawable {
    // ball dimensions
    private int left;
    private int right;
    private int top;
    private int bottom;
    private int radius;

    // ball speed
    private int velocityX;
    private int velocityY;

    // timer when ball hits screen bottom
    private final int resetBallTimer = 1000;

    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;
    private boolean paddleCollision;
    private Rect mPaddle;
    private Rect ballRect;

    private boolean soundOn;
    private SoundPool soundPool;
    private int paddleSoundId;
    private int blockSoundId;
    private int bottomSoundId;

    public Ball(Context context, boolean sound) {
        super(new OvalShape());
        this.getPaint().setColor(Color.CYAN);
        soundOn = sound;

        if (soundOn) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes( new AudioAttributes.Builder()
                                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                                .build())
                    .build();
            paddleSoundId = soundPool.load(context, R.raw.paddle, 0);
            blockSoundId  = soundPool.load(context, R.raw.block, 0);
            bottomSoundId = soundPool.load(context, R.raw.bottom, 0);
        }
    }

    public void init(int width, int height) {
        Random rnd = new Random(); // starting x velocity direction

        paddleCollision= false;
        SCREEN_WIDTH   = width;
        SCREEN_HEIGHT  = height;

        radius    = SCREEN_WIDTH / 72;
        velocityX = radius;
        velocityY = radius * 2;

        // ball coordinates
        left  = (SCREEN_WIDTH / 2) - radius;
        right = (SCREEN_WIDTH / 2) + radius;
        top   = (SCREEN_HEIGHT / 2) - radius;
        bottom= (SCREEN_HEIGHT / 2) + radius;

        int startingXDirection = rnd.nextInt(2); // random beginning direction
        if (startingXDirection > 0) {
            velocityX = -velocityX;
        }
    }

    public void draw(Canvas canvas) {
        super.setBounds(left, top, right, bottom);
        super.draw(canvas);
    }

    public int updateCoordinates() {
        int lifeDamage = 0;

        // paddle collision
        if (paddleCollision && velocityY > 0) {
            int paddleSplit = (mPaddle.right - mPaddle.left) / 4;
            int ballCenter = ballRect.centerX();
            if (ballCenter < mPaddle.left + paddleSplit) {
                velocityX = -(radius * 3);
            } else if (ballCenter < mPaddle.left + (paddleSplit * 2)) {
                velocityX = -(radius * 2);
            } else if (ballCenter < mPaddle.centerX() + paddleSplit) {
                velocityX = radius * 2;
            } else {
                velocityX = radius * 3;
            }
            velocityY = -velocityY;
        }

        // side walls collision
        if (this.getBounds().right >= SCREEN_WIDTH) {
            velocityX = -velocityX;
        } else if (this.getBounds().left <= 0) {
            this.setBounds(0, top, radius * 2, bottom);
            velocityX = -velocityX;
        }

        // screen top/bottom collisions
        if (this.getBounds().top <= 0) {
            velocityY = -velocityY;
        } else if (this.getBounds().top > SCREEN_HEIGHT) {
            lifeDamage = 1; // lose a turn
            if (soundOn) {
                soundPool.play(bottomSoundId, 1, 1, 1, 0, 1);
            }
            try {
                Thread.sleep(resetBallTimer);
                init(SCREEN_WIDTH, SCREEN_HEIGHT); // reset ball
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // move ball
        left  += velocityX;
        right += velocityX;
        top   += velocityY;
        bottom+= velocityY;

        return lifeDamage;
    }

    public boolean checkPaddleCollision(Paddle paddle) {
        mPaddle = paddle.getBounds();
        ballRect = this.getBounds();

        if (       ballRect.left  >= mPaddle.left  - (radius * 2)
                && ballRect.right <= mPaddle.right + (radius * 2)
                && ballRect.bottom>= mPaddle.top   - (radius * 2)
                && ballRect.top   <  mPaddle.bottom) {
            if (soundOn && velocityY > 0) {
                soundPool.play(paddleSoundId, 1, 1, 1, 0, 1);
            }
            paddleCollision = true;
        } else
            paddleCollision = false;

        return paddleCollision;
    }

    public int checkBlocksCollision(ArrayList<Block> blocks) {
        int points = 0;
        int blockListLength = blocks.size();
        ballRect = this.getBounds();

        int ballLeft = ballRect.left + velocityX;
        int ballRight = ballRect.right + velocityY;
        int ballTop = ballRect.top + velocityY;
        int ballBottom = ballRect.bottom + velocityY;

        /* コリジョン処理 */
        Function<Integer, Function<Integer, Integer>> collided = (color) -> (idx) -> {
            velocityY = -velocityY;             /* y反転 */
            blocks.remove(idx.intValue());      /* ブロック排除 */
            if (soundOn) {                      /* 衝突音再生 */
                soundPool.play(blockSoundId, 1, 1, 1, 0, 1);
            }
            return getPoints(color);
        };

        // check collision; remove block if true
        for(ListIterator<Block> it = blocks.listIterator(blocks.size()); it.hasPrevious();) {
            Block block = it.previous();
            int idx = it.previousIndex();
            Rect blockRect = block.getBounds();
            int color = block.getColor();

            if(        ballLeft >= blockRect.left  - (radius * 2)
                    && ballLeft <= blockRect.right + (radius * 2)
                    && (ballTop == blockRect.bottom || ballTop == blockRect.top)) {
                return collided.apply(color).apply(idx);
            } else if (ballRight <= blockRect.right
                    && ballRight >= blockRect.left
                    && ballTop   <= blockRect.bottom
                    && ballTop   >= blockRect.top) {
                return collided.apply(color).apply(idx);
            } else if (ballLeft   >= blockRect.left
                    && ballLeft   <= blockRect.right
                    && ballBottom <= blockRect.bottom
                    && ballBottom >= blockRect.top) {
                return collided.apply(color).apply(idx);
            } else if (ballRight  <= blockRect.right
                    && ballRight  >= blockRect.left
                    && ballBottom <= blockRect.bottom
                    && ballBottom >= blockRect.top) {
                return collided.apply(color).apply(idx);
            }
        }
        return 0;
    }

    private int getPoints(int color) {
        switch(color) {
            case Color.LTGRAY: return 100;
            case Color.MAGENTA:return 200;
            case Color.GREEN:  return 300;
            case Color.YELLOW: return 400;
            case Color.RED:    return 500;
            default:           return 0;
        }
    }

}
