package com.carsailms.widgets;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
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
    private View originalContentView;
    
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
        
        // Salvar referência do conteúdo original
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        originalContentView = decorView.getChildAt(0);
        
        // Criar container fullscreen
        fullscreenContainer = new FrameLayout(activity);
        fullscreenContainer.setBackgroundColor(android.graphics.Color.BLACK);
        fullscreenContainer.addView(customView, new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        
        // Esconder conteúdo original
        if (originalContentView != null) {
            originalContentView.setVisibility(View.GONE);
        }
        
        // Esconder barra de navegação
        View bottomNav = activity.findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.GONE);
        }
        
        // Adicionar container fullscreen
        decorView.addView(fullscreenContainer, new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        
        // Configurar UI imersiva
        activity.getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }
    
    @Override
    public void onHideCustomView() {
        if (customView == null) {
            return;
        }
        
        // Remover view customizada
        if (fullscreenContainer != null) {
            fullscreenContainer.removeView(customView);
        }
        
        // Notificar callback
        if (customViewCallback != null) {
            customViewCallback.onCustomViewHidden();
        }
        customViewCallback = null;
        
        // Remover container fullscreen
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        if (fullscreenContainer != null) {
            decorView.removeView(fullscreenContainer);
        }
        
        // Restaurar conteúdo original
        if (originalContentView != null) {
            originalContentView.setVisibility(View.VISIBLE);
        }
        
        // Restaurar barra de navegação
        View bottomNav = activity.findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }
        
        // Restaurar UI do sistema
        activity.getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_VISIBLE
        );
        
        customView = null;
        fullscreenContainer = null;
    }
}
