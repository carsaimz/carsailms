package com.carsailms;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.carsailms.R;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.carsailms.utils.NetworkUtils;
import com.carsailms.utils.PreferenceManager;
import com.carsailms.utils.UpdateChecker;
import com.carsailms.utils.WebViewClientCustom;
import com.carsailms.widgets.CustomWebChromeClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {
    
    private static final String BASE_URL = "https://carsailms.linkpc.net";
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    private WebView webView;
    private FrameLayout progressBar;
    private View offlineLayout;
    private ImageButton btnBack, btnRefresh, btnForward, btnMenu;
    
    private PreferenceManager preferenceManager;
    private FirebaseAnalytics firebaseAnalytics;
    private FirebaseRemoteConfig remoteConfig;
    
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
        
        // Verificar se foi aberto via deep link
        handleDeepLink(getIntent());
    }
    
    private void initFirebase() {
        // Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("screen", "MainActivity");
        firebaseAnalytics.logEvent("app_opened", bundle);
        
        // Remote Config
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        
        // Fetch remote config
        remoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Config atualizado
            }
        });
        
        // Firebase Messaging
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    // Salvar token se necessário
                }
            });
        
        // Subscribe to topics
        FirebaseMessaging.getInstance().subscribeToTopic("general");
        FirebaseMessaging.getInstance().subscribeToTopic("updates");
    }
    
    private void initViews() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        offlineLayout = findViewById(R.id.offlineLayout);
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnForward = findViewById(R.id.btnForward);
        btnMenu = findViewById(R.id.btnMenu);
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
        
        webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            if (checkStoragePermission()) {
                com.carsailms.utils.DownloadUtils.downloadFile(
                    MainActivity.this, url, contentDisposition, mimeType
                );
            } else {
                requestStoragePermission();
            }
        });
    }
    
    private void setupBottomNavigation() {
        btnBack.setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });
        
        btnRefresh.setOnClickListener(v -> webView.reload());
        
        btnForward.setOnClickListener(v -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });
        
        btnMenu.setOnClickListener(this::showMenu);
        
        updateNavigationButtons();
    }
    
    private void showMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.bottom_menu, popup.getMenu());
        
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                webView.loadUrl(BASE_URL);
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
    
    private void checkForUpdates() {
    Toast.makeText(this, R.string.checking_updates, Toast.LENGTH_SHORT).show();

        UpdateChecker.checkForUpdates(this, new UpdateChecker.UpdateListener() {
            @Override
            public void onUpdateAvailable(String latestVersion, String downloadUrl) {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.update_available)
                    .setMessage(getString(R.string.update_message, latestVersion))
                    .setPositiveButton(R.string.download_update, (dialog, which) -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                        startActivity(browserIntent);
                    })
                    .setNegativeButton(R.string.later, null)
                    .show();
            }
            
            @Override
            public void onNoUpdate() {
                Toast.makeText(MainActivity.this, R.string.no_updates, Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    public void updateNavigationButtons() {
        runOnUiThread(() -> {
            btnBack.setEnabled(webView.canGoBack());
            btnForward.setEnabled(webView.canGoForward());
            btnBack.setAlpha(webView.canGoBack() ? 1.0f : 0.5f);
            btnForward.setAlpha(webView.canGoForward() ? 1.0f : 0.5f);
        });
    }
    
    private void loadWebsite() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            webView.loadUrl(BASE_URL);
            offlineLayout.setVisibility(View.GONE);
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
                firebaseAnalytics.logEvent("deep_link_opened", new Bundle());
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
        handleDeepLink(intent);
    }
    
    private void showOfflineScreen() {
        offlineLayout.setVisibility(View.VISIBLE);
        
        findViewById(R.id.btnTryAgain).setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(this)) {
                loadWebsite();
            } else {
                Toast.makeText(this, R.string.offline_message, Toast.LENGTH_SHORT).show();
            }
        });
        
        findViewById(R.id.btnCloseApp).setOnClickListener(v -> showExitDialog());
    }
    
    private void registerLaunchers() {
        // File chooser launcher
        fileChooserLauncher = registerForActivityResult(
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
                        } else if (data.getData() != null) {
                            // Single file
                            results = new Uri[]{data.getData()};
                        }
                    }
                }
                filePathCallback.onReceiveValue(results);
                filePathCallback = null;
            }
        );
        
        // Permission launcher
        permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allGranted = true;
                for (Boolean granted : permissions.values()) {
                    if (!granted) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    Toast.makeText(this, "Permissões concedidas", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    
    public void openFileChooser(ValueCallback<Uri[]> callback, String[] acceptTypes, boolean multipleFiles) {
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
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao abrir seletor de arquivos", Toast.LENGTH_SHORT).show();
            filePathCallback.onReceiveValue(null);
            filePathCallback = null;
        }
    }
    
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(new String[]{Manifest.permission.POST_NOTIFICATIONS});
            }
        }
    }
    
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }
        return ContextCompat.checkSelfPermission(this, 
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
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
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                firebaseAnalytics.logEvent("app_exited", null);
                finish();
            })
            .setNegativeButton(R.string.no, null)
            .show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }
    
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
