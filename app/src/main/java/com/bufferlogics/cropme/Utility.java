package com.bufferlogics.cropme;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Ahsanwarsi on 31/05/16.
 * Copyright (c) 2016 BufferLogics. All rights reserved.
 */
public class Utility {

    public Utility() {
        //
    }

    //Save image bitmaps and get image path
    public String getImagePath(Bitmap bitmap, int quality) throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+Config.DEFAULT_IMAGE_DIRECTORY);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath()+Config.DEFAULT_IMAGE_DIRECTORY + Config.DEFAULT_IMAGE_NAME;
        File f = new File(imagePath);
        f.createNewFile();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return imagePath;
    }


    /**
     * Image Path
     **/
    public String getImagePath(Uri selectedImage, Context ctx) {
        String filePath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor =
                ctx.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
}
