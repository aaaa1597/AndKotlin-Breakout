package com.aaa.breakout

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape


class Block(rect: Rect, color: Int) : ShapeDrawable(RectShape()) {
    private var paint: Paint = Paint().apply { this.color = color }

    init {
        bounds = rect
    }

    override fun draw(canvas: Canvas)
        = canvas.drawRect(this.bounds, paint)

    fun getColor()
        = paint.color
}
