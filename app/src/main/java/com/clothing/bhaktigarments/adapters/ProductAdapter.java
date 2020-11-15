package com.clothing.bhaktigarments.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
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

    public ProductAdapter(@NonNull Activity context, ArrayList<Product> arrayList) {
        super(context, R.layout.single_item_list_view, arrayList);
        this.arrayList = arrayList;
        this.activity = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.single_item_list_view, parent, false);
        Product product = arrayList.get(position);
        TextView name = view.findViewById(R.id.textView_single_item_list_view);
        ImageView delete = view.findViewById(R.id.imageView_single_item_list_view);
        name.setText(product.getName());
        if (product.getName().equals("No Data Found.")) {
            delete.setVisibility(View.GONE);
            name.setTextColor(Color.RED);
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
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Warning");
        alertDialog.setCancelable(true);
        alertDialog.setMessage("Product '" + product.getName() + "' will be deleted permanently.\nAre you sure to delete it?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct(product);
            }
        });
        alertDialog.show();
    }

    private void deleteProduct(Product product) {
        LocalDatabase db = new LocalDatabase(activity);
        ResponseHandler response = db.deleteProduct(product.getUid());
        Log.i(AppConfig.APP_NAME, ""+response.getErrorCode());
        if (response.getErrorCode() == 0) {
            remove(product);
            Log.i(AppConfig.APP_NAME,"size = "+arrayList.size());
            if (arrayList.size() == 0) {
                Product p = new Product();
                p.setName("No Data Found.");
                arrayList.clear();
                arrayList.add(p);
            }
            notifyDataSetChanged();
        }
    }
}



