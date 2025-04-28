package com.aaa.breakout

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape

class Paddle: ShapeDrawable(RectShape()) {
    private var SCREEN_WIDTH = 0
    private var SCREEN_HEIGHT = 0
    private var left = 0
    private var right = 0
    private var top = 0
    private var bottom = 0
    private var paddle_width = 0
    private var paddle_height = 0
    private var paddle_offset = 0 // bottom screen offset
    private var paddle_move_offset = 0 // move paddle to touch event speed

    init {
        paint.color = Color.WHITE
    }

    fun init(width: Int, height: Int) {
        SCREEN_WIDTH = width
        SCREEN_HEIGHT= height

        paddle_width = SCREEN_WIDTH / 10
        paddle_height= SCREEN_WIDTH / 72
        paddle_offset= SCREEN_HEIGHT / 6

        left  = (SCREEN_WIDTH / 2) - paddle_width
        right = (SCREEN_WIDTH / 2) + paddle_width
        top   = (SCREEN_HEIGHT - paddle_offset) - paddle_height
        bottom= (SCREEN_HEIGHT - paddle_offset) + paddle_height

        paddle_move_offset = SCREEN_WIDTH / 15
    }

    override fun draw(canvas: Canvas) {
        super.setBounds(left, top, right, bottom)
        super.draw(canvas)
    }

    fun move(eventX: Int) {
        if (eventX in left..right) {
            left = eventX - paddle_width
            right= eventX + paddle_width
        } else if (right < eventX) {
            left += paddle_move_offset
            right+= paddle_move_offset
        } else /*if (eventX < left)*/ {
            left -= paddle_move_offset
            right-= paddle_move_offset
        }

        // keep paddle from going off screen left
        if (left < 0) {
            left = 0
            right = paddle_width * 2
        }

        // keep paddle from going off screen right
        if (right > SCREEN_WIDTH) {
            right = SCREEN_WIDTH
            left  = SCREEN_WIDTH - (paddle_width * 2)
        }
    }
}