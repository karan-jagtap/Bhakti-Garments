package com.clothing.bhaktigarments.activities;

import android.Manifest;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.clothing.bhaktigarments.R;
import com.clothing.bhaktigarments.classes.Shop;
import com.clothing.bhaktigarments.config.AppConfig;
import com.clothing.bhaktigarments.helpers.LocalDatabase;
import com.clothing.bhaktigarments.helpers.ResponseHandler;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class RegisterShopActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    // Components
    private EditText shopNameED;
    private Button saveBtn;
    private ImageView imageView;
    private TextView imageNameTV, messageTV;

    // Data
    private Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_shop);
        Toolbar toolbar = findViewById(R.id.toolbar_RegisterShopActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        declarations();
        listeners();
    }

    private void declarations() {
        shopNameED = findViewById(R.id.editText_name_RegisterShopActivity);
        saveBtn = findViewById(R.id.save_RegisterShopActivity);
        imageView = findViewById(R.id.imageView_RegisterShopActivity); // default : hidden
        imageNameTV = findViewById(R.id.textView_imageName_RegisterShopActivity); // default : hidden
        messageTV = findViewById(R.id.textView_message_RegisterShopActivity); // default : hidden

        shop = new Shop();
    }

    private void listeners() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shop.setName(shopNameED.getText().toString().trim());
                if (!shop.getName().isEmpty()) {
                    getPermission();
                } else {
                    Toast.makeText(RegisterShopActivity.this, "Please Enter Shop Name", Toast.LENGTH_SHORT).show();
                }
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
            shop.setUid(UUID.randomUUID().toString());
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(shop.toString(), BarcodeFormat.QR_CODE, 400, 400);
            imageView.setImageBitmap(bitmap);
            Log.i(AppConfig.APP_NAME, "Current Build version :: " + Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT >= 29) {
                if (addDataToLocalDB()) {
                    saveImage(bitmap);
                }
            } else {
                String appDirectoryPath = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_PICTURES + "/Bhakti Garments";
                File appDirectory = new File(appDirectoryPath);
                if (!appDirectory.exists()) {
                    if (appDirectory.mkdir()) {
                        // call to save image
                        if (addDataToLocalDB()) {
                            saveImage(bitmap, appDirectory);
                        }
                    } else {
                        Log.i(AppConfig.APP_NAME, "Something went wrong while creating directory at :\n" + appDirectoryPath);
                    }
                } else {
                    Log.i(AppConfig.APP_NAME, appDirectory.toString() + " already exists");
                    // call to save image
                    if (addDataToLocalDB()) {
                        saveImage(bitmap, appDirectory);
                    }
                }
            }
        } catch (Exception e) {
            Log.i(AppConfig.APP_NAME, "generateAndSaveQRCode() exception :: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveImage(Bitmap bitmap) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            cv.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            cv.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Bhakti Garments");
            cv.put(MediaStore.Images.Media.DISPLAY_NAME, shop.getName() + ".png");
            cv.put(MediaStore.Images.Media.TITLE, shop.getName() + ".png");
            cv.put(MediaStore.Images.Media.IS_PENDING, true);
            Uri uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            if (uri != null) {
                OutputStream os = this.getContentResolver().openOutputStream(uri);
                if (os != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                    os.close();
                    imageNameTV.setText(uri.toString());
                    showSuccessComponents();
                } else {
                    Toast.makeText(this, "Output steam NULL", Toast.LENGTH_SHORT).show();
                }
                this.getContentResolver().update(uri, cv, null, null);
            } else {
                Toast.makeText(this, "null uri", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.i(AppConfig.APP_NAME, "saveImage() exception :: " + e.getMessage());
        }
    }

    private void saveImage(Bitmap bitmap, File appDirectory) throws Exception {
        String fileName = shop.getName() + ".png";
        File file = new File(appDirectory, fileName);
        if (!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            imageNameTV.setText(fileName);
            showSuccessComponents();
            MediaScannerConnection.scanFile(RegisterShopActivity.this,
                    new String[]{file.toString()}, new String[]{"image/png"},
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i(AppConfig.APP_NAME, "Path :: " + path);
                            Log.i(AppConfig.APP_NAME, "URI :: " + uri.getPath());
                        }
                    });
        } else {
            Log.i(AppConfig.APP_NAME, "Image with " + shop.getName() + " already exists.");
            showErrorComponents();
            messageTV.setText(new ResponseHandler().getErrorMessage(1).replace("{}", shop.getName()));
            messageTV.setTextColor(Color.RED);
        }
    }

    private boolean addDataToLocalDB() {
        LocalDatabase db = new LocalDatabase(RegisterShopActivity.this);
        ResponseHandler response = db.registerShop(shop);
        if (response.getErrorCode() == 0) {
            showSuccessComponents();
            shopNameED.setText("");
            messageTV.setTextColor(getColor(R.color.blue_medium));
            return true;
        } else if (response.getErrorCode() == 1) {
            showErrorComponents();
            messageTV.setText(response.getErrorMessage(1).replace("{}", shop.getName()));
            messageTV.setTextColor(Color.RED);
        }
        Log.i(AppConfig.APP_NAME, "DB error : " + response.getErrorMessage(response.getErrorCode()));
        return false;
    }

    private void showErrorComponents() {
        imageView.setVisibility(View.GONE);
        imageNameTV.setVisibility(View.GONE);
        messageTV.setVisibility(View.VISIBLE);
    }

    private void showSuccessComponents() {
        imageView.setVisibility(View.VISIBLE);
        imageNameTV.setVisibility(View.VISIBLE);
        messageTV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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