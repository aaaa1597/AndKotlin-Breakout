package com.aaa.breakout

import android.R.attr
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.aaa.breakout.PhaseManager.init
import com.aaa.breakout.PhaseManager.phase

const val FRAMERATE = 15

class GameSurfaceView: SurfaceView, Runnable, SurfaceHolder.Callback {
    private var isRunning = false
    private lateinit var gameThread: Thread
    private lateinit var mHolder: SurfaceHolder
    private var eventX = 0f
    private var mSoundFlg = false

    /* Viewを継承したときのお約束 */
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    override fun surfaceCreated(holer: SurfaceHolder) {
        /* ↓kotlinの時はこれがないと描画が見えなかった。 */
//        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        /* 描画開始 */
        init(context, mSoundFlg, width, height)
        mHolder = holder
        isRunning = true
        gameThread = Thread(this)
        gameThread.start()

        /* ジェスチャバックを無効化 */
        systemGestureExclusionRects = ArrayList<Rect>().apply {
            add(Rect(0, attr.height - 650, attr.width, attr.height - 450))
            add(Rect(0, attr.height - 450, attr.width, attr.height - 250))
            add(Rect(0, attr.height - 250, attr.width, attr.height - 50))
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isRunning = false
        try {
            gameThread.join()
        } catch (e: InterruptedException) {
            Log.d("aaaaa", "error2!!", e)
        }
    }

    override fun run() {
        while (isRunning) {
            try {
                Thread.sleep(FRAMERATE.toLong())
            } catch (e: InterruptedException) {
                Log.e("aaaaa", "error!!", e)
            }

            val canvas = mHolder.lockCanvas() ?: continue
            /* 一旦全消去 */
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            val drawPhase = phase
            drawPhase.update(eventX)
            drawPhase.draw(canvas)

            mHolder.unlockCanvasAndPost(canvas)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            eventX = event.x
            performClick()
            return true
        }
        return false
    }

    fun setSoundFlg(soundFlg: Boolean) {
        mSoundFlg = soundFlg
    }
}