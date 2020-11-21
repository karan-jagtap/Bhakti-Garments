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
import android.view.inputmethod.InputMethodManager;
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
import com.clothing.bhaktigarments.helpers.MessageDialog;
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
    private boolean permissionGranted = false;

    // Helper
    private MessageDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_shop);
        Toolbar toolbar = findViewById(R.id.toolbar_RegisterShopActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Log.i(AppConfig.APP_NAME, "" + Environment.getExternalStorageDirectory().toString());
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
        dialog = new MessageDialog(RegisterShopActivity.this);
    }

    private void listeners() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shop.setName(shopNameED.getText().toString().trim());
                if (!shop.getName().isEmpty()) {
                    shop.setUid(UUID.randomUUID().toString());
                    getPermission();
                    try {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                } else {
                    //dialog.showMessage("Please Enter Shop Name");
                    showErrorComponents("Please Enter Shop Name");
                }
            }
        });
    }

    private void generateAndSaveQRCode() {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(shop.getUid(), BarcodeFormat.QR_CODE, 400, 400);
            imageView.setImageBitmap(bitmap);
            Log.i(AppConfig.APP_NAME, "Current Build version :: " + Build.VERSION.SDK_INT);

            String appDirectoryPath = Environment.getExternalStorageDirectory().toString() +  "/Bhakti Garments";
            File appDirectory = new File(appDirectoryPath);
            if (!appDirectory.exists()) {
                if (appDirectory.mkdir()) {
                    appDirectoryPath = appDirectoryPath + "/Shops";
                    appDirectory = new File(appDirectoryPath);
                    if (!appDirectory.exists()) {
                        if (appDirectory.mkdir()) {
                            saveImage(bitmap, appDirectory);
                        }
                    } else {
                        saveImage(bitmap, appDirectory);
                    }
                } else {
                    Log.i(AppConfig.APP_NAME, "Something went wrong while creating directory at :\n" + appDirectoryPath);
                }
            } else {
                Log.i(AppConfig.APP_NAME, appDirectory.toString() + " already exists");
                appDirectoryPath = appDirectoryPath + "/Shops";
                appDirectory = new File(appDirectoryPath);
                if (!appDirectory.exists()) {
                    if (appDirectory.mkdir()) {
                        saveImage(bitmap, appDirectory);
                    }
                } else {
                    saveImage(bitmap, appDirectory);
                }
            }
        } catch (Exception e) {
            Log.i(AppConfig.APP_NAME, "generateAndSaveQRCode() exception :: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveImage(Bitmap bitmap, File appDirectory) throws Exception {
        String fileName = shop.getUid() + ".png";
        File file = new File(appDirectory, fileName);
        if (!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            imageView.setImageBitmap(bitmap);
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
            showErrorComponents(new ResponseHandler().getErrorMessage(1).replace("{}", shop.getName()));
        }
    }

    private void addDataToLocalDB() {
        LocalDatabase db = new LocalDatabase(RegisterShopActivity.this);
        ResponseHandler response = db.registerShop(shop);
        if (response.getErrorCode() == 0) {
            shopNameED.setText("");
            checkBuildVersion();
        } else if (response.getErrorCode() == 1) {
            showErrorComponents(response.getErrorMessage(1).replace("{}", shop.getName()));
        }
    }

    private void showErrorComponents(String error) {
        imageView.setVisibility(View.GONE);
        imageNameTV.setVisibility(View.GONE);
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setText(error);
        messageTV.setTextColor(Color.RED);
    }

    private void showSuccessComponents() {
        imageView.setVisibility(View.VISIBLE);
        imageNameTV.setVisibility(View.VISIBLE);
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setTextColor(getColor(R.color.blue_medium));
        messageTV.setText("This QR Code image is stored in Bhakti Garments/Shops folder.");
        imageView.requestFocus();
    }

    private void checkBuildVersion() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            dialog.showMessage("This feature is not supported for Android 11 by the Developer of this application.\nContact Developer for further details.");
        } else {
            generateAndSaveQRCode();
        }
    }

    @AfterPermissionGranted(29)
    private void getPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // generateAndSaveQRCode();
            if (!permissionGranted) {
                addDataToLocalDB();
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This is this",
                    29, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        permissionGranted = true;
        addDataToLocalDB();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Cannot Save QR Code", Toast.LENGTH_SHORT).show();
    }
}