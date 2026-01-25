package com.carsailms.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.BuildConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.carsailms.R;

import org.json.JSONException;
import org.json.JSONObject;

/** Gerenciador de Firebase Remote Config Centraliza todas as configurações remotas do app */
public class RemoteConfigManager {

  private static final String TAG = "RemoteConfig";
  private static RemoteConfigManager instance;

  private final FirebaseRemoteConfig remoteConfig;
  private final Context context;

  // Chaves dos parâmetros
  public static final String KEY_MAINTENANCE_MODE = "maintenance_mode";
  public static final String KEY_MAINTENANCE_MESSAGE = "maintenance_message";
  public static final String KEY_FORCE_UPDATE = "force_update";
  public static final String KEY_MIN_VERSION = "min_version";
  public static final String KEY_UPDATE_MESSAGE = "update_message";
  public static final String KEY_UPDATE_URL = "update_url";
  public static final String KEY_WELCOME_MESSAGE = "welcome_message";
  public static final String KEY_SHOW_PROMO = "show_promo";
  public static final String KEY_PROMO_BANNER = "promo_banner";
  public static final String KEY_PROMO_URL = "promo_url";
  public static final String KEY_ENABLE_DARK_MODE = "enable_dark_mode";
  public static final String KEY_ENABLE_DOWNLOADS = "enable_downloads";
  public static final String KEY_MAX_FILE_SIZE = "max_file_size_mb";
  public static final String KEY_THEME_COLORS = "theme_colors";
  public static final String KEY_HOME_URL = "home_url";

  private RemoteConfigManager(Context context) {
    this.context = context.getApplicationContext();
    this.remoteConfig = FirebaseRemoteConfig.getInstance();

    // Configurar
    FirebaseRemoteConfigSettings configSettings =
        new FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(BuildConfig.DEBUG ? 0 : 3600) // 1 hora em produção
            .build();

    remoteConfig.setConfigSettingsAsync(configSettings);
    remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
  }

  public static synchronized RemoteConfigManager getInstance(Context context) {
    if (instance == null) {
      instance = new RemoteConfigManager(context);
    }
    return instance;
  }

  /** Buscar configurações do servidor */
  public void fetchConfig(ConfigListener listener) {
    remoteConfig
        .fetchAndActivate()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful()) {
                boolean updated = task.getResult();
                Log.d(TAG, "Config atualizado: " + updated);

                if (listener != null) {
                  listener.onConfigFetched(updated);
                }

                // Aplicar configurações críticas
                applyCriticalConfig();

              } else {
                Log.e(TAG, "Erro ao buscar config", task.getException());
                if (listener != null) {
                  listener.onConfigError(task.getException());
                }
              }
            });
  }

  /** Verificar e aplicar configurações críticas */
  private void applyCriticalConfig() {
    // Verificar modo manutenção
    if (isMaintenanceMode()) {
      Log.w(TAG, "App em modo manutenção");
    }

    // Verificar atualização forçada
    if (shouldForceUpdate()) {
      Log.w(TAG, "Atualização obrigatória necessária");
    }
  }

  // ==========================================
  // MODO MANUTENÇÃO
  // ==========================================

  public boolean isMaintenanceMode() {
    return remoteConfig.getBoolean(KEY_MAINTENANCE_MODE);
  }

  public String getMaintenanceMessage() {
    return remoteConfig.getString(KEY_MAINTENANCE_MESSAGE);
  }

  public void showMaintenanceDialog(Context context, Runnable onClose) {
    new AlertDialog.Builder(context)
        .setTitle("Manutenção")
        .setMessage(getMaintenanceMessage())
        .setCancelable(false)
        .setPositiveButton(
            "OK",
            (dialog, which) -> {
              if (onClose != null) {
                onClose.run();
              }
            })
        .show();
  }

  // ==========================================
  // ATUALIZAÇÃO
  // ==========================================

  public boolean shouldForceUpdate() {
    if (!remoteConfig.getBoolean(KEY_FORCE_UPDATE)) {
      return false;
    }

    String currentVersion = BuildConfig.VERSION_NAME;
    String minVersion = remoteConfig.getString(KEY_MIN_VERSION);

    return compareVersions(currentVersion, minVersion) < 0;
  }

  public void showUpdateDialog(Context context, boolean force) {
    String message = remoteConfig.getString(KEY_UPDATE_MESSAGE);
    String url = remoteConfig.getString(KEY_UPDATE_URL);

    AlertDialog.Builder builder =
        new AlertDialog.Builder(context)
            .setTitle("Atualização Disponível")
            .setMessage(message)
            .setPositiveButton(
                "Atualizar",
                (dialog, which) -> {
                  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                  context.startActivity(intent);
                });

    if (!force) {
      builder.setNegativeButton("Mais Tarde", null);
    } else {
      builder.setCancelable(false);
    }

    builder.show();
  }

  private int compareVersions(String version1, String version2) {
    String[] v1Parts = version1.split("\\.");
    String[] v2Parts = version2.split("\\.");

    int length = Math.max(v1Parts.length, v2Parts.length);
    for (int i = 0; i < length; i++) {
      int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
      int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;

      if (v1Part > v2Part) return 1;
      if (v1Part < v2Part) return -1;
    }
    return 0;
  }

  // ==========================================
  // MENSAGENS
  // ==========================================

  public String getWelcomeMessage() {
    return remoteConfig.getString(KEY_WELCOME_MESSAGE);
  }

  public void showWelcomeMessage(Context context) {
    String message = getWelcomeMessage();
    if (!message.isEmpty()) {
      Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
  }

  // ==========================================
  // PROMOÇÕES
  // ==========================================

  public boolean shouldShowPromo() {
    return remoteConfig.getBoolean(KEY_SHOW_PROMO);
  }

  public String getPromoBanner() {
    return remoteConfig.getString(KEY_PROMO_BANNER);
  }

  public String getPromoUrl() {
    return remoteConfig.getString(KEY_PROMO_URL);
  }

  // ==========================================
  // FUNCIONALIDADES
  // ==========================================

  public boolean isDarkModeEnabled() {
    return remoteConfig.getBoolean(KEY_ENABLE_DARK_MODE);
  }

  public boolean areDownloadsEnabled() {
    return remoteConfig.getBoolean(KEY_ENABLE_DOWNLOADS);
  }

  public long getMaxFileSizeMB() {
    return remoteConfig.getLong(KEY_MAX_FILE_SIZE);
  }

  // ==========================================
  // TEMA E CORES
  // ==========================================

  public ThemeColors getThemeColors() {
    String json = remoteConfig.getString(KEY_THEME_COLORS);
    try {
      JSONObject colors = new JSONObject(json);
      return new ThemeColors(
          colors.getString("primary"),
          colors.getString("secondary"),
          colors.optString("accent", "#4CAF50"));
    } catch (JSONException e) {
      Log.e(TAG, "Erro ao parsear cores do tema", e);
      return new ThemeColors("#2196F3", "#F44336", "#4CAF50");
    }
  }

  public static class ThemeColors {
    public final String primary;
    public final String secondary;
    public final String accent;

    public ThemeColors(String primary, String secondary, String accent) {
      this.primary = primary;
      this.secondary = secondary;
      this.accent = accent;
    }
  }

  // ==========================================
  // URLs
  // ==========================================

  public String getHomeUrl() {
    return remoteConfig.getString(KEY_HOME_URL);
  }

  // ==========================================
  // UTILITÁRIOS
  // ==========================================

  /** Obter valor string */
  public String getString(String key) {
    return remoteConfig.getString(key);
  }

  /** Obter valor boolean */
  public boolean getBoolean(String key) {
    return remoteConfig.getBoolean(key);
  }

  /** Obter valor long */
  public long getLong(String key) {
    return remoteConfig.getLong(key);
  }

  /** Obter valor double */
  public double getDouble(String key) {
    return remoteConfig.getDouble(key);
  }

  /** Obter objeto JSON */
  public JSONObject getJSON(String key) {
    try {
      return new JSONObject(remoteConfig.getString(key));
    } catch (JSONException e) {
      Log.e(TAG, "Erro ao parsear JSON: " + key, e);
      return new JSONObject();
    }
  }

  /** Log de todos os valores (debug) */
  public void logAllValues() {
    if (!BuildConfig.DEBUG) return;

    Log.d(TAG, "=== Remote Config Values ===");
    Log.d(TAG, "Maintenance Mode: " + isMaintenanceMode());
    Log.d(TAG, "Force Update: " + shouldForceUpdate());
    Log.d(TAG, "Welcome Message: " + getWelcomeMessage());
    Log.d(TAG, "Show Promo: " + shouldShowPromo());
    Log.d(TAG, "Dark Mode Enabled: " + isDarkModeEnabled());
    Log.d(TAG, "Downloads Enabled: " + areDownloadsEnabled());
    Log.d(TAG, "Max File Size: " + getMaxFileSizeMB() + "MB");
    Log.d(TAG, "Home URL: " + getHomeUrl());

    ThemeColors colors = getThemeColors();
    Log.d(
        TAG,
        "Theme Colors - Primary: "
            + colors.primary
            + ", Secondary: "
            + colors.secondary
            + ", Accent: "
            + colors.accent);
    Log.d(TAG, "============================");
  }

  /** Interface para callbacks */
  public interface ConfigListener {
    void onConfigFetched(boolean updated);

    void onConfigError(Exception error);
  }
}

// ==========================================
// EXEMPLO DE USO NO MAINACTIVITY
// ==========================================

/*
public class MainActivity extends AppCompatActivity {

    private RemoteConfigManager configManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Remote Config
        configManager = RemoteConfigManager.getInstance(this);

        // Buscar configurações
        configManager.fetchConfig(new RemoteConfigManager.ConfigListener() {
            @Override
            public void onConfigFetched(boolean updated) {
                applyRemoteConfig();
            }

            @Override
            public void onConfigError(Exception error) {
                Log.e("MainActivity", "Erro ao buscar config", error);
                // Usar valores padrão
                applyRemoteConfig();
            }
        });
    }

    private void applyRemoteConfig() {
        // 1. Verificar manutenção
        if (configManager.isMaintenanceMode()) {
            configManager.showMaintenanceDialog(this, this::finish);
            return;
        }

        // 2. Verificar atualização forçada
        if (configManager.shouldForceUpdate()) {
            configManager.showUpdateDialog(this, true);
            return;
        }

        // 3. Mostrar mensagem de boas-vindas
        configManager.showWelcomeMessage(this);

        // 4. Verificar promoção
        if (configManager.shouldShowPromo()) {
            showPromoBanner();
        }

        // 5. Aplicar cores do tema
        applyThemeColors();

        // 6. Carregar URL inicial
        String homeUrl = configManager.getHomeUrl();
        webView.loadUrl(homeUrl);

        // 7. Configurar downloads
        if (!configManager.areDownloadsEnabled()) {
            disableDownloads();
        }

        // 8. Log (apenas debug)
        configManager.logAllValues();
    }

    private void showPromoBanner() {
        String bannerText = configManager.getPromoBanner();
        String promoUrl = configManager.getPromoUrl();

        // Mostrar banner
        promoBannerView.setText(bannerText);
        promoBannerView.setVisibility(View.VISIBLE);
        promoBannerView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(promoUrl));
            startActivity(intent);
        });
    }

    private void applyThemeColors() {
        RemoteConfigManager.ThemeColors colors = configManager.getThemeColors();

        // Aplicar cores
        int primaryColor = Color.parseColor(colors.primary);
        int secondaryColor = Color.parseColor(colors.secondary);
        int accentColor = Color.parseColor(colors.accent);

        // Usar cores no tema
        toolbar.setBackgroundColor(primaryColor);
        fab.setBackgroundColor(accentColor);
    }
}
*/
