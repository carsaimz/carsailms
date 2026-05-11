package com.carsailms.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class UpdateDownloader {

    /**
     * Abre o navegador com o URL de download
     */
    public static void openInBrowser(Context context, String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(context, "URL inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Fallback: tentar abrir a página principal
            try {
                Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, 
                    Uri.parse("https://github.com/carsaimz/carsailms/releases/latest"));
                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(fallbackIntent);
            } catch (Exception ex) {
                Toast.makeText(context, 
                    "Nenhum navegador encontrado no dispositivo", 
                    Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, 
                "Erro ao abrir navegador: " + e.getMessage(), 
                Toast.LENGTH_SHORT).show();
        }
    }
}