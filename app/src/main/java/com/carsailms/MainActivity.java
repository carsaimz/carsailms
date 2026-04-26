package com.carsailms;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.multidex.BuildConfig;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;

import com.carsailms.utils.NetworkUtils;
import com.carsailms.utils.PreferenceManager;
import com.carsailms.utils.RemoteConfigManager;
import com.carsailms.utils.UpdateChecker;
import com.carsailms.utils.WebViewClientCustom;
import com.carsailms.widgets.CustomWebChromeClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.carsailms.R;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private static final String BASE_URL = "https://carsailms.linkpc.net";

  private WebView webView;
  private FrameLayout progressBar;
  private View offlineLayout;
  private View bottomNavigation;
  private ImageButton btnBack, btnRefresh, btnForward, btnMenu;
  private TextView promoBannerView;

  private PreferenceManager preferenceManager;
  private RemoteConfigManager configManager;
  private FirebaseAnalytics firebaseAnalytics;

  private ValueCallback<Uri[]> filePathCallback;
  private ActivityResultLauncher<Intent> fileChooserLauncher;
  private ActivityResultLauncher<String[]> permissionLauncher;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    preferenceManager = new PreferenceManager(this);
    preferenceManager.applyTheme();
    preferenceManager.applyLanguage();

    setContentView(R.layout.activity_main);

    initFirebase();
    initViews();
    setupWebView();
    setupBottomNavigation();
    registerLaunchers();
    checkPermissions();

    // Buscar Remote Config antes de carregar
    fetchRemoteConfigAndLoad();
  }

  private void initFirebase() {
    // Analytics
    firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    Bundle bundle = new Bundle();
    bundle.putString("screen", "MainActivity");
    bundle.putString("version", BuildConfig.VERSION_NAME);
    firebaseAnalytics.logEvent("app_opened", bundle);

    // Remote Config Manager
    configManager = RemoteConfigManager.getInstance(this);

    // Firebase Messaging
    FirebaseMessaging.getInstance()
        .getToken()
        .addOnCompleteListener(
            task -> {
              if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult();
                Log.d(TAG, "FCM Token: " + token);
                // Enviar para servidor se necessário
              }
            });

    // Subscribe to topics
    FirebaseMessaging.getInstance()
        .subscribeToTopic("general")
        .addOnCompleteListener(
            task -> {
              String msg = task.isSuccessful() ? "Inscrito em 'general'" : "Erro ao inscrever";
              Log.d(TAG, msg);
            });

    FirebaseMessaging.getInstance()
        .subscribeToTopic("updates")
        .addOnCompleteListener(
            task -> {
              String msg = task.isSuccessful() ? "Inscrito em 'updates'" : "Erro ao inscrever";
              Log.d(TAG, msg);
            });
  }

  private void initViews() {
    webView = findViewById(R.id.webView);
    progressBar = findViewById(R.id.progressBar);
    offlineLayout = findViewById(R.id.offlineLayout);
    bottomNavigation = findViewById(R.id.bottomNavigation);
    btnBack = findViewById(R.id.btnBack);
    btnRefresh = findViewById(R.id.btnRefresh);
    btnForward = findViewById(R.id.btnForward);
    btnMenu = findViewById(R.id.btnMenu);

    // Banner promocional (opcional, adicionar no layout se quiser)
    // promoBannerView = findViewById(R.id.promoBanner);
  }

  private void setupWebView() {
    WebSettings webSettings = webView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setDomStorageEnabled(true);
    webSettings.setDatabaseEnabled(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setAllowContentAccess(true);
    webSettings.setLoadWithOverviewMode(true);
    webSettings.setUseWideViewPort(true);
    webSettings.setBuiltInZoomControls(false);
    webSettings.setDisplayZoomControls(false);
    webSettings.setSupportZoom(true);
    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    webSettings.setMediaPlaybackRequiresUserGesture(false);
    webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }

    webView.setWebViewClient(new WebViewClientCustom(this, progressBar, offlineLayout));

    CustomWebChromeClient chromeClient = new CustomWebChromeClient(this);
    chromeClient.setFilePathCallback(callback -> filePathCallback = callback);
    webView.setWebChromeClient(chromeClient);

    webView.setDownloadListener(
        (url, userAgent, contentDisposition, mimeType, contentLength) -> {
          // Verificar se downloads estão habilitados via Remote Config
          if (!configManager.areDownloadsEnabled()) {
            Toast.makeText(this, "Downloads desabilitados temporariamente", Toast.LENGTH_SHORT)
                .show();
            return;
          }

          if (checkStoragePermission()) {
            com.carsailms.utils.DownloadUtils.downloadFile(
                MainActivity.this, url, contentDisposition, mimeType);
          } else {
            requestStoragePermission();
          }
        });
  }

  private void setupBottomNavigation() {
    btnBack.setOnClickListener(
        v -> {
          if (webView.canGoBack()) {
            webView.goBack();
            firebaseAnalytics.logEvent("navigation_back", null);
          }
        });

    btnRefresh.setOnClickListener(
        v -> {
          webView.reload();
          firebaseAnalytics.logEvent("navigation_refresh", null);
        });

    btnForward.setOnClickListener(
        v -> {
          if (webView.canGoForward()) {
            webView.goForward();
            firebaseAnalytics.logEvent("navigation_forward", null);
          }
        });

    btnMenu.setOnClickListener(this::showMenu);

    updateNavigationButtons();
  }

  private void showMenu(View view) {
    PopupMenu popup = new PopupMenu(this, view);
    popup.getMenuInflater().inflate(R.menu.bottom_menu, popup.getMenu());

    popup.setOnMenuItemClickListener(
        item -> {
          int id = item.getItemId();
          if (id == R.id.menu_home) {
            String homeUrl = configManager.getHomeUrl();
            webView.loadUrl(homeUrl);
            firebaseAnalytics.logEvent("menu_home_clicked", null);
            return true;
          } else if (id == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            firebaseAnalytics.logEvent("menu_about_clicked", null);
            return true;
          } else if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            firebaseAnalytics.logEvent("menu_settings_clicked", null);
            return true;
          } else if (id == R.id.menu_updates) {
            checkForUpdates();
            firebaseAnalytics.logEvent("menu_updates_clicked", null);
            return true;
          }
          return false;
        });

    popup.show();
  }

  /** Buscar Remote Config e aplicar configurações */
  private void fetchRemoteConfigAndLoad() {
    configManager.fetchConfig(
        new RemoteConfigManager.ConfigListener() {
          @Override
          public void onConfigFetched(boolean updated) {
            Log.d(TAG, "Remote Config atualizado: " + updated);
            applyRemoteConfig();
          }

          @Override
          public void onConfigError(Exception error) {
            Log.e(TAG, "Erro ao buscar Remote Config", error);
            // Usar valores padrão e continuar
            applyRemoteConfig();
          }
        });
  }

  /** Aplicar todas as configurações do Remote Config */
  private void applyRemoteConfig() {
    // 1. VERIFICAR MODO MANUTENÇÃO (prioridade máxima)
    if (configManager.isMaintenanceMode()) {
      configManager.showMaintenanceDialog(this, this::finish);
      firebaseAnalytics.logEvent("maintenance_mode_active", null);
      return;
    }

    // 2. VERIFICAR ATUALIZAÇÃO FORÇADA
    if (configManager.shouldForceUpdate()) {
      configManager.showUpdateDialog(this, true);
      firebaseAnalytics.logEvent("force_update_required", null);
      return;
    }

    // 3. MOSTRAR MENSAGEM DE BOAS-VINDAS
    configManager.showWelcomeMessage(this);

    // 4. APLICAR TEMA/CORES
    applyThemeColors();

    // 5. MOSTRAR BANNER PROMOCIONAL
    if (configManager.shouldShowPromo()) {
      showPromoBanner();
    }

    // 6. VERIFICAR DEEP LINK OU CARREGAR HOME
    handleDeepLink(getIntent());

    // 7. LOG DE TODAS AS CONFIGURAÇÕES (apenas em debug)
    if (BuildConfig.DEBUG) {
      configManager.logAllValues();
    }

    // 8. ANALYTICS - Registrar configurações aplicadas
    Bundle configBundle = new Bundle();
    configBundle.putBoolean("dark_mode_enabled", configManager.isDarkModeEnabled());
    configBundle.putBoolean("downloads_enabled", configManager.areDownloadsEnabled());
    configBundle.putLong("max_file_size", configManager.getMaxFileSizeMB());
    firebaseAnalytics.logEvent("remote_config_applied", configBundle);
  }

  /** Aplicar cores do tema via Remote Config */
  private void applyThemeColors() {
    try {
      RemoteConfigManager.ThemeColors colors = configManager.getThemeColors();

      // Aplicar cores nos elementos da UI
      int primaryColor = Color.parseColor(colors.primary);
      int accentColor = Color.parseColor(colors.accent);

      // Exemplo: aplicar cor na barra de status
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        getWindow().setStatusBarColor(primaryColor);
      }

      // Aplicar em outros elementos conforme necessário
      // toolbar.setBackgroundColor(primaryColor);
      // fab.setBackgroundColor(accentColor);

      Log.d(
          TAG,
          "Tema aplicado - Primary: "
              + colors.primary
              + ", Secondary: "
              + colors.secondary
              + ", Accent: "
              + colors.accent);

    } catch (Exception e) {
      Log.e(TAG, "Erro ao aplicar cores do tema", e);
    }
  }

  /** Mostrar banner promocional */
  private void showPromoBanner() {
    String bannerText = configManager.getPromoBanner();
    String promoUrl = configManager.getPromoUrl();

    if (bannerText.isEmpty() || promoUrl.isEmpty()) {
      return;
    }

    // Se você tiver um TextView para banner no layout, use assim:
    /*
    if (promoBannerView != null) {
        promoBannerView.setText(bannerText);
        promoBannerView.setVisibility(View.VISIBLE);
        promoBannerView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(promoUrl));
            startActivity(intent);
            firebaseAnalytics.logEvent("promo_banner_clicked", null);
        });
    }
    */

    // Ou mostrar como Toast/Snackbar
    Toast.makeText(this, bannerText, Toast.LENGTH_LONG).show();

    Log.d(TAG, "Banner promocional: " + bannerText);
    firebaseAnalytics.logEvent("promo_banner_shown", null);
  }

  private void checkForUpdates() {
    Toast.makeText(this, R.string.checking_updates, Toast.LENGTH_SHORT).show();

    UpdateChecker.checkForUpdates(
        this,
        new UpdateChecker.UpdateListener() {
          @Override
          public void onUpdateAvailable(String latestVersion, String downloadUrl) {
            new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.update_available)
                .setMessage(getString(R.string.update_message, latestVersion))
                .setPositiveButton(
                    R.string.download_update,
                    (dialog, which) -> {
                      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                      startActivity(browserIntent);

                      Bundle bundle = new Bundle();
                      bundle.putString("version", latestVersion);
                      firebaseAnalytics.logEvent("update_download_clicked", bundle);
                    })
                .setNegativeButton(R.string.later, null)
                .show();
          }

          @Override
          public void onNoUpdate() {
            Toast.makeText(MainActivity.this, R.string.no_updates, Toast.LENGTH_SHORT).show();
            firebaseAnalytics.logEvent("update_check_no_update", null);
          }

          @Override
          public void onError(String error) {
            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            firebaseAnalytics.logEvent("update_check_error", null);
          }
        });
  }

  public void updateNavigationButtons() {
    runOnUiThread(
        () -> {
          if (btnBack != null && btnForward != null) {
            btnBack.setEnabled(webView.canGoBack());
            btnForward.setEnabled(webView.canGoForward());
            btnBack.setAlpha(webView.canGoBack() ? 1.0f : 0.5f);
            btnForward.setAlpha(webView.canGoForward() ? 1.0f : 0.5f);
          }
        });
  }

  private void loadWebsite() {
    if (NetworkUtils.isNetworkAvailable(this)) {
      String homeUrl = configManager.getHomeUrl();
      webView.loadUrl(homeUrl);
      offlineLayout.setVisibility(View.GONE);

      Bundle bundle = new Bundle();
      bundle.putString("url", homeUrl);
      firebaseAnalytics.logEvent("website_loaded", bundle);
    } else {
      showOfflineScreen();
    }
  }

  private void handleDeepLink(Intent intent) {
    Uri data = intent.getData();
    if (data != null) {
      String url = data.toString();
      if (NetworkUtils.isNetworkAvailable(this)) {
        webView.loadUrl(url);

        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        firebaseAnalytics.logEvent("deep_link_opened", bundle);

        Log.d(TAG, "Deep link aberto: " + url);
      } else {
        showOfflineScreen();
      }
    } else {
      loadWebsite();
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    handleDeepLink(intent);
  }

  private void showOfflineScreen() {
    offlineLayout.setVisibility(View.VISIBLE);

    // Usar mensagem do Remote Config se disponível
    String offlineMessage = configManager.getString("offline_message");
    if (!offlineMessage.isEmpty()) {
      TextView offlineMsgView = offlineLayout.findViewById(R.string.offline_message);
      if (offlineMsgView != null) {
        offlineMsgView.setText(offlineMessage);
      }
    }

    findViewById(R.id.btnTryAgain)
        .setOnClickListener(
            v -> {
              if (NetworkUtils.isNetworkAvailable(this)) {
                loadWebsite();
                firebaseAnalytics.logEvent("offline_retry_success", null);
              } else {
                Toast.makeText(this, R.string.offline_message, Toast.LENGTH_SHORT).show();
                firebaseAnalytics.logEvent("offline_retry_failed", null);
              }
            });

    findViewById(R.id.btnCloseApp).setOnClickListener(v -> showExitDialog());

    firebaseAnalytics.logEvent("offline_screen_shown", null);
  }

  private void registerLaunchers() {
    // File chooser launcher
    fileChooserLauncher =
        registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
              if (filePathCallback == null) return;

              Uri[] results = null;
              if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                  if (data.getClipData() != null) {
                    // Multiple files
                    int count = data.getClipData().getItemCount();
                    results = new Uri[count];
                    for (int i = 0; i < count; i++) {
                      results[i] = data.getClipData().getItemAt(i).getUri();
                    }

                    Bundle bundle = new Bundle();
                    bundle.putInt("file_count", count);
                    firebaseAnalytics.logEvent("files_selected_multiple", bundle);
                  } else if (data.getData() != null) {
                    // Single file
                    results = new Uri[] {data.getData()};
                    firebaseAnalytics.logEvent("file_selected_single", null);
                  }
                }
              }
              filePathCallback.onReceiveValue(results);
              filePathCallback = null;
            });

    // Permission launcher
    permissionLauncher =
        registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
              boolean allGranted = true;
              for (Boolean granted : permissions.values()) {
                if (!granted) {
                  allGranted = false;
                  break;
                }
              }

              Bundle bundle = new Bundle();
              bundle.putBoolean("all_granted", allGranted);
              firebaseAnalytics.logEvent("permissions_result", bundle);

              if (allGranted) {
                Toast.makeText(this, "Permissões concedidas", Toast.LENGTH_SHORT).show();
              }
            });
  }

  public void openFileChooser(
      ValueCallback<Uri[]> callback, String[] acceptTypes, boolean multipleFiles) {
    filePathCallback = callback;

    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("*/*");

    if (acceptTypes != null && acceptTypes.length > 0) {
      intent.putExtra(Intent.EXTRA_MIME_TYPES, acceptTypes);
    }

    if (multipleFiles) {
      intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
    }

    Intent chooserIntent = Intent.createChooser(intent, getString(R.string.choose_file));

    try {
      fileChooserLauncher.launch(chooserIntent);
      firebaseAnalytics.logEvent("file_chooser_opened", null);
    } catch (Exception e) {
      Toast.makeText(this, "Erro ao abrir seletor de arquivos", Toast.LENGTH_SHORT).show();
      filePathCallback.onReceiveValue(null);
      filePathCallback = null;

      firebaseAnalytics.logEvent("file_chooser_error", null);
    }
  }

  private void checkPermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
          != PackageManager.PERMISSION_GRANTED) {
        permissionLauncher.launch(new String[] {Manifest.permission.POST_NOTIFICATIONS});
      }
    }
  }

  private boolean checkStoragePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      return true;
    }
    return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED;
  }

  private void requestStoragePermission() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
      permissionLauncher.launch(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (webView.canGoBack()) {
        webView.goBack();
        return true;
      } else {
        showExitDialog();
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  private void showExitDialog() {
    new AlertDialog.Builder(this)
        .setTitle(R.string.exit_app)
        .setMessage(R.string.exit_message)
        .setPositiveButton(
            R.string.yes,
            (dialog, which) -> {
              Bundle bundle = new Bundle();
              bundle.putString("exit_method", "dialog");
              firebaseAnalytics.logEvent("app_exited", bundle);
              finish();
            })
        .setNegativeButton(R.string.no, null)
        .show();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (webView != null) {
      webView.onResume();
    }
    firebaseAnalytics.logEvent("app_resumed", null);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (webView != null) {
      webView.onPause();
    }
    firebaseAnalytics.logEvent("app_paused", null);
  }

  @Override
  protected void onDestroy() {
    if (webView != null) {
      webView.destroy();
    }
    super.onDestroy();
  }
}
