package com.clothing.bhaktigarments.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.clothing.bhaktigarments.R;
import com.clothing.bhaktigarments.classes.Product;
import com.clothing.bhaktigarments.config.AppConfig;
import com.clothing.bhaktigarments.helpers.LocalDatabase;
import com.clothing.bhaktigarments.helpers.ResponseHandler;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    private ArrayList<Product> arrayList;
    private Activity activity;
    private String from, shopUid;

    public ProductAdapter(@NonNull Activity context, ArrayList<Product> arrayList, String from,
                          @Nullable String shopUid) {
        super(context, R.layout.single_item_list_view, arrayList);
        this.arrayList = arrayList;
        this.activity = context;
        this.from = from;
        this.shopUid = shopUid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.single_item_list_view, parent, false);
        Product product = arrayList.get(position);
        TextView name = view.findViewById(R.id.textView_single_item_list_view);
        ImageView delete = view.findViewById(R.id.imageView_single_item_list_view);
        name.setText(product.getSerialNo() + " | " + product.getName());
        if (product.getName().equals("No Data Found") || product.getName().equals("No Products added yet")) {
            delete.setVisibility(View.GONE);
            name.setTextColor(Color.RED);
            name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            name.setText("¯\\_(ツ)_/¯  " + product.getName() + "  ¯\\_(ツ)_/¯");
        } else {
            name.setTextColor(activity.getColor(R.color.black));
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(product);
            }
        });
        return view;
    }

    

    private void showDialog(Product product) {

        String message = "";
        if (from.equals(AppConfig.PRODUCT_ACTIVITY)) {
            message= "Product " + product.getSerialNo() + " | '" + product.getName() +
                    "'\nwill be deleted permanently.\nAre you sure to delete it?";
        } else if (from.equals(AppConfig.ADD_PRODUCTS_TO_SHOP_ACTIVITY)) {
            message ="Product " + product.getSerialNo() + " | '" + product.getName() +
                    "'\nwill be removed from this Shop.\nAre you sure to remove it?";
        }
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Warning");
        alertDialog.setCancelable(true);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (from.equals(AppConfig.PRODUCT_ACTIVITY)) {
                    deleteProductForProductsActivity(product);
                } else if (from.equals(AppConfig.ADD_PRODUCTS_TO_SHOP_ACTIVITY)) {
                    deleteProductForShopActivity(product);
                }
            }
        });
        alertDialog.show();
    }

    private void deleteProductForShopActivity(Product product) {
        LocalDatabase db = new LocalDatabase(activity);
        ResponseHandler response = db.deleteProductFromShop(product.getUid(), shopUid);
        Log.i(AppConfig.APP_NAME, "" + response.getErrorCode());
        if (response.getErrorCode() == 0) {
            remove(product);
            Log.i(AppConfig.APP_NAME, "size = " + arrayList.size());
            if (arrayList.size() == 0) {
                Product p = new Product();
                p.setName("No Products added yet");
                arrayList.clear();
                arrayList.add(p);
            }
            notifyDataSetChanged();
            activity.recreate();
        }
    }

    private void deleteProductForProductsActivity(Product product) {
        LocalDatabase db = new LocalDatabase(activity);
        ResponseHandler response = db.deleteProduct(product.getUid());
        Log.i(AppConfig.APP_NAME, "" + response.getErrorCode());
        if (response.getErrorCode() == 0) {
            remove(product);
            Log.i(AppConfig.APP_NAME, "size = " + arrayList.size());
            if (arrayList.size() == 0) {
                Product p = new Product();
                p.setName("No Data Found");
                arrayList.clear();
                arrayList.add(p);
            }
            notifyDataSetChanged();
        }
    }
}



