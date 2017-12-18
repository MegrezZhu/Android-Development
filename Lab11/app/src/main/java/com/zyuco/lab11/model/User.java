package com.zyuco.lab11.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("login")
    public String name;
    public int id;
    public String blog;
}
