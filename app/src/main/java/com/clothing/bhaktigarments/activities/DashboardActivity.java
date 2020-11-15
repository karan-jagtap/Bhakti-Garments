package com.clothing.bhaktigarments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.clothing.bhaktigarments.R;
import com.clothing.bhaktigarments.classes.Shop;
import com.clothing.bhaktigarments.config.AppConfig;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;

public class DashboardActivity extends AppCompatActivity {

    // components
    private LinearLayout registerShopL, detailsShopL, registerWorkerL, detailsWorkerL, productL, settingsL;

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

        declarations();
        listeners();
    }

    private void listeners() {
        registerShopL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ShopActivity.class));
            }
        });

        detailsShopL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanShopQRCode();
            }
        });

        registerWorkerL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        detailsWorkerL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    }


    private void scanShopQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(DashboardActivity.this);
        integrator.setPrompt("Align the QR code inside the box");
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                Shop shop = new Shop();
                try {
                    Log.i(AppConfig.APP_NAME, "Before Shop : " + shop.toString());
                    shop.convertStringToInstance(result.getContents());
                    Log.i(AppConfig.APP_NAME, "After Shop : " + shop.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(AppConfig.APP_NAME, "JSON Exception : " + e.getMessage());
                }
                Log.i(AppConfig.APP_NAME, "Scanned :: " + result.getContents());
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}