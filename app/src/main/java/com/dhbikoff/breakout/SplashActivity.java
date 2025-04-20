package com.dhbikoff.breakout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    private static final String PREFS = "PREFS";
    private static final String ITEM_HIGHSCORE = "ITEM_HIGHSCORE";
    private static final String ITEM_SOUNDONOFF = "ITEM_SOUNDONOFF";
    private static final String ITEM_NEWGAME = "ITEM_NEWGAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        /* 音量設定変更したときの変更先をミュージックに */
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        /* NewGameボタン押下 */
        findViewById(R.id.newGameButton).setOnClickListener(view -> {
            /* 一旦SharedPreferencesを更新 */
            SharedPreferences.Editor prefeditor = getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
            prefeditor.putBoolean(ITEM_NEWGAME, true);
            prefeditor.apply();
            /* 画面遷移 */
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });
        /* ContinueGameボタン押下 */
        findViewById(R.id.contGameButton).setOnClickListener(view -> {
            /* 一旦SharedPreferencesを更新 */
            SharedPreferences.Editor prefeditor = getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
            prefeditor.putBoolean(ITEM_NEWGAME, false);
            prefeditor.apply();
            /* 画面遷移 */
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });
        /* 音声ON/OFFボタン押下 */
        SwitchCompat sc = findViewById(R.id.soundToggleButton);
        sc.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            /* SharedPreferences更新 */
            SharedPreferences.Editor prefeditor = getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
            prefeditor.putBoolean(ITEM_SOUNDONOFF, isChecked);
            prefeditor.apply();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences(PREFS, 0);
        boolean soundFlg = pref.getBoolean(ITEM_SOUNDONOFF, true);
        int highScore = pref.getInt(ITEM_HIGHSCORE, 0);
        /* 音声ON/OFF設定 */
        SwitchCompat soundButton = findViewById(R.id.soundToggleButton);
        soundButton.setChecked(soundFlg);
        /* ハイスコア設定 */
        TextView hiScore = findViewById(R.id.hiScoreView);
        hiScore.setText(String.format(Locale.JAPAN, "High Score = %d", highScore));
    }
}