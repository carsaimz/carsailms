package com.carsailms;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class AboutActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.about);
        }
        
        displayVersionInfo();
    }
    
    private void displayVersionInfo() {
        TextView tvVersion = findViewById(R.id.tvVersion);
        TextView tvChangelog = findViewById(R.id.tvChangelog);
        
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;
            int versionCode = pInfo.versionCode;
            
            tvVersion.setText(getString(R.string.version) + " " + versionName + " (Build " + versionCode + ")");
            
            String changelog = "v1.0.1 (Build 2)\n" +
                "• Corrigido upload de arquivos\n" +
                "• Adicionado menu de navegação\n" +
                "• Implementada verificação de atualizações\n" +
                "• Melhorado Firebase Analytics e Messaging\n" +
                "• Corrigido deep linking com URLs específicas\n" +
                "• Adicionada tela Sobre\n\n" +
                "v1.0.0 (Build 1)\n" +
                "• Versão inicial\n" +
                "• WebView integrado\n" +
                "• Sistema de downloads\n" +
                "• Modo escuro/claro\n" +
                "• Suporte multilíngue (PT/EN)";
            
            tvChangelog.setText(changelog);
            
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
