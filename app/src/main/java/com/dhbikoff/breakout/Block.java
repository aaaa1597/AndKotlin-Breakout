package com.dhbikoff.breakout;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

public class Block extends ShapeDrawable {
    private Paint paint;
    private int blockColor;

    public Block(Rect rect, int color) {
        super(new RectShape());
        this.setBounds(rect);
        paint = new Paint();
        paint.setColor(color);
        blockColor = color;
    }

    public void drawBlock(Canvas canvas) {
        canvas.drawRect(this.getBounds(), paint);
    }

    public int getColor() {
        return paint.getColor();
    }
    public int[] toIntArray() {
        Rect rect = getBounds();
        if(paint.getColor() != blockColor)
            throw new RuntimeException("aaaaaa paint.getColor() != blockColorやし!!");
        int[] arr = { rect.left, rect.top, rect.right, rect.bottom, blockColor };
        return arr;
    }

}
