package com.dhbikoff.breakout;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameActivity extends AppCompatActivity {
    private static final String PREFS = "PREFS";
    private static final String ITEM_HIGHSCORE = "ITEM_HIGHSCORE";
    private static final String ITEM_SOUNDONOFF = "ITEM_SOUNDONOFF";
    private static final String ITEM_NEWGAME = "ITEM_NEWGAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* 設定値取得 */
        SharedPreferences pref = getSharedPreferences(PREFS, 0);
        boolean soundFlg = pref.getBoolean(ITEM_SOUNDONOFF, true);

        GameSurfaceView sv = findViewById(R.id.surfaceview);
        sv.setSoundFlg(soundFlg);
        sv.getHolder().addCallback(sv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GameSurfaceView sv = findViewById(R.id.surfaceview);
        sv.getHolder().removeCallback(sv);
    }
}