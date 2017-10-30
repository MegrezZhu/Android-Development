package com.zyuco.lab7;

import java.util.HashMap;
import java.util.Map;

public class MessageEvent {
    public Map<String, String> itemData;

    public MessageEvent(Map<String, String> map) {
        itemData = new HashMap<>(map);
    }
}
