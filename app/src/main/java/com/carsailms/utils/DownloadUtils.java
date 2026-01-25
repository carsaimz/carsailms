package com.carsailms.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.URLUtil;
import android.widget.Toast;
import com.carsailms.R;

import java.io.File;

public class DownloadUtils {
    
    private static final String DOWNLOAD_FOLDER = "Carsai LMS";
    
    public static void downloadFile(Context context, String url, String contentDisposition, String mimeType) {
        try {
            String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
            
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType(mimeType);
            request.addRequestHeader("User-Agent", "CarsaiLMS/1.0");
            request.setDescription(context.getString(R.string.downloading));
            request.setTitle(fileName);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            
            File downloadDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                DOWNLOAD_FOLDER
            );
            
            if (!downloadDir.exists()) {
                downloadDir.mkdirs();
            }
            
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS + "/" + DOWNLOAD_FOLDER,
                fileName
            );
            
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                downloadManager.enqueue(request);
                Toast.makeText(context, context.getString(R.string.downloading) + " " + fileName, 
                    Toast.LENGTH_SHORT).show();
            }
            
        } catch (Exception e) {
            Toast.makeText(context, R.string.download_failed, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
