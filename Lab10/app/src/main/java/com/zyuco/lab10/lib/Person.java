package com.zyuco.lab10.lib;

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
}
