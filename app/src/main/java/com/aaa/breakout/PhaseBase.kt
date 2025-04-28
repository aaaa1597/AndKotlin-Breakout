package com.aaa.breakout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

const val OPENING_PHASE_PERIOD = 66

interface PhaseBase {
    fun prepare(context: Context): PhaseBase
    fun update(eventX: Float)
    fun draw(canvas: Canvas)
}

/** PhaseManager  */
internal object PhaseManager {
    private lateinit var openingPhase: OpeningPhase
    lateinit var buildPhase: BuildPhase
    lateinit var playPhase: PlayPhase
    lateinit var closingPhase: ClosingPhase
    lateinit var clearPhase: ClearPhase
    private var currentPhase: PhaseBase = NonePhase()

    fun init(soundFlg: Boolean, screenW: Int, screenH: Int) {
        openingPhase= OpeningPhase()
        buildPhase  = BuildPhase(soundFlg, screenW, screenH)
        playPhase   = PlayPhase()
        closingPhase= ClosingPhase()
        clearPhase  = ClearPhase()
        currentPhase= openingPhase
    }

    fun getNextPhase(context: Context): PhaseBase {
        currentPhase = currentPhase.prepare(context)
        return currentPhase
    }

    /** NonePhase(何もしないフェーズ)  */
    class NonePhase : PhaseBase {
        override fun prepare(context: Context): PhaseBase { return this }
        override fun update(eventX: Float) {}
        override fun draw(canvas: Canvas) {}
    }

    /** OpeningStage  */
    class OpeningPhase : PhaseBase {
        private val paint = Paint()

        /* 初期化 */
        init {
            paint.textAlign = Paint.Align.CENTER
            paint.color = Color.WHITE
            paint.textSize = 72f
        }

        /* 準備 */
        override fun prepare(context: Context): PhaseBase {
            phaseCounter++
            if (phaseCounter < OPENING_PHASE_PERIOD) return this
            else phaseCounter = 0
            return buildPhase
        }

        /* 更新 */
        override fun update(eventX: Float) { /*何もしない*/}

        /* 描画 */
        override fun draw(canvas: Canvas)
            = canvas.drawText("GET READY...", canvas.width.toFloat() / 2,(canvas.height / 2).toFloat(), paint)

        companion object {
            private var phaseCounter = 0
        }
    }

    /** BuildStage  */
    class BuildPhase /* コンストラクタ */(
        private val mSoundFlg: Boolean,
        private val mScreenW: Int,
        private val mScreenH: Int
    ) : PhaseBase {
        /* 準備 */
        override fun prepare(context: Context): PhaseBase {
            val ball   = Ball(context, mSoundFlg).apply { init(mScreenW, mScreenH) }
            val paddle = Paddle().apply { init(mScreenW, mScreenH) }
            val blocksList: ArrayList<Block> = ArrayList()
            initBlocks(mScreenW, mScreenH, blocksList)
            playPhase.setParams(ball, paddle, blocksList)
            return playPhase
        }

        override fun update(eventX: Float) {}
        override fun draw(canvas: Canvas) {}

        /* 準備2(ブロック初期化) */
        private fun initBlocks(screenW: Int, screenH: Int, blocksList: ArrayList<Block>) {
            val blockHeight = screenW / 36
            val spacing = screenW / 144
            val topOffset = screenH / 10
            val blockWidth = (screenW / 10) - spacing

            for (idxy in 0..9) {
                for (idxx in 0..9) {
                    val ycoordinate = (idxy * (blockHeight + spacing)) + topOffset
                    val xcoordinate = idxx * (blockWidth + spacing)

                    val rect = Rect()
                    rect.set(xcoordinate, ycoordinate, xcoordinate + blockWidth, ycoordinate + blockHeight)

                    if (idxy < 2) blocksList.add(Block(rect, Color.RED))
                    else if (idxy < 4) blocksList.add(Block(rect, Color.YELLOW))
                    else if (idxy < 6) blocksList.add(Block(rect, Color.GREEN))
                    else if (idxy < 8) blocksList.add(Block(rect, Color.MAGENTA))
                    else blocksList.add(Block(rect, Color.LTGRAY))
                }
            }
        }
    }

    /** PlayStage  */
    internal class PlayPhase : PhaseBase {
        private lateinit var ball: Ball
        private lateinit var paddle: Paddle
        private lateinit var blocksList: ArrayList<Block>
        private var score = 0
        private var lifeGauge = 5
        private var scorePaint: Paint = Paint().apply {
                                                    color = Color.WHITE
                                                    textSize = 60f
                                                }
        private val lifePaint: Paint = Paint().apply {
                                                    textAlign = Paint.Align.RIGHT
                                                    color = Color.WHITE
                                                    textSize = 60f
                                                }
        private var gameoverFlg = false

        fun setParams(ball: Ball, paddle: Paddle, blocksList: ArrayList<Block>) {
            this.ball = ball
            this.paddle = paddle
            this.blocksList = blocksList
        }

        override fun prepare(context: Context): PhaseBase {
            if (blocksList.isEmpty()) {
                gameoverFlg = false
                return clearPhase
            }
            else if (!gameoverFlg)
                return this
            else {
                gameoverFlg = false
                return closingPhase
            }
        }

        override fun update(eventX: Float) {
            /* ボール更新 */
            lifeGauge -= ball.updateCoordinates()
            if (lifeGauge <= 0) gameoverFlg = true
            /* パドル移動 */
            paddle.move(eventX.toInt())
            /* コリジョンチェック(ボール-パドル) */
            ball.checkPaddleCollision(paddle)
            /* コリジョンチェック(ボール-ブロックs) */
            score += ball.checkBlocksCollision(blocksList)
        }

        override fun draw(canvas: Canvas) {
            /* ボール描画 */
            ball.draw(canvas)
            /* ブロック描画 */
            for (item in blocksList) item.draw(canvas)
            /* パドル描画 */
            paddle.draw(canvas)

            canvas.drawText("SCORE = $score", 0f, 50f, scorePaint)
            canvas.drawText("Life: $lifeGauge", canvas.width.toFloat(), 50f, lifePaint)
        }
    }

    /** ClearStage  */
    internal class ClearPhase : PhaseBase {
        private val paint: Paint = Paint().apply {
                                        textAlign = Paint.Align.CENTER
                                        textSize = 72f
                                        color = Color.GREEN
                                    }
        override fun prepare(context: Context): PhaseBase { return this }
        override fun update(eventX: Float) { /*何もしない*/ }
        override fun draw(canvas: Canvas) {
            canvas.drawText( "Game Cleared!", canvas.width.toFloat() / 2, (canvas.height / 2).toFloat() - 50, paint )
            canvas.drawText( "Good Gob!!!", canvas.width.toFloat() / 2, (canvas.height / 2).toFloat() - 50 + 72, paint )
        }
    }

    /** ClosingStage  */
    internal class ClosingPhase : PhaseBase {
        private val paint: Paint = Paint().apply {
                                            textAlign = Paint.Align.CENTER
                                            textSize = 72f
                                            color = Color.RED
                                        }

        override fun prepare(context: Context): PhaseBase { return this }
        override fun update(eventX: Float) { /*何もしない*/ }
        override fun draw(canvas: Canvas)
            = canvas.drawText("GAME OVER!!!", canvas.width.toFloat() / 2, (canvas.height / 2).toFloat() - 50, paint)
    }
}
