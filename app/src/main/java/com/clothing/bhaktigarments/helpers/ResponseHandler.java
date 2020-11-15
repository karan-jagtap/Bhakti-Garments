package com.clothing.bhaktigarments.helpers;

import com.clothing.bhaktigarments.classes.Product;

import java.util.ArrayList;

public class ResponseHandler {
    private String errorMessage;
    private int errorCode;
    private ArrayList<Product> productArrayList;

    public ResponseHandler() {
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case 0:
                errorMessage = "";
                break;
            case 1:
                errorMessage = "Shop with {} name already exists";
                break;
            case 2:
                errorMessage = "Internal Database error";
                break;
            case 3:
                errorMessage = "No Data Found.";
                break;
            case 4:
                errorMessage = "Product with {} name already exists";
                break;
        }
        return errorMessage;
    }

    public ArrayList<Product> getProductArrayList() {
        return productArrayList;
    }

    public void setProductArrayList(ArrayList<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }
}
