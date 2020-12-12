package com.clothing.bhaktigarments.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.clothing.bhaktigarments.R;
import com.clothing.bhaktigarments.classes.Shop;
import com.clothing.bhaktigarments.config.AppConfig;
import com.clothing.bhaktigarments.helpers.LocalDatabase;
import com.clothing.bhaktigarments.helpers.ResponseHandler;

import java.io.File;

public class ShopDetailsActivity extends AppCompatActivity {

    // components
    private EditText nameED;
    private Button editBtn, delBtn, addRemoveProdBtn, addRemoveWorkerBtn;
    private TextView messageTV;

    // data
    private static String uid;
    private Shop shop;

    // helpers
    private LocalDatabase db;
    private ResponseHandler response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);
        Toolbar toolbar = findViewById(R.id.toolbar_ShopDetailsActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(AppConfig.APP_NAME, "onStart");

        declarations();
        listeners();
    }

    private void listeners() {
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) {
                    editWorkerDetails();
                }
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        addRemoveProdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ShopDetailsActivity.this, AddRemoveProductsToShopActivity.class);
                i.putExtra(AppConfig.UID, uid);
                startActivity(i);
            }
        });

        addRemoveWorkerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void editWorkerDetails() {
        response = db.editShop(shop);
        Log.i(AppConfig.APP_NAME, "After edit response code = " + response.getErrorCode());
        if (response.getErrorCode() == 0) {
            showSuccessComponents("Shop Edited");
        } else {
            showErrorComponents(response.getErrorMessage(2));
        }
    }

    private void declarations() {
        try {
            String receivedUid = getIntent().getStringExtra(AppConfig.UID);
            if (uid == null) {
                Log.i(AppConfig.APP_NAME, "uid is NULL");
                uid = receivedUid;
            } else if (!uid.equals(receivedUid) && receivedUid != null) {
                Log.i(AppConfig.APP_NAME, "uid does not equal - " + receivedUid);
                uid = receivedUid;
            }
        } catch (Exception ignore) {
        }
        nameED = findViewById(R.id.editText_name_ShopDetailsActivity);
        messageTV = findViewById(R.id.textView_message_ShopDetailsActivity);
        editBtn = findViewById(R.id.button_edit_ShopDetailsActivity);
        delBtn = findViewById(R.id.button_delete_ShopDetailsActivity);
        addRemoveProdBtn = findViewById(R.id.button_add_products_ShopDetailsActivity);
        addRemoveWorkerBtn = findViewById(R.id.button_view_products_ShopDetailsActivity);

        db = new LocalDatabase(ShopDetailsActivity.this);
        messageTV = findViewById(R.id.textView_message_ShopDetailsActivity);
        shop = db.getShopByUid(uid);
        if (shop != null) {
            Log.i(AppConfig.APP_NAME, "shop is not null");
            loadUIComponents();
        }
    }


    private void loadUIComponents() {
        nameED.setText(shop.getName());
    }

    private boolean checkData() {
        if (nameED.getText().toString().trim().isEmpty()) {
            showErrorComponents("Please Enter Name");
            return false;
        }
        shop.setName(nameED.getText().toString().trim());
        return true;
    }

    private void showErrorComponents(String message) {
        messageTV.setText(message);
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setTextColor(Color.RED);
    }

    private void showSuccessComponents(String message) {
        messageTV.setText(message);
        messageTV.setVisibility(View.VISIBLE);
        messageTV.setTextColor(getColor(R.color.blue_medium));
    }

    private void showDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(ShopDetailsActivity.this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setCancelable(true);
        alertDialog.setMessage("The QR Code image of this Shop will also be deleted.\nAre you sure to delete it?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                response = db.deleteShop(shop.getUid());
                if (response.getErrorCode() == 0) {
                    deleteImage();
                }
            }
        });
        alertDialog.show();
    }

    private void deleteImage() {
        try {
            String appDirectoryPath = Environment.getExternalStorageDirectory().toString() +
                    "/Bhakti Garments/Shops/" + shop.getUid() + ".png";
            File appDirectory = new File(appDirectoryPath);
            if (!appDirectory.exists()) {
                showErrorComponents("Cannot locate the image at location : " + appDirectoryPath +
                        "\nPlease delete the image with name " + shop.getUid() + ".png manually.");
            } else {
                if (appDirectory.delete()) {
                    showSuccessComponents("Shop Deleted.");
                    finish();
                } else {
                    showErrorComponents("Cannot Delete Shop. Make sure you have allowed all " +
                            "permissions and the QR Code is present in the phone storage.");
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AppConfig.UID, uid);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        uid = savedInstanceState.getString(AppConfig.UID);
    }
}