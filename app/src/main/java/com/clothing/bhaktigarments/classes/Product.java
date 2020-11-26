package com.clothing.bhaktigarments.classes;

import java.io.Serializable;

public class Product implements Serializable {
    private String uid, name, serialNo;

    public Product() {
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        if (product.getUid() == null) return false;
        if (getUid() == null) return false;
        if (product.getSerialNo() == null) return false;
        if (getSerialNo() == null) return false;
        return getUid().equals(product.getUid()) &&
                getName().equals(product.getName()) &&
                getSerialNo().equals(product.getSerialNo());
    }

}
