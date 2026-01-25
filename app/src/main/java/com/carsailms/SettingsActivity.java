package com.carsailms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.carsailms.utils.PreferenceManager;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingsActivity extends AppCompatActivity {
    
    private PreferenceManager preferenceManager;
    private RadioGroup themeRadioGroup, languageRadioGroup;
    private RadioButton radioLight, radioDark, radioSystem;
    private RadioButton radioEnglish, radioPortuguese;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        preferenceManager = new PreferenceManager(this);
        preferenceManager.applyTheme();
        preferenceManager.applyLanguage();
        
        setContentView(R.layout.activity_settings);
        
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings);
        }
        
        initViews();
        loadSettings();
        setupListeners();
    }
    
    private void initViews() {
        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        languageRadioGroup = findViewById(R.id.languageRadioGroup);
        radioLight = findViewById(R.id.radioLight);
        radioDark = findViewById(R.id.radioDark);
        radioSystem = findViewById(R.id.radioSystem);
        radioEnglish = findViewById(R.id.radioEnglish);
        radioPortuguese = findViewById(R.id.radioPortuguese);
    }
    
    private void loadSettings() {
        int theme = preferenceManager.getTheme();
        switch (theme) {
            case PreferenceManager.THEME_LIGHT:
                radioLight.setChecked(true);
                break;
            case PreferenceManager.THEME_DARK:
                radioDark.setChecked(true);
                break;
            case PreferenceManager.THEME_SYSTEM:
                radioSystem.setChecked(true);
                break;
        }
        
        String language = preferenceManager.getLanguage();
        if (language.equals("pt")) {
            radioPortuguese.setChecked(true);
        } else {
            radioEnglish.setChecked(true);
        }
    }
    
    private void setupListeners() {
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int theme;
            if (checkedId == R.id.radioLight) {
                theme = PreferenceManager.THEME_LIGHT;
            } else if (checkedId == R.id.radioDark) {
                theme = PreferenceManager.THEME_DARK;
            } else {
                theme = PreferenceManager.THEME_SYSTEM;
            }
            preferenceManager.setTheme(theme);
            recreate();
        });
        
        languageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String languageCode = (checkedId == R.id.radioPortuguese) ? "pt" : "en";
            preferenceManager.setLanguage(languageCode);
            restartApp();
        });
    }
    
    private void restartApp() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
