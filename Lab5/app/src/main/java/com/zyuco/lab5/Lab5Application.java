package com.zyuco.lab5;

import android.app.Application;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Lab5Application extends Application {
    final public List<Map<String, String>> items = new ArrayList<>();
    final public List<Map<String, String>> cart = new ArrayList<>();
    public SimpleAdapter cartAdapter;
}
