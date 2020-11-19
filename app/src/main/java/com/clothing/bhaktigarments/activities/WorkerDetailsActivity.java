package com.clothing.bhaktigarments.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.clothing.bhaktigarments.R;
import com.clothing.bhaktigarments.config.AppConfig;

public class WorkerDetailsActivity extends AppCompatActivity {

    // data
    private static String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);
        Toolbar toolbar = findViewById(R.id.toolbar_WorkerDetailsActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        declarations();
    }

    private void declarations() {
        if (uid != null) {
            uid = getIntent().getStringExtra("uid");
        }
        Log.i(AppConfig.APP_NAME, "WorkerDetailsActivity :: " + uid);
        Toast.makeText(this, "" + uid, Toast.LENGTH_SHORT).show();
    }
}