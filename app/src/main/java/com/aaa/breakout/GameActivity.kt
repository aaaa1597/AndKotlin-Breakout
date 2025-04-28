package com.aaa.breakout

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameActivity : AppCompatActivity() {
    companion object {
        private const val PREFS = "PREFS"
//      private const val ITEM_HIGHSCORE = "ITEM_HIGHSCORE"
        private const val ITEM_SOUNDONOFF = "ITEM_SOUNDONOFF"
//      private const val ITEM_NEWGAME = "ITEM_NEWGAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        /* 設定値取得 */
        val pref = getSharedPreferences(PREFS, 0)
        val soundFlg = pref.getBoolean(ITEM_SOUNDONOFF, true)

        val sv = findViewById<GameSurfaceView>(R.id.surfaceview)
        sv.setSoundFlg(soundFlg)
        sv.holder.addCallback(sv)
    }

    override fun onDestroy() {
        super.onDestroy()

        val sv = findViewById<GameSurfaceView>(R.id.surfaceview)
        sv.holder.removeCallback(sv)
    }
}