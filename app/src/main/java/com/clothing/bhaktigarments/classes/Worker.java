package com.clothing.bhaktigarments.classes;

import java.io.Serializable;

public class Worker implements Serializable {
    private String uid, name, contactNo, machineNo;

    public Worker() {
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

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getMachineNo() {
        return machineNo;
    }

    public void setMachineNo(String machineNo) {
        this.machineNo = machineNo;
    }

    @Override
    public String toString() {
        return "{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", machineNo='" + machineNo + '\'' +
                '}';
    }
}
