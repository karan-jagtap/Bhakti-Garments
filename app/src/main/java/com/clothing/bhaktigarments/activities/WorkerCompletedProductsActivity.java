package com.clothing.bhaktigarments.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.clothing.bhaktigarments.R;
import com.clothing.bhaktigarments.adapters.ProductAdapter;
import com.clothing.bhaktigarments.classes.Product;
import com.clothing.bhaktigarments.config.AppConfig;
import com.clothing.bhaktigarments.helpers.LocalDatabase;
import com.clothing.bhaktigarments.helpers.MessageDialog;
import com.clothing.bhaktigarments.helpers.ResponseHandler;

import java.util.ArrayList;

public class WorkerCompletedProductsActivity extends AppCompatActivity {

    // components
    private Spinner spinner;
    private ImageButton button;
    private ListView listView;


    // data
    private ArrayList<Product> selectedProductList, allProductList;
    private ProductAdapter selectedProductAdapter;
    private ArrayAdapter<String> displayAdapter;
    private ArrayList<String> displayArrayList;
    private static String uid, shopUid;

    // helpers
    private MessageDialog dialog;
    private LocalDatabase db;
    private ResponseHandler response;

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
            } else if (!uid.equals(receivedUid) && receivedUid != null) {
                uid = receivedUid;
            }
            Log.i(AppConfig.APP_NAME, "WorkerCompletedProductsActivity | Worker Uid = " + uid);
        } catch (Exception ignore) {
        }

    }

    private void listeners() {

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