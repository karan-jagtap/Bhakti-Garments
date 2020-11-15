package com.clothing.bhaktigarments.classes;

import java.io.Serializable;

public class Product implements Serializable {
    private String uid, name;

    public Product() {
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
}
