package com.uir.lostfound.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageHelper {

    /**
     * Saves a Bitmap to the app's internal storage.
     * Returns the absolute file path, or null if saving failed.
     */
    public static String saveImageToInternalStorage(Context context, Bitmap bitmap) {
        String fileName = "IMG_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                        .format(new Date()) + ".jpg";

        File directory = context.getFilesDir(); // internal storage, private to app
        File file = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads a Bitmap from an absolute file path.
     * Returns null if the file doesn't exist.
     */
    public static Bitmap loadImageFromPath(String path) {
        if (path == null || path.isEmpty()) return null;
        File file = new File(path);
        if (!file.exists()) return null;
        return BitmapFactory.decodeFile(path);
    }

    /**
     * Creates a temp image file for the camera intent URI.
     * Returns the File object.
     */
    public static File createImageFile(Context context) throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                        .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getFilesDir();
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}
