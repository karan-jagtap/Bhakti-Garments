package com.clothing.bhaktigarments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.clothing.bhaktigarments.activities.DashboardActivity;
import com.clothing.bhaktigarments.activities.ShopActivity;
import com.clothing.bhaktigarments.config.AppConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private Button scanBTN, generateBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startActivity(new Intent(MainActivity.this, DashboardActivity.class));
        finish();
        Toast.makeText(this, AppConfig.APP_NAME, Toast.LENGTH_SHORT).show();

        scanBTN = findViewById(R.id.button);
        generateBTN = findViewById(R.id.button2);

        scanBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setPrompt("Align the QR code inside the box");
                integrator.setOrientationLocked(true);
                integrator.initiateScan();
            }
        });

        generateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermission();
            }
        });
    }

    @AfterPermissionGranted(29)
    private void getPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            generateAndSaveQRCode();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This is this",
                    29, perms);
        }
    }

    private void generateAndSaveQRCode() {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap("Bhakti Garments", BarcodeFormat.QR_CODE, 400, 400);
                    /*ImageView imageViewQrCode = (ImageView) findViewById(R.id.qrCode);
                    imageViewQrCode.setImageBitmap(bitmap);*/
            Log.i(AppConfig.APP_NAME, "Current Build version :: " + Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT >= 29) {
                // TODO :: handle lower SDK versions
                ContentValues cv = new ContentValues();
                cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                cv.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                cv.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Bhakti Garments");
                cv.put(MediaStore.Images.Media.IS_PENDING, true);
                Uri uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                if (uri != null) {
                    OutputStream os = this.getContentResolver().openOutputStream(uri);
                    if (os != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                        os.flush();
                        os.close();
                    } else {
                        Toast.makeText(this, "Output steam NULL", Toast.LENGTH_SHORT).show();
                    }
                    cv.put(MediaStore.Images.Media.IS_PENDING, false);
                    this.getContentResolver().update(uri, cv, null, null);
                } else {
                    Toast.makeText(this, "null uri", Toast.LENGTH_SHORT).show();
                }
            } else {
                String appDirectoryPath = Environment.getExternalStorageDirectory().toString() + "/Bhakti Garments";
                File appDirectory = new File(appDirectoryPath);
                if (!appDirectory.exists()) {
                    if (appDirectory.mkdir()) {
                        // call to save image
                        saveImage(bitmap, appDirectory);
                    } else {
                        Log.i(AppConfig.APP_NAME, "Something went wrong while creating directory at :\n" + appDirectoryPath);
                    }
                } else {
                    // call to save image
                    saveImage(bitmap, appDirectory);
                }
            }
        } catch (Exception e) {
            Log.i(AppConfig.APP_NAME, "generateAndSaveQRCode() exception :: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void saveImage(Bitmap bitmap, File appDirectory) throws Exception {
        String fileName = AppConfig.SHOP_NAME_SAMPLE + ".png";
        File file = new File(appDirectory, fileName);
        if (!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            MediaScannerConnection.scanFile(MainActivity.this, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Log.i(AppConfig.APP_NAME, "Path :: " + path);
                    Log.i(AppConfig.APP_NAME, "URI :: " + uri.getPath());
                }
            });
        } else {
            Log.i(AppConfig.APP_NAME, "Image with " + AppConfig.SHOP_NAME_SAMPLE + " already exists.");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                Log.i(AppConfig.APP_NAME, "cancelled");
            } else {
                Log.i(AppConfig.APP_NAME, "Scanned :: " + result.getContents());
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        generateAndSaveQRCode();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Cannot Save QR Code", Toast.LENGTH_SHORT).show();
    }
}