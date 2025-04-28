package com.aaa.breakout

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

const val PREFS: String = "PREFS"
const val ITEM_HIGHSCORE: String = "ITEM_HIGHSCORE"
const val ITEM_SOUNDONOFF: String = "ITEM_SOUNDONOFF"
const val ITEM_NEWGAME: String = "ITEM_NEWGAME"

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /* 音量設定変更したときの変更先をミュージックに */
        volumeControlStream = AudioManager.STREAM_MUSIC

        /* NewGameボタン押下 */
        findViewById<View>(R.id.newGameButton).setOnClickListener { _: View? ->
            /* 一旦SharedPreferencesを更新 */
            val prefEditor = getSharedPreferences(PREFS, MODE_PRIVATE).edit()
            prefEditor.putBoolean(ITEM_NEWGAME, true)
            prefEditor.apply()
            /* 画面遷移 */
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        /* ContinueGameボタン押下 */
        findViewById<View>(R.id.contGameButton).setOnClickListener { _: View? ->
            /* 一旦SharedPreferencesを更新 */
            val prefEditor = getSharedPreferences(PREFS, MODE_PRIVATE).edit()
            prefEditor.putBoolean(ITEM_NEWGAME, false)
            prefEditor.apply()
            /* 画面遷移 */
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        /* 音声ON/OFFボタン押下 */
        val sc = findViewById<SwitchCompat>(R.id.soundToggleButton)
        sc.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            /* SharedPreferences更新 */
            val prefEditor = getSharedPreferences(PREFS, MODE_PRIVATE).edit()
            prefEditor.putBoolean(ITEM_SOUNDONOFF, isChecked)
            prefEditor.apply()
        }
    }

    override fun onResume() {
        super.onResume()
        val pref = getSharedPreferences(PREFS, 0)
        val soundFlg = pref.getBoolean(ITEM_SOUNDONOFF, true)
        val highScore = pref.getInt(ITEM_HIGHSCORE, 0)

        /* 音声ON/OFF設定 */
        val soundButton = findViewById<SwitchCompat>(R.id.soundToggleButton)
        soundButton.isChecked = soundFlg

        /* ハイスコア設定 */
        val hiScore = findViewById<TextView>(R.id.hiScoreView)
        hiScore.text = String.format(Locale.JAPAN, "High Score = %d", highScore)
    }
}