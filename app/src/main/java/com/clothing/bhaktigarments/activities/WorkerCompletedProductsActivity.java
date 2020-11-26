package com.clothing.bhaktigarments.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.clothing.bhaktigarments.R;
import com.clothing.bhaktigarments.config.AppConfig;

public class WorkerCompletedProductsActivity extends AppCompatActivity {

    // data
    private static String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_completed_products);
        Toolbar toolbar = findViewById(R.id.toolbar_WorkerCompletedProductsActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        declarations();
        listeners();
    }

    private void declarations() {
        try {
            String receivedUid = getIntent().getStringExtra(AppConfig.UID);
            if (uid == null) {
                uid = receivedUid;
            } else if (!uid.equals(receivedUid)) {
                uid = receivedUid;
            }
        } catch (Exception ignore) {

        }
    }

    private void listeners() {

    }
}