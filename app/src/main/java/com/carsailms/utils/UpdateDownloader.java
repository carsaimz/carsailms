package com.carsailms.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class UpdateDownloader {

    private static final String FALLBACK_URL = "https://github.com/carsaimz/carsailms/releases/latest";

    /**
     * Abre o navegador com o URL de download fornecido.
     * Se o URL for inválido ou ocorrer erro, tenta abrir a página principal do GitHub.
     *
     * @param context Contexto da aplicação
     * @param url     URL para download (pode ser nulo ou vazio)
     */
    public static void openInBrowser(Context context, String url) {
        // Garantir URL válida
        if (url == null || url.isEmpty()) {
            url = FALLBACK_URL;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Fallback: tentar URL principal
            try {
                Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(FALLBACK_URL));
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
                Toast.LENGTH_LONG).show();
        }
    }
}