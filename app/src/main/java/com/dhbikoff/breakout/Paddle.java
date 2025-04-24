package com.dhbikoff.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Paddle extends ShapeDrawable {
    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;
    private int left;
    private int right;
    private int top;
    private int bottom;
    private int paddle_width;
    private int paddle_height;
    private int paddle_offset; // bottom screen offset
    private int paddle_move_offset; // move paddle to touch event speed

    public Paddle() {
        super(new RectShape());
        this.getPaint().setColor(Color.WHITE);
    }

    public void init(int width, int height) {
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;

        paddle_width = SCREEN_WIDTH / 10;
        paddle_height= SCREEN_WIDTH / 72;
        paddle_offset= SCREEN_HEIGHT/ 6;

        left  = (SCREEN_WIDTH / 2) - paddle_width;
        right = (SCREEN_WIDTH / 2) + paddle_width;
        top   = (SCREEN_HEIGHT - paddle_offset) - paddle_height;
        bottom= (SCREEN_HEIGHT - paddle_offset) + paddle_height;

        paddle_move_offset = SCREEN_WIDTH / 15;
    }

    public void draw(Canvas canvas) {
        super.setBounds(left, top, right, bottom);
        super.draw(canvas);
    }

    public void move(int eventX) {
        if (left <= eventX && eventX <= right) {
            left  = eventX - paddle_width;
            right = eventX + paddle_width;
        } else if (right < eventX) {
            left += paddle_move_offset;
            right+= paddle_move_offset;
        } else if (eventX < left) {
            left -= paddle_move_offset;
            right-= paddle_move_offset;
        }

        // keep paddle from going off screen left
        if (left < 0) {
            left = 0;
            right = paddle_width * 2;
        }

        // keep paddle from going off screen right
        if (right > SCREEN_WIDTH) {
            right= SCREEN_WIDTH;
            left = SCREEN_WIDTH - (paddle_width * 2);
        }
    }
}
