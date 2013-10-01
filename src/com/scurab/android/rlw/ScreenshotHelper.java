package com.scurab.android.rlw;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * User: Joe Scurab
 * Date: 01/10/13
 * Time: 20:58
 */
public class ScreenshotHelper {

    public static int TEXT_OFFSET = 25;
    public static final int DEFAULT_JPEG_QUALITY = 85;

    /**
     *
     * @param view
     * @return null if any problem
     */
    public static Bitmap saveScreenshot(View view){
        return saveScreenshot(view, DEFAULT_JPEG_QUALITY);
    }

    /**
     *
     * @param view view to save
     * @param compress jpeg compression (85 by default)
     * @return null if any problem
     */
    public static Bitmap saveScreenshot(View view, int compress){
        if(view == null){
            throw new IllegalArgumentException("View is null!");
        }

        // prepare view
        view.destroyDrawingCache();
        view.buildDrawingCache(false);

        // get bitmap
        Bitmap b = view.getDrawingCache();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // save it
        if(!b.compress(Bitmap.CompressFormat.JPEG, compress, baos)){
            b = null;
        }
        return b;
    }

    /**
     * Write some text into this image
     * @param bitmap
     * @param text
     * @return
     */
    public static Bitmap writeText(Bitmap bitmap, String text){
        Canvas canvas = new Canvas(bitmap);
        Paint p = createPaint();
        canvas.drawText(text, TEXT_OFFSET,TEXT_OFFSET, p);
        return bitmap;
    }

    /**
     * Save bitmap into sdcard (must have permission!) and send gallery update broadcast
     * @param context
     * @param bitmap
     * @throws FileNotFoundException
     */
    public static void saveBitmapToPhoneGallery(Context context, Bitmap bitmap, String title, String description) throws Exception {
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title , description);
    }

    /**
     * Save Bitmap into jpeg and convert it into byte array
     *
     * @param bitmap
     * @return
     */
    public static byte[] saveBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, DEFAULT_JPEG_QUALITY, baos);
        return baos.toByteArray();
    }

    private static Paint createPaint(){
        Paint p = new Paint();
        p.setAlpha(255 >> 1);
        p.setAntiAlias(true);
        p.setTextSize(24);
        p.setColor(Color.WHITE);
        p.setFakeBoldText(false);
        p.setShadowLayer(2, 0, 0, Color.BLACK);
        return p;
    }
}
