package com.carsailms.utils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.carsailms.MainActivity;

public class WebViewClientCustom extends WebViewClient {
    
    private final MainActivity activity;
    private final FrameLayout progressBar;
    private final View offlineLayout;
    
    public WebViewClientCustom(MainActivity activity, FrameLayout progressBar, View offlineLayout) {
        this.activity = activity;
        this.progressBar = progressBar;
        this.offlineLayout = offlineLayout;
    }
    
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        progressBar.setVisibility(View.VISIBLE);
        offlineLayout.setVisibility(View.GONE);
    }
    
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressBar.setVisibility(View.GONE);
        activity.updateNavigationButtons();
    }
    
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        if (request.isForMainFrame()) {
            progressBar.setVisibility(View.GONE);
            offlineLayout.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        
        if (url.contains("carsailms") || url.contains("lms") || url.contains("carsai")) {
            return false;
        }
        
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            
            if (url.contains("whatsapp.com") || url.contains("wa.me")) {
                intent.setPackage("com.whatsapp");
            }
            else if (url.contains("drive.google.com") || url.contains("docs.google.com")) {
                intent.setPackage("com.google.android.apps.docs");
            }
            else if (url.contains("youtube.com") || url.contains("youtu.be")) {
                intent.setPackage("com.google.android.youtube");
            }
            
            activity.startActivity(intent);
            return true;
            
        } catch (ActivityNotFoundException e) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(browserIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Erro ao abrir link", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
