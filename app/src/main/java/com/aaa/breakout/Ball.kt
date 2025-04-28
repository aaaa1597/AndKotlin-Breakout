package com.aaa.breakout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.media.AudioAttributes
import android.media.SoundPool
import java.util.Random
import java.util.function.Function

class Ball(context: Context, sound: Boolean) : ShapeDrawable(OvalShape()) {
    private val soundOn = sound

    // ball dimensions
    private var left = 0
    private var right = 0
    private var top = 0
    private var bottom = 0
    private var radius = 0

    // ball speed
    private var velocityX = 0
    private var velocityY = 0

    // timer when ball hits screen bottom
    private var resetBallTimer = 1000

    private var SCREEN_WIDTH = 0
    private var SCREEN_HEIGHT = 0
    private var paddleCollision = false
    private lateinit var mPaddle: Rect
    private lateinit var ballRect: Rect

    private lateinit var soundPool: SoundPool
    private var paddleSoundId = 0
    private var blockSoundId = 0
    private var bottomSoundId = 0

    /* 初期化 */
    init {
        paint.color = Color.CYAN

        if (soundOn) {
            soundPool = SoundPool.Builder()
                            .setMaxStreams(2)
                            .setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build()
                            ).build()
            paddleSoundId = soundPool.load(context, R.raw.paddle, 0)
            blockSoundId  = soundPool.load(context, R.raw.block, 0)
            bottomSoundId = soundPool.load(context, R.raw.bottom, 0)
        }
    }

    /* 外部公開の初期値設定関数 */
    fun init(width: Int, height: Int) {
        paddleCollision = false
        SCREEN_WIDTH = width
        SCREEN_HEIGHT = height

        radius   = SCREEN_WIDTH / 72
        velocityX= radius / 2
        velocityY= radius

        // ball coordinates
        left  = (SCREEN_WIDTH / 2) - radius
        right = (SCREEN_WIDTH / 2) + radius
        top   = (SCREEN_HEIGHT / 2.5).toInt() - radius
        bottom= (SCREEN_HEIGHT / 2.5).toInt() + radius

        val startingXDirection = Random().nextInt(2) // random beginning direction
        if (startingXDirection > 0) {
            velocityX = -velocityX
        }
    }

    fun updateCoordinates(): Int {
        var lifeDamage = 0

        // paddle collision
        if (paddleCollision && velocityY > 0) {
            val paddleSplit = (mPaddle.right - mPaddle.left) / 10
            val ballCenter = ballRect.centerX()
            if (ballCenter < mPaddle.left + paddleSplit)
                velocityX = -(radius * 3)
            else if (ballCenter < mPaddle.left + (paddleSplit * 2))
                velocityX = -(radius * 2)
            else if((mPaddle.left + (paddleSplit*2) <= ballCenter &&
                                                       ballCenter <= (mPaddle.left + mPaddle.right)/2))
                velocityX = -radius
            else if((mPaddle.left + mPaddle.right)/2 <= ballCenter &&
                                                        ballCenter <= mPaddle.right - (paddleSplit * 2))
                velocityX = radius
            else if (ballCenter < mPaddle.right - (paddleSplit * 2))
                velocityX = radius * 2
            else
                velocityX = radius * 3

            velocityY = -velocityY
        }

        // side walls collision
        if (bounds.right >= SCREEN_WIDTH)
            velocityX = -velocityX
        else if (bounds.left <= 0) {
            this.setBounds(0, top, radius * 2, bottom)
            velocityX = -velocityX
        }

        // screen top/bottom collisions
        if (bounds.top <= 0)
            velocityY = -velocityY
        else if (bounds.top > SCREEN_HEIGHT) {
            lifeDamage = 1 // lose a turn
            if (soundOn)
                soundPool.play(bottomSoundId, 1f, 1f, 1, 0, 1f)
            try {
                Thread.sleep(resetBallTimer.toLong())
                init(SCREEN_WIDTH, SCREEN_HEIGHT) // reset ball
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        // move ball
        left  += velocityX
        right += velocityX
        top   += velocityY
        bottom+= velocityY

        return lifeDamage
    }

    override fun draw(canvas: Canvas) {
        super.setBounds(left, top, right, bottom)
        super.draw(canvas)
    }

    fun checkPaddleCollision(paddle: Paddle) {
        mPaddle = paddle.bounds
        ballRect = this.bounds

        if (ballRect.left  >= mPaddle.left- (radius * 2) && ballRect.right <= mPaddle.right + (radius * 2)
         && ballRect.bottom>= mPaddle.top - (radius * 2) && ballRect.top   < mPaddle.bottom) {
            if (soundOn && velocityY > 0)
                soundPool.play(paddleSoundId, 1f, 1f, 1, 0, 1f)
            paddleCollision = true
        }
        else
            paddleCollision = false

        return
    }

    fun checkBlocksCollision(blocks: ArrayList<Block>): Int {
        ballRect = this.bounds

        val nextBallLeft  = ballRect.left + velocityX
        val nextBallRight = ballRect.right + velocityY
        val nextBallTop   = ballRect.top + velocityY
        val nextBallBottom= ballRect.bottom + velocityY

        /* コリジョン処理 */
        val collided = Function { color: Int -> Function { idx: Int ->
                    velocityY = -velocityY  /* y反転 */
                    blocks.removeAt(idx)    /* ブロック排除 */
                    if (soundOn)            /* 衝突音再生 */
                        soundPool.play(blockSoundId, 1f, 1f, 1, 0, 1f)
                    getPoints(color)
                }
            }

        /* コリジョン判定 */
        val it: ListIterator<Block> = blocks.listIterator(blocks.size)
        while (it.hasPrevious()) {
            val idx = it.previousIndex()
            val block = it.previous()
            val blockRect = block.bounds
            val color = block.getColor()

            if (nextBallLeft>= blockRect.left   && nextBallLeft <= blockRect.right &&
                nextBallTop <= blockRect.bottom && nextBallTop >= blockRect.top)
                return collided.apply(color).apply(idx)
            else if (nextBallRight<= blockRect.right  && nextBallRight >= blockRect.left &&
                      nextBallTop <= blockRect.bottom && nextBallTop   >= blockRect.top)
                return collided.apply(color).apply(idx)
            else if (nextBallLeft  >= blockRect.left   && nextBallLeft  <= blockRect.right &&
                     nextBallBottom<= blockRect.bottom && nextBallBottom>= blockRect.top)
                return collided.apply(color).apply(idx)
            else if (nextBallRight <= blockRect.right  && nextBallRight >= blockRect.left &&
                     nextBallBottom<= blockRect.bottom && nextBallBottom>= blockRect.top)
                return collided.apply(color).apply(idx)
        }
        return 0
    }

    private fun getPoints(color: Int): Int {
        return when (color) {
                                Color.LTGRAY -> 100
                                Color.MAGENTA -> 200
                                Color.GREEN -> 300
                                Color.YELLOW -> 400
                                Color.RED -> 500
                                else -> 0
                            }
    }
}