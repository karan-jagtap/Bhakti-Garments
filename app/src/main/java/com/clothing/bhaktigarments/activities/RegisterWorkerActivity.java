package com.clothing.bhaktigarments.activities;

import android.Manifest;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.clothing.bhaktigarments.R;
import com.clothing.bhaktigarments.classes.Worker;
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

public class RegisterWorkerActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    // components
    private EditText nameED, contactED, machineED;
    private Button saveBtn;
    private ImageView imageView;
    private TextView imageNameTV, messageTV;

    // data
    private Worker worker;
    private boolean permissionGranted = false;

    // helpers
    private MessageDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_worker);
        Toolbar toolbar = findViewById(R.id.toolbar_RegisterWorkerActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        declarations();
        listeners();
    }

    private void listeners() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) {
                    getPermission();
                    try {
                        permissionGranted = false;
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        });
    }

    private void declarations() {
        nameED = findViewById(R.id.editText_name_RegisterWorkerActivity);
        contactED = findViewById(R.id.editText_contact_no_RegisterWorkerActivity);
        machineED = findViewById(R.id.editText_machine_no_RegisterWorkerActivity);
        saveBtn = findViewById(R.id.save_RegisterWorkerActivity);
        imageView = findViewById(R.id.imageView_RegisterWorkerActivity);
        imageNameTV = findViewById(R.id.textView_imageName_RegisterWorkerActivity);
        messageTV = findViewById(R.id.textView_message_RegisterWorkerActivity);

        dialog = new MessageDialog(RegisterWorkerActivity.this);
    }

    private boolean checkData() {
        worker = new Worker();
        if (nameED.getText().toString().trim().isEmpty()) {
            showErrorComponents();
            messageTV.setText("Please Enter Name");
            messageTV.setTextColor(Color.RED);
            return false;
        }
        if (contactED.getText().toString().trim().isEmpty()) {
            showErrorComponents();
            messageTV.setText("Please Enter Contact No.");
            messageTV.setTextColor(Color.RED);
            return false;
        }
        if (contactED.getText().toString().length() != 10) {
            showErrorComponents();
            messageTV.setText("Please Enter Valid Contact No. upto 10 digits");
            messageTV.setTextColor(Color.RED);
            return false;
        }
        if (machineED.getText().toString().trim().isEmpty()) {
            showErrorComponents();
            messageTV.setText("Please Enter Machine No.");
            messageTV.setTextColor(Color.RED);
            return false;
        }
        worker.setUid(UUID.randomUUID().toString());
        worker.setName(nameED.getText().toString().trim());
        worker.setContactNo(contactED.getText().toString().trim());
        worker.setMachineNo(machineED.getText().toString().trim());
        return true;
    }

    private void addDataToLocalDB() {
        LocalDatabase db = new LocalDatabase(RegisterWorkerActivity.this);
        ResponseHandler response = db.registerWorker(worker);
        if (response.getErrorCode() == 0) {
            Log.i(AppConfig.APP_NAME, "response = 0");
            clearComponents();
            // generateQRCode();
            checkBuildVersion();
        } else if (response.getErrorCode() == 5) {
            Log.i(AppConfig.APP_NAME, "response = 5");
            showErrorComponents();
            messageTV.setText(response.getErrorMessage(5).replace("{}", worker.getName()));
            messageTV.setTextColor(Color.RED);
        }
    }

    private void generateQRCode() {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(worker.getUid(), BarcodeFormat.QR_CODE, 400, 400);
            imageView.setImageBitmap(bitmap);
            Log.i(AppConfig.APP_NAME, "Current Build version :: " + Build.VERSION.SDK_INT);
            String appDirectoryPath = Environment.getExternalStorageDirectory().toString() + "/Bhakti Garments";
            File appDirectory = new File(appDirectoryPath);
            if (!appDirectory.exists()) {
                if (appDirectory.mkdir()) {
                    appDirectoryPath = appDirectoryPath + "/Workers";
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
                appDirectoryPath = appDirectoryPath + "/Workers";
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

    private void saveImage(Bitmap bitmap) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            cv.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            cv.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Bhakti Garments/Workers");
            cv.put(MediaStore.Images.Media.DISPLAY_NAME, worker.getName() + ".png");
            cv.put(MediaStore.Images.Media.TITLE, worker.getName() + ".png");
            cv.put(MediaStore.Images.Media.IS_PENDING, true);
            Uri uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            if (uri != null) {
                OutputStream os = this.getContentResolver().openOutputStream(uri);
                if (os != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    imageNameTV.setText(uri.toString());
                    imageView.setImageBitmap(bitmap);
                    messageTV.setText("Worker Registered successfully");
                    showSuccessComponents();
                } else {
                    Toast.makeText(this, "Output steam NULL", Toast.LENGTH_SHORT).show();
                }
                os.flush();
                os.close();
                this.getContentResolver().update(uri, cv, null, null);
            } else {
                Toast.makeText(this, "null uri", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.i(AppConfig.APP_NAME, "saveImage(bitmap) exception :: " + e.getMessage());
        }
    }

    private void saveImage(Bitmap bitmap, File appDirectory) throws Exception {
        String fileName = worker.getUid() + ".png";
        File file = new File(appDirectory, fileName);
        if (!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            imageView.setImageBitmap(bitmap);
            imageNameTV.setText(fileName);
            showSuccessComponents();
            /*MediaScannerConnection.scanFile(RegisterWorkerActivity.this,
                    new String[]{file.toString()}, new String[]{"image/png"},
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i(AppConfig.APP_NAME, "Path :: " + path);
                            Log.i(AppConfig.APP_NAME, "URI :: " + uri.getLastPathSegment());
                        }
                    });*/
        } else {
            Log.i(AppConfig.APP_NAME, "QRCode Image with " + worker.getName() + " already exists.");
            showErrorComponents();
            messageTV.setText("QRCode Image with " + worker.getName() + " already exists.");
        }
    }

    private void showErrorComponents() {
        Log.i(AppConfig.APP_NAME, "hiding components");
        imageView.setVisibility(View.GONE);
        imageNameTV.setVisibility(View.GONE);
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setTextColor(Color.RED);
    }

    private void showSuccessComponents() {
        Log.i(AppConfig.APP_NAME, "showing components");
        imageView.setVisibility(View.VISIBLE);
        imageNameTV.setVisibility(View.VISIBLE);
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setTextColor(getColor(R.color.blue_medium));
        messageTV.setText("This QR Code image is stored in Bhakti Garments/Workers folder.");
    }

    private void clearComponents() {
        Log.i(AppConfig.APP_NAME, "clearing components");
        nameED.setText("");
        contactED.setText("");
        machineED.setText("");
    }

    private void checkBuildVersion() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            dialog.showMessage("This feature is not supported for Android 11 ie. Android R by the Developer of this application.\nContact Developer for further details.");
        } else {
            generateQRCode();
        }
    }

    @AfterPermissionGranted(29)
    private void getPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            Log.i(AppConfig.APP_NAME, "Call from getPermission()");
            if (!permissionGranted) {
                addDataToLocalDB();
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Please allow storage permission to save the QR Code image.",
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
        Log.i(AppConfig.APP_NAME, "Call from onPermissionsGranted()");
        permissionGranted = true;
        addDataToLocalDB();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Cannot Save QR Code", Toast.LENGTH_SHORT).show();
    }

}