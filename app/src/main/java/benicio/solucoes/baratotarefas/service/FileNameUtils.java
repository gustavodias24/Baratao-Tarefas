package benicio.solucoes.baratotarefas.service;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.util.UUID;

public class FileNameUtils {

    public static String getFileName(Uri uri, Context c) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = c.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(displayNameIndex);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String truncateFileName(String fileName, int maxLength) {
        if (fileName.length() > maxLength) {
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            String truncatedName = fileName.substring(0, maxLength - 3) + "..." + extension;
            return truncatedName;
        } else {
            return fileName;
        }
    }

    public static String fileNameForDb(String fileName){
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        String nome = UUID.randomUUID().toString();
        return nome + extension;
    }
}
