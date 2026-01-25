package com.carsailms.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.Locale;

public class PreferenceManager {
    
    private static final String PREF_NAME = "CarsaiLMSPrefs";
    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";
    
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;
    
    private final SharedPreferences preferences;
    private final Context context;
    
    public PreferenceManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void setTheme(int theme) {
        preferences.edit().putInt(KEY_THEME, theme).apply();
        applyTheme();
    }
    
    public int getTheme() {
        return preferences.getInt(KEY_THEME, THEME_SYSTEM);
    }
    
    public void applyTheme() {
        int theme = getTheme();
        switch (theme) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
    
    public void setLanguage(String languageCode) {
        preferences.edit().putString(KEY_LANGUAGE, languageCode).apply();
        applyLanguage();
    }
    
    public String getLanguage() {
        return preferences.getString(KEY_LANGUAGE, "system");
    }
    
    public void applyLanguage() {
        String languageCode = getLanguage();
        if (!languageCode.equals("system")) {
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);
            
            Resources resources = context.getResources();
            Configuration config = new Configuration(resources.getConfiguration());
            config.setLocale(locale);
            context.createConfigurationContext(config);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }
}
