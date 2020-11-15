package com.clothing.bhaktigarments.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.clothing.bhaktigarments.classes.Product;
import com.clothing.bhaktigarments.classes.Shop;

import java.util.ArrayList;

public class LocalDatabase extends SQLiteOpenHelper {

    // constants
    private static final String DB_NAME = "bhakti_garments";
    private static final int DB_VERSION = 1;

    // Shop : Constants
    private static final String KEY_ID = "id";
    private static final String KEY_UID = "uid";
    private static final String KEY_NAME = "name";
    private static final String TABLE_SHOP = "shop";

    // Product : Constants
    private static final String TABLE_PRODUCT = "product";

    // Shop : TABLE
    // Query : create table shop (id integer not null primary key autoincrement, uid text, name text);
    private static final String CREATE_TABLE_SHOP =
            "CREATE TABLE " + TABLE_SHOP + " (" +
                    KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    KEY_UID + " TEXT, " +
                    KEY_NAME + " TEXT" +
                    ");";

    // Product : TABLE
    // Query : create table product (id integer not null primary key autoincrement, uid text, name text);
    private static final String CREATE_TABLE_PRODUCT =
            "CREATE TABLE " + TABLE_PRODUCT + " (" +
                    KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    KEY_UID + " TEXT, " +
                    KEY_NAME + " TEXT" +
                    ");";

    // variables
    private Context context;

    public LocalDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SHOP);
        db.execSQL(CREATE_TABLE_PRODUCT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    // @context : REGISTER SHOP
    // @desc : Registers a new shop
    // @params : Shop.class
    // @returns : ResponseHandler
    public ResponseHandler registerShop(Shop shop) {
        ResponseHandler response = new ResponseHandler();

        // Checking if a shop with same name is already present.
        if (selectQueryTable(shop.getName(), TABLE_SHOP)) {
            response.setErrorCode(1);
            return response;
        }

        // Insert new Shop
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_UID, shop.getUid());
        cv.put(KEY_NAME, shop.getName());
        db.insert(TABLE_SHOP, null, cv);
        cv.clear();
        db.close();

        // Check if insertion successful
        if (selectQueryTable(shop.getName(), TABLE_SHOP)) {
            response.setErrorCode(0);
            return response;
        }
        response.setErrorCode(2);
        return response;
    }

    // @context : ADD PRODUCT
    // @desc : Adds a new Product
    // @params : Product.class
    // @returns : ResponseHandler
    public ResponseHandler addProduct(Product product) {
        ResponseHandler response = new ResponseHandler();

        // Checking if a product with same name is already present.
        if (selectQueryTable(product.getName(), TABLE_PRODUCT)) {
            response.setErrorCode(4);
            return response;
        }

        // Insert new Produc
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_UID, product.getUid());
        cv.put(KEY_NAME, product.getName());
        db.insert(TABLE_PRODUCT, null, cv);
        cv.clear();
        db.close();

        // Check if insertion successful
        if (selectQueryTable(product.getName(), TABLE_PRODUCT)) {
            response.setErrorCode(0);
            return response;
        }
        response.setErrorCode(2);
        return response;
    }

    // @context : LIST PRODUCT
    // @desc : Gets list of all products
    // @returns : ArrayList<Product>
    public ResponseHandler getAllProducts() {
        ResponseHandler response = new ResponseHandler();
        ArrayList<Product> arrayList = new ArrayList<>();

        // get all records
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCT + ";";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Product product = new Product();
                product.setUid(cursor.getString(1));
                product.setName(cursor.getString(2));
                arrayList.add(product);
            }
            response.setErrorCode(0);
            response.setProductArrayList(arrayList);
            return response;
        }
        response.setErrorCode(3);
        Product product = new Product();
        product.setName("No Data Found.");
        arrayList.clear();
        arrayList.add(product);
        response.setProductArrayList(arrayList);
        cursor.close();
        db.close();
        return response;
    }

    // @context : DELETE PRODUCT
    // @desc : Deletes a Product
    // @params : String
    // @returns : ResponseHandler
    public ResponseHandler deleteProduct(String uid) {
        ResponseHandler response = new ResponseHandler();
        SQLiteDatabase db = getWritableDatabase();
        int deleted = db.delete(TABLE_PRODUCT, KEY_UID + "= ?", new String[]{uid});
        if (deleted == 1) {
            response.setErrorCode(0);
            return response;
        }
        response.setErrorCode(2);
        return response;
    }


    // START :: UTILITIES

    // @desc : Checks if shop is present
    // @params : String
    // @returns : boolean
    private boolean selectQueryTable(String name, String TABLE_NAME) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " +
                KEY_NAME + " = '" + name +
                "';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }
    // END :: UTILITIES
}
