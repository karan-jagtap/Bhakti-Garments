package com.clothing.bhaktigarments.classes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Shop implements Serializable {
    private String uid, name;

    public Shop() {
        this.uid = "";
        this.name = "";
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
    public String toString() {
        return '{' +
                "id='" + uid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public void convertStringToInstance(String contents) throws JSONException {
        JSONObject jsonObject = new JSONObject(contents);
        this.uid = jsonObject.getString("id");
        this.name = jsonObject.getString("name");
    }
}
