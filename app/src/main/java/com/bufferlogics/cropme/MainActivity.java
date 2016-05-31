package com.bufferlogics.cropme;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Ahsanwarsi on 31/05/16.
 * Copyright (c) 2016 Bufferlogics. All rights reserved.
 */

public class MainActivity extends Activity {

    private Context ctx;
    private View view;
    private Utility utility;

    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST = 1888;
    private final int PIC_CROP = 1;
    private final int PERMISSION_ALL = 199;

    private Uri imageUri = null;
    private String imagePath = "";

    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = MainActivity.this;
        view = getCurrentFocus();
        utility = new Utility();


    }

    //
    public void onChooseClick(View view) {
        if(hasPermissions(this, PERMISSIONS)){
            showInputOptions();
        }else{
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    /*****
     * Image selection options
     *****/
    public void showInputOptions() {
        final CharSequence options[] = new CharSequence[]{ctx.getResources().getString(R.string.image_selection_camera), ctx.getResources().getString(R.string.image_selection_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                switch (index) {
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                    default:
                        openGallery();
                        break;
                }
            }
        });
        builder.show();
    }

    //Gallery image selection
    public void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    //Camera Capturing
    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, R.string.app_name);
        values.put(MediaStore.Images.Media.DESCRIPTION, ctx.getPackageName());
        imageUri = ctx.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    imagePath = utility.getImagePath(selectedImage, ctx);
                    try {
                        startCropping(imagePath);
                    } catch (Exception e) {
                    }
                }
                break;
            case CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        imagePath = utility.getImagePath(imageUri, ctx);
                        startCropping(imagePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case PIC_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    String croppedImage = imageReturnedIntent.getStringExtra(Config.INTENT_KEY_CROPPED_IMAGE);
                    setImage(croppedImage);
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //
                }
                break;
        }
    }

    /*********
     * Image Cropping
     ***********/
    private void startCropping(String imagePath){
        Intent intent = new Intent(ctx, ImageCropper.class);
        intent.putExtra(Config.INTENT_KEY_PIC, imagePath);
        startActivityForResult(intent, PIC_CROP);
    }

    /*********
     Set Image
     ***********/
    private void setImage(String imagePath) {
        ((ImageView) findViewById(R.id.img_view)).setImageBitmap(BitmapFactory.decodeFile(imagePath));

    }


    /****
     Permissions
     ****/
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showInputOptions();
                } else {
                    Toast.makeText(ctx, "You don't have permission to access Camera and Storage. Please grant permissions to proceed.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}