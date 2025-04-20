package com.dhbikoff.breakout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class Splash extends AppCompatActivity {
    private static final String HIGH_SCORE_PREF = "HIGH_SCORE_PREF";
    private static final String SOUND_ON_OFF = "SOUND_ON_OFF";
    private static final String SOUND_PREFS = "SOUND_PREFS";
    private static final String NEW_GAME = "NEW_GAME";
    private boolean soundFlg = false;
    private int highScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        /* 音量設定変更したときの変更先をミュージックに */
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        /* NewGameボタン押下 */
        findViewById(R.id.newGameButton).setOnClickListener(view -> {
            Intent intent = new Intent(this, Breakout.class);
            intent.putExtra(NEW_GAME, true);
            intent.putExtra(SOUND_ON_OFF, soundFlg);
            startActivity(intent);
        });
        /* ContinueGameボタン押下 */
        findViewById(R.id.contGameButton).setOnClickListener(view -> {
            Intent intent = new Intent(this, Breakout.class);
            intent.putExtra(NEW_GAME, false);
            intent.putExtra(SOUND_ON_OFF, soundFlg);
            startActivity(intent);
        });
        /* 音声ON/OFFボタン押下 */
        SwitchCompat sc = findViewById(R.id.soundToggleButton);
        sc.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            soundFlg = isChecked;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences soundSettings = getSharedPreferences(SOUND_PREFS, 0);
        soundFlg = soundSettings.getBoolean("soundOn", true);
        SwitchCompat soundButton = findViewById(R.id.soundToggleButton);
        soundButton.setChecked(soundFlg);

        SharedPreferences scoreSettings = getSharedPreferences(HIGH_SCORE_PREF, 0);
        highScore = scoreSettings.getInt("highScore", 0);
        TextView hiScore = findViewById(R.id.hiScoreView);
        hiScore.setText(String.format(Locale.JAPAN, "High Score = %d", highScore));

    }

    @Override
    protected void onPause() {
        super.onPause();

        // save sound settings and high score
        SharedPreferences soundSettings = getSharedPreferences(SOUND_PREFS, 0);
        SharedPreferences.Editor soundEditor = soundSettings.edit();
        soundEditor.putBoolean("soundOn", soundFlg);
        soundEditor.apply();

        SharedPreferences highScoreSave = getSharedPreferences(HIGH_SCORE_PREF, 0);
        SharedPreferences.Editor scoreEditor = highScoreSave.edit();
        scoreEditor.putInt("highScore", highScore);

        // Commit the edits
        scoreEditor.apply();
    }
}