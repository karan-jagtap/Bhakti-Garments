package com.clothing.bhaktigarments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.clothing.bhaktigarments.R;
import com.clothing.bhaktigarments.config.AppConfig;
import com.clothing.bhaktigarments.helpers.LocalDatabase;
import com.clothing.bhaktigarments.helpers.MessageDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    // components
    private LinearLayout registerShopL, detailsShopL, registerWorkerL, detailsWorkerL, productL, settingsL;

    // helpers
    private LocalDatabase db;
    private MessageDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar_DashboardActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        checkAvailability();

        declarations();
        listeners();
    }

    private void checkAvailability() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date today = new Date();
        try {
            Date endDate = format.parse(AppConfig.MAX_TIMER);
            if (today.after(endDate)) {
                finish();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void listeners() {
        registerShopL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, RegisterShopActivity.class));
            }
        });

        detailsShopL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQRCode();
            }
        });

        registerWorkerL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, RegisterWorkerActivity.class));
            }
        });

        detailsWorkerL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scanQRCode();
            }
        });

        productL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ProductsActivity.class));
            }
        });

        settingsL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void declarations() {
        registerShopL = findViewById(R.id.layout_register_shop_DashboardActivity);
        detailsShopL = findViewById(R.id.layout_shop_details_DashboardActivity);
        registerWorkerL = findViewById(R.id.layout_register_worker_DashboardActivity);
        detailsWorkerL = findViewById(R.id.layout_worker_details_DashboardActivity);
        productL = findViewById(R.id.layout_products_DashboardActivity);
        settingsL = findViewById(R.id.layout_settings_DashboardActivity);

        db = new LocalDatabase(DashboardActivity.this);
        dialog = new MessageDialog(DashboardActivity.this);
    }

    private void scanQRCode() {
        Toast.makeText(this, "Scanning started", Toast.LENGTH_SHORT).show();
        IntentIntegrator integrator = new IntentIntegrator(DashboardActivity.this);
        integrator.setPrompt("Align the QR code inside the box");
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    private void checkShopOrWorkerUID(String uid) {
        switch (db.checkUid(uid)) {
            case AppConfig.SHOP_UID:
                Log.i(AppConfig.APP_NAME, "This is shop's uid");
                Intent shopI = new Intent(DashboardActivity.this, ShopDetailsActivity.class);
                shopI.putExtra(AppConfig.UID, uid);
                startActivity(shopI);
                break;
            case AppConfig.WORKER_UID:
                Log.i(AppConfig.APP_NAME, "This is worker's uid :: " + uid);
                Intent workerI = new Intent(DashboardActivity.this, WorkerDetailsActivity.class);
                workerI.putExtra(AppConfig.UID, uid);
                startActivity(workerI);
                break;
            case 0:
                Log.i(AppConfig.APP_NAME, "This is not useful uid anymore.");
                dialog.showMessage("This QR Code's data is not valid.\nIt has either been deleted or generated from some other application.");
                break;
        }
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Log.i(AppConfig.APP_NAME, "request code = " + requestCode);
        if (result != null) {
            if (result.getContents() != null) {
                Log.i(AppConfig.APP_NAME, "uid inside onresult = " + result.getContents());
                checkShopOrWorkerUID(result.getContents());

            }
        } else {
            Log.i(AppConfig.APP_NAME, "result is null");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}