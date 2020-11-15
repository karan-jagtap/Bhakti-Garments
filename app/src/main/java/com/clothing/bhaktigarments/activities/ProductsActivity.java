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
    private EditText nameED;
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
                product.setName(nameED.getText().toString().trim());
                if (!product.getName().isEmpty()) {
                    product.setUid(UUID.randomUUID().toString());
                    ResponseHandler response = db.addProduct(product);
                    if (response.getErrorCode() == 0) {
                        setSuccess("New Product Added");
                        loadProductList();
                    } else if (response.getErrorCode() == 4) {
                        Log.i(AppConfig.APP_NAME, "Dup item");
                        setError(response.getErrorMessage(4).replace("{}", product.getName()));
                    }
                } else {
                    Log.i(AppConfig.APP_NAME, "Dup item");
                    setError("Please Enter Product's Name to Add");
                }
            }
        });
    }

    private void loadProductList() {
        productArrayList = db.getAllProducts().getProductArrayList();
        ProductAdapter adapter = new ProductAdapter(ProductsActivity.this, productArrayList);
        listView.setAdapter(adapter);
    }

    private void setError(String message) {
        messageTV.setText(message);
        messageTV.setTextColor(Color.RED);
        messageTV.setVisibility(View.VISIBLE);
    }

    private void setSuccess(String message) {
        nameED.setText("");
        messageTV.setText(message);
        messageTV.setTextColor(getColor(R.color.blue_medium));
        messageTV.setVisibility(View.VISIBLE);
    }
}