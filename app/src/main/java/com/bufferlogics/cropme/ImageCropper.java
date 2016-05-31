package com.bufferlogics.cropme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import com.fenchtose.nocropper.CropperView;
import java.io.IOException;

/**
 * Created by Ahsanwarsi on 31/05/16.
 * Copyright (c) 2016 Bufferlogics. All rights reserved.
 */

public class ImageCropper extends ActionBarActivity {
    private CropperView cropperView;
    private Bitmap mBitmap;
    private boolean isSnappedToCenter = false;

    private String imagePath = null;
    private Utility utility;
    private Context ctx;
    private Activity activity;
    private static String TAG = ImageCropper.class.getSimpleName();
    private Toolbar mToolbar;
    private TextView mTitle;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        ctx = ImageCropper.this;
        utility =  new Utility();
        cropperView = (CropperView) findViewById(R.id.imageview);

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);

        //Show back button
        mToolbar.setNavigationIcon(R.drawable.back_arrow_ic);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Set title
        mTitle.setText(getString(R.string.cropper_title));

        imagePath = getIntent().getStringExtra(Config.INTENT_KEY_PIC);
        loadNewImage(imagePath);
    }

    //Load new image
    private void loadNewImage(String filePath) {
        mBitmap = BitmapFactory.decodeFile(filePath);

        int maxP = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
        float scale1280 = (float)maxP / 1280;

        if (cropperView.getWidth() != 0) {
            cropperView.setMaxZoom(cropperView.getWidth() * 2 / 1280f);
        } else {

            ViewTreeObserver vto = cropperView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    cropperView.getViewTreeObserver().removeOnPreDrawListener(this);
                    cropperView.setMaxZoom(cropperView.getWidth() * 2 / 1280f);
                    return true;
                }
            });

        }

        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (mBitmap.getWidth() / scale1280),
                (int) (mBitmap.getHeight() / scale1280), true);
        cropperView.setImageBitmap(mBitmap);
    }


    public void onImageCropClicked(View v) {
        cropImage();
    }

    public void onImageRotateClicked(View v) {
        rotateImage();
    }

    public void onImageSnapClicked(View v) {
        snapImage();
    }

    //Crop image
    private void cropImage() {
        Bitmap bitmap = cropperView.getCroppedBitmap();
        if (bitmap != null) {
            try {
                String croppedImagePath = utility.getImagePath(bitmap, 100);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Config.INTENT_KEY_CROPPED_IMAGE ,croppedImagePath);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Rotate image
    private void rotateImage() {
        if (mBitmap == null) {
            return;
        }

        mBitmap = BitmapUtils.rotateBitmap(mBitmap, 90);
        cropperView.setImageBitmap(mBitmap);
    }

    //Scale image
    private void snapImage() {
        if (isSnappedToCenter) {
            cropperView.cropToCenter();
        } else {
            cropperView.fitToCenter();
        }

        isSnappedToCenter = !isSnappedToCenter;
    }

    /********
     * Menu
     ********/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}