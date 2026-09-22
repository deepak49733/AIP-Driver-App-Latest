package com.sc.aipdriver.activities.otherclasses;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {
    // Compress to WebP under 100 KB
    public static String getPathFromUri(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }
    public static File compressImageFile(Context context, File originalFile) {
        try {
            if (!originalFile.exists() || !originalFile.canRead()) {
                Log.e("Compress", "File not found or unreadable: " + originalFile.getAbsolutePath());
                return originalFile;
            }

            // Decode with inSampleSize for memory efficiency (e.g. max 1280px)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(originalFile.getAbsolutePath(), options);

            int maxDim = 1280;
            int scale = 1;
            while (options.outWidth / scale > maxDim || options.outHeight / scale > maxDim) {
                scale *= 2;
            }

            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(originalFile.getAbsolutePath(), options);



            if (bitmap == null) {
                Log.e("Compress", "Bitmap decode failed");
                return originalFile;
            }

            // ✅ Fix orientation before compression
            bitmap = rotateImageIfRequired(bitmap, originalFile.getAbsolutePath());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int quality = 100;

            // Always use JPEG for better server/email compatibility
            Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;

            bitmap.compress(format, quality, out);

            // Reduce quality loop
            while (out.toByteArray().length / 1024 > 200 && quality > 10) { // Increased to 200KB for better quality
                out.reset();
                quality -= 5;
                bitmap.compress(format, quality, out);
            }

            // Save compressed file
            String ext = ".jpg";
            File compressedFile = new File(context.getCacheDir(), "compressed_" + System.currentTimeMillis() + ext);
            FileOutputStream fos = new FileOutputStream(compressedFile);
            fos.write(out.toByteArray());
            fos.flush();
            fos.close();

            return compressedFile;
        } catch (Exception e) {
            e.printStackTrace();
            return originalFile;
        }
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, String path) throws IOException {
        ExifInterface ei = new ExifInterface(path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


}
