package com.carsailms.widgets;

import android.net.Uri;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.carsailms.MainActivity;
import com.carsailms.R;

public class CustomWebChromeClient extends WebChromeClient {
    
    private final MainActivity activity;
    private View customView;
    private CustomViewCallback customViewCallback;
    private FrameLayout fullscreenContainer;
    private ValueCallbackHandler filePathCallbackHandler;
    
    public interface ValueCallbackHandler {
        void onCallback(ValueCallback<Uri[]> callback);
    }
    
    public CustomWebChromeClient(MainActivity activity) {
        this.activity = activity;
    }
    
    public void setFilePathCallback(ValueCallbackHandler handler) {
        this.filePathCallbackHandler = handler;
    }
    
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        // Pode adicionar lógica de progress aqui se necessário
    }
    
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                    FileChooserParams fileChooserParams) {
        if (filePathCallbackHandler != null) {
            filePathCallbackHandler.onCallback(filePathCallback);
        }
        
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        boolean allowMultiple = fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE;
        
        activity.openFileChooser(filePathCallback, acceptTypes, allowMultiple);
        return true;
    }
    
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (customView != null) {
            onHideCustomView();
            return;
        }
        
        customView = view;
        customViewCallback = callback;
        
        activity.getWindow().getDecorView()
            .setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        
        fullscreenContainer = new FrameLayout(activity);
        fullscreenContainer.addView(customView);
        activity.setContentView(fullscreenContainer);
    }
    
    @Override
    public void onHideCustomView() {
        if (customView == null) {
            return;
        }
        
        activity.getWindow().getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        
        if (fullscreenContainer != null) {
            fullscreenContainer.removeView(customView);
        }
        
        customView = null;
        if (customViewCallback != null) {
            customViewCallback.onCustomViewHidden();
        }
        
        activity.setContentView(R.layout.activity_main);
    }
}
