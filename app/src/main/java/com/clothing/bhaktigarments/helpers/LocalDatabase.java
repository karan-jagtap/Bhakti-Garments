package com.clothing.bhaktigarments.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.clothing.bhaktigarments.classes.Product;
import com.clothing.bhaktigarments.classes.Shop;
import com.clothing.bhaktigarments.classes.Worker;
import com.clothing.bhaktigarments.config.AppConfig;

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
    private static final String KEY_SERIAL_NO = "serial_no";
    private static final String TABLE_PRODUCT = "product";

    // Worker : Constants
    private static final String KEY_CONTACT_NO = "contact_no";
    private static final String KEY_MACHINE_NO = "machine_no";
    private static final String TABLE_WORKER = "worker";

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
                    KEY_SERIAL_NO + " TEXT, " +
                    KEY_NAME + " TEXT" +
                    ");";

    // Worker : TABLE
    // Query : create table worker (id integer not null primary key autoincrement, uid text, name text, contact_no text, machine_no text);
    private static final String CREATE_TABLE_WORKER =
            "CREATE TABLE " + TABLE_WORKER + " (" +
                    KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    KEY_UID + " TEXT, " +
                    KEY_NAME + " TEXT, " +
                    KEY_CONTACT_NO + " TEXT, " +
                    KEY_MACHINE_NO + " TEXT " +
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
        db.execSQL(CREATE_TABLE_WORKER);
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
        if (selectQueryTable(shop.getName(), TABLE_SHOP, KEY_NAME)) {
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
        if (selectQueryTable(shop.getName(), TABLE_SHOP, KEY_NAME)) {
            response.setErrorCode(0);
            return response;
        }
        response.setErrorCode(2);
        return response;
    }

    // @context : REGISTER WORKER
    // @desc : Registers a new worker
    // @params : Worker.class
    // @returns : ResponseHandler
    public ResponseHandler registerWorker(Worker worker) {
        ResponseHandler response = new ResponseHandler();

        // Checking if a worker with same name is already present.
        if (selectQueryTable(worker.getName(), TABLE_WORKER, KEY_NAME)) {
            response.setErrorCode(5);
            return response;
        }

        // Insert new Worker
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_UID, worker.getUid());
        cv.put(KEY_NAME, worker.getName());
        cv.put(KEY_CONTACT_NO, worker.getContactNo());
        cv.put(KEY_MACHINE_NO, worker.getMachineNo());
        db.insert(TABLE_WORKER, null, cv);
        cv.clear();
        db.close();

        // Check if insertion successful
        if (selectQueryTable(worker.getName(), TABLE_WORKER, KEY_NAME)) {
            response.setErrorCode(0);
            return response;
        }
        response.setErrorCode(2);
        return response;
    }

    // @context : EDIT WORKER
    // @desc : Edit a worker
    // @params : Worker.class
    // @returns : ResponseHandler
    public ResponseHandler editWorker(Worker worker) {
        ResponseHandler response = new ResponseHandler();

        // Edit Worker
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, worker.getName());
        cv.put(KEY_CONTACT_NO, worker.getContactNo());
        cv.put(KEY_MACHINE_NO, worker.getMachineNo());
        int rows = db.update(TABLE_WORKER, cv, KEY_UID + "= ?", new String[]{worker.getUid()});
        cv.clear();
        db.close();

        if (rows == 1) {
            response.setErrorCode(0);
            return response;
        }
        response.setErrorCode(2);
        return response;
    }

    // @context : GET SINGLE WORKER
    // @desc : Retrieves a worker
    // @params : String
    // @returns : Worker
    public Worker getWorkerByUid(String uid) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_WORKER + " WHERE " + KEY_UID + " = '" + uid + "';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        Worker worker = new Worker();
        Log.i(AppConfig.APP_NAME, "query = " + selectQuery);
        Log.i(AppConfig.APP_NAME, "cursor count = " + cursor.getCount());
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            worker.setUid(uid);
            Log.i(AppConfig.APP_NAME,"2 = "+cursor.getInt(0));
            worker.setName(cursor.getString(2));
            worker.setContactNo(cursor.getString(3));
            worker.setMachineNo(cursor.getString(4));
        } else {
            worker = null;
        }
        cursor.close();
        db.close();
        return worker;
    }

    // @context : DELETE WORKER
    // @desc : Deletes a worker
    // @params : String
    // @returns : ResponseHandler
    public ResponseHandler deleteWorker(String uid) {
        ResponseHandler response = new ResponseHandler();
        SQLiteDatabase db = getWritableDatabase();
        int deleted = db.delete(TABLE_WORKER, KEY_UID + "= ?", new String[]{uid});
        if (deleted == 1) {
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
        if (selectQueryTable(product.getName(), TABLE_PRODUCT, KEY_NAME)) {
            response.setErrorCode(4);
            return response;
        }

        // Checking if a product with same serial no. is already present.
        // 19/11/2020 - As per client's requirements this doesn't need to satisfy,
        //              but will continue with this and then act on feedback.
        if (selectQueryTable(product.getSerialNo(), TABLE_PRODUCT, KEY_SERIAL_NO)) {
            response.setErrorCode(6);
            return response;
        }

        // Insert new Product
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_UID, product.getUid());
        cv.put(KEY_SERIAL_NO, product.getSerialNo());
        cv.put(KEY_NAME, product.getName());
        db.insert(TABLE_PRODUCT, null, cv);
        cv.clear();
        db.close();

        // Check if insertion successful
        if (selectQueryTable(product.getSerialNo(), TABLE_PRODUCT, KEY_SERIAL_NO)) {
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
                product.setSerialNo(cursor.getString(2));
                Log.i(AppConfig.APP_NAME, "serial no - " + product.getSerialNo());
                product.setName(cursor.getString(3));
                arrayList.add(product);
            }
            response.setErrorCode(0);
            response.setProductArrayList(arrayList);
            return response;
        }
        response.setErrorCode(3);
        Product product = new Product();
        product.setName("No Data Found");
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
    private boolean selectQueryTable(String name, String TABLE_NAME, String KEY) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " +
                KEY + " = '" + name +
                "';";
        Log.i(AppConfig.APP_NAME, "SELECT query = " + selectQuery);
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


    public int checkUid(String uid) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_SHOP + " WHERE " + KEY_UID + " = '" + uid + "';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return AppConfig.SHOP_UID;
        }
        selectQuery = "SELECT * FROM " + TABLE_WORKER + " WHERE " + KEY_UID + " = '" + uid + "';";
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return AppConfig.WORKER_UID;
        }
        return 0;
    }

    // END :: UTILITIES
}
