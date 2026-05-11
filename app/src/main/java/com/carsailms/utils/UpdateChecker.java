package com.carsailms.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateChecker {

    private static final String GITHUB_API = "https://api.github.com/repos/carsaimz/carsailms/releases/latest";
    private static final String FALLBACK_URL = "https://github.com/carsaimz/carsailms/releases/latest";

    public interface UpdateListener {
        void onUpdateAvailable(String latestVersion, String downloadUrl);
        void onNoUpdate();
        void onError(String error);
    }

    public static void checkForUpdates(Context context, UpdateListener listener) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String currentVersion = getCurrentVersion(context);

                URL url = new URL(GITHUB_API);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    handler.post(() -> listener.onError("Erro de conexão: " + responseCode));
                    connection.disconnect();
                    return;
                }

                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();

                JSONObject json = new JSONObject(response.toString());
                String latestVersion = json.getString("tag_name").replace("v", "");
                String downloadUrl = FALLBACK_URL;

                if (json.has("assets")) {
                    JSONArray assets = json.getJSONArray("assets");
                    if (assets.length() > 0) {
                        JSONObject asset = assets.getJSONObject(0);
                        if (asset.has("browser_download_url") && 
                            !asset.getString("browser_download_url").isEmpty()) {
                            downloadUrl = asset.getString("browser_download_url");
                        }
                    }
                }

                if (downloadUrl.equals(FALLBACK_URL) && json.has("html_url")) {
                    downloadUrl = json.getString("html_url");
                }

                final String finalDownloadUrl = downloadUrl;

                if (compareVersions(latestVersion, currentVersion) > 0) {
                    handler.post(() -> listener.onUpdateAvailable(latestVersion, finalDownloadUrl));
                } else {
                    handler.post(listener::onNoUpdate);
                }

            } catch (Exception e) {
                handler.post(() -> listener.onError("Erro ao verificar atualizações: " + e.getMessage()));
                e.printStackTrace();
            }
        });
    }

    private static String getCurrentVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager()
                .getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName != null ? pInfo.versionName : "1.0.0";
        } catch (Exception e) {
            return "1.0.0";
        }
    }

    private static int compareVersions(String version1, String version2) {
        try {
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
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}