package com.zyuco.lab10.lib;

import android.database.Cursor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Person implements Serializable {
    public String name, birthday, gift;

    public Person(String name, String birthday, String gift) {
        this.name = name;
        this.birthday = birthday;
        this.gift = gift;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("birthday", birthday);
        map.put("gift", gift);
        return map;
    }

    public static Person fromCursor(Cursor cursor) {
        Person person = new Person(null, null, null);
        person.name = cursor.getString(cursor.getColumnIndex("name"));
        person.birthday = cursor.getString(cursor.getColumnIndex("birthday"));
        person.gift = cursor.getString(cursor.getColumnIndex("gift"));

        return person;
    }
}
