package com.phoenix.otlobbetyshipper.Model;

import android.text.Editable;

public class Push {
    private String name;
    private String phone;
    private String idnum;

    public Push() {
    }

    public Push(String name, String phone , String idnum) {

        this.name = name;
        this.phone = phone;
        this.idnum = idnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdnum() {
        return idnum;
    }

    public void setIdnum(String idnum) {
        this.idnum = idnum;
    }
}
