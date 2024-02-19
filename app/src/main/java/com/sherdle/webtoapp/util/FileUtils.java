package com.sherdle.webtoapp.util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static String getRealPathFromURI(Context context, Uri uri) throws IOException {
        File file = createTemporalFileFromUri(context, uri);
        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }

    private static File createTemporalFileFromUri(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            return null;
        }

        File tempFile = File.createTempFile("temp_sound", null, context.getExternalFilesDir(Environment.DIRECTORY_MUSIC));
        tempFile.deleteOnExit();

        FileOutputStream out = new FileOutputStream(tempFile);
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
        out.close();
        inputStream.close();
        return tempFile;
    }
}

