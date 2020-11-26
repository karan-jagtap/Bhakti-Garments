package com.clothing.bhaktigarments.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class AddProductsToShopActivity extends AppCompatActivity {

    // components
    private Spinner spinner;
    private ImageButton button;
    private ListView listView;

    // data
    private ArrayList<Product> selectedProductList, allProductList;
    private ProductAdapter selectedProductAdapter;
    private ArrayAdapter<String> displayAdapter;
    private ArrayList<String> displayArrayList;
    private static String uid;

    // helpers
    private MessageDialog dialog;
    private LocalDatabase db;
    private ResponseHandler response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products_to_shop);
        Toolbar toolbar = findViewById(R.id.toolbar_AddProductsToShopActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        declarations();
        listeners();

    }

    private void listeners() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getSelectedItemPosition() != 0) {
                    // add product to selected list, save it to db and remove it from allProducts list
                    Log.i(AppConfig.APP_NAME, "on IMagebutton click");
                    addProductToSelectedProductListAndSaveDB(spinner.getSelectedItemPosition() - 1);
                }
            }
        });
    }

    private void addProductToSelectedProductListAndSaveDB(int i) {
        Product product = allProductList.get(i);
        response = db.addProductToShop(product.getUid(), uid);
        if (response.getErrorCode() == 0) {
            if(selectedProductList.get(0).getName().equals("No Products added yet")) {
                selectedProductList.remove(0);
            }
            selectedProductList.add(product);
            allProductList.remove(product);
            displayArrayList.remove(i + 1);
            spinner.setSelection(0, false);
            if (allProductList.isEmpty()) {
                dialog.showMessage("You have added all presently available products to this Shop.\n" +
                        "If you need more products then you can add them from Dashboard.");
                spinner.setEnabled(false);
                spinner.setClickable(false);
                button.setEnabled(false);
                button.setClickable(false);
            }
            selectedProductAdapter.notifyDataSetChanged();
            displayAdapter.notifyDataSetChanged();
            Log.i((AppConfig.APP_NAME), "after performing list operation : all -->" +
                    allProductList.size() + ", selected --> " + selectedProductList.size());
        } else {
            Log.i(AppConfig.APP_NAME, "Error while adding Product to Shop in LocalDatabase().addProductToShop()");
        }
    }

    private void declarations() {
        try {
            String receivedUid = getIntent().getStringExtra(AppConfig.UID);
            if (uid == null) {
                uid = receivedUid;
            } else if (!uid.equals(receivedUid) && receivedUid != null) {
                uid = receivedUid;
            }
        } catch (Exception ignore) {
        }
        spinner = findViewById(R.id.spinner_products_list_AddProductsToShopActivity);
        listView = findViewById(R.id.listView_selected_products_AddProductsToShopActivity);
        button = findViewById(R.id.imageButton_add_AddProductsToShopActivity);

        db = new LocalDatabase(AddProductsToShopActivity.this);
        dialog = new MessageDialog(AddProductsToShopActivity.this);

        // loading and adding already selected products list
        response = db.getAllSelectedProductsOfShopUid(uid);
        selectedProductList = response.getProductArrayList();
        selectedProductAdapter = new ProductAdapter(AddProductsToShopActivity.this,
                selectedProductList, AppConfig.ADD_PRODUCTS_TO_SHOP_ACTIVITY, uid);
        listView.setAdapter(selectedProductAdapter);

        // getting rest of the products
        response = db.getAllProducts();
        allProductList = response.getProductArrayList();
        if (allProductList.size() == 1 && allProductList.get(0).getName().equals("No Data Found")) {
            dialog.showMessage("No Products found. Please add Products from Dashboard first.");
            spinner.setEnabled(false);
            listView.setEnabled(false);
            button.setEnabled(false);
            button.setClickable(false);
        } else {
            // remove already selected product from to be selected list
            for (Product p : selectedProductList) {
                allProductList.remove(p);
            }
            displayArrayList = new ArrayList<>();
            displayArrayList.add(0, "Select Product");
            for (Product p : allProductList) {
                displayArrayList.add(p.getSerialNo() + " : " + p.getName());
            }
            if (displayArrayList.size() == 1) {
                Log.i(AppConfig.APP_NAME, "No products added yet");
                spinner.setEnabled(false);
                button.setEnabled(false);
            }
            displayAdapter = new ArrayAdapter<>(AddProductsToShopActivity.this,
                    android.R.layout.simple_list_item_1,
                    displayArrayList);
            spinner.setAdapter(displayAdapter);
        }
    }

    public void refreshAllProductsList() {

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