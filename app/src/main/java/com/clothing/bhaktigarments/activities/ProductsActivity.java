package com.clothing.bhaktigarments.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.clothing.bhaktigarments.R;
import com.clothing.bhaktigarments.adapters.ProductAdapter;
import com.clothing.bhaktigarments.classes.Product;
import com.clothing.bhaktigarments.config.AppConfig;
import com.clothing.bhaktigarments.helpers.LocalDatabase;
import com.clothing.bhaktigarments.helpers.ResponseHandler;

import java.util.ArrayList;
import java.util.UUID;

public class ProductsActivity extends AppCompatActivity {

    // components
    private Button addBT;
    private EditText serialNoED, nameED;
    private ListView listView;
    private TextView messageTV;

    // data
    private LocalDatabase db;
    private Product product;
    private ArrayList<Product> productArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = findViewById(R.id.toolbar_ProductsActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        declarations();
        listeners();
        loadProductList();
    }

    private void declarations() {
        addBT = findViewById(R.id.button_add_ProductsActivity);
        serialNoED = findViewById(R.id.editText_serial_no_ProductsActivity);
        nameED = findViewById(R.id.editText_name_ProductsActivity);
        listView = findViewById(R.id.listView_products_ProductsActivity);
        messageTV = findViewById(R.id.textView_message_ProductsActivity);

        db = new LocalDatabase(ProductsActivity.this);
        product = new Product();
    }

    private void listeners() {
        addBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setSerialNo(serialNoED.getText().toString().trim());
                product.setName(nameED.getText().toString().trim());
                if (!product.getName().isEmpty() && !product.getSerialNo().isEmpty()) {
                    Log.i(AppConfig.APP_NAME, "both not empty");
                    product.setUid(UUID.randomUUID().toString());
                    ResponseHandler response = db.addProduct(product);
                    Log.i(AppConfig.APP_NAME, "Add response inside = " + response.getErrorCode());
                    if (response.getErrorCode() == 0) {
                        setSuccess("New Product Added");
                        Log.i(AppConfig.APP_NAME, "New product added");
                        loadProductList();
                    } else if (response.getErrorCode() == 4) {
                        Log.i(AppConfig.APP_NAME, "Duplicate item");
                        setError(response.getErrorMessage(4).replace("{}", product.getName()));
                    } else if (response.getErrorCode() == 6) {
                        Log.i(AppConfig.APP_NAME, "Duplicate item");
                        setError(response.getErrorMessage(6).replace("{}", product.getSerialNo()));
                    }
                } else {
                    Log.i(AppConfig.APP_NAME, "Dup item");
                    if (product.getSerialNo().isEmpty()) {
                        setError("Please Enter Product's Serial No.");
                    } else {
                        setError("Please Enter Product's Name");
                    }
                }
            }
        });
    }

    private void loadProductList() {
        Log.i(AppConfig.APP_NAME, "loadProductList()");
        productArrayList = db.getAllProducts().getProductArrayList();
        ProductAdapter adapter = new ProductAdapter(ProductsActivity.this,
                productArrayList, AppConfig.PRODUCT_ACTIVITY, null);
        listView.setAdapter(adapter);
    }

    private void setError(String message) {
        messageTV.setText(message);
        messageTV.setTextColor(Color.RED);
        messageTV.setVisibility(View.VISIBLE);
    }

    private void setSuccess(String message) {
        serialNoED.setText("");
        nameED.setText("");
        messageTV.setText(message);
        messageTV.setTextColor(getColor(R.color.blue_medium));
        messageTV.setVisibility(View.VISIBLE);
    }
}