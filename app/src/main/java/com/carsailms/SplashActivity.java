package com.carsailms;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.carsailms.utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {
    
    private static final int SPLASH_DELAY = 3000;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.applyTheme();
        preferenceManager.applyLanguage();
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
