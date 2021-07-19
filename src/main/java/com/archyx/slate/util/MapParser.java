package com.archyx.slate.util;

import org.apache.commons.lang.Validate;

import java.util.Map;

public class MapParser {

    public Object getElement(Map<?, ?> map, String key) {
        // Check if not null
        Object object = map.get(key);
        Validate.notNull(object, "Item requires entry with key " + key);
        return object;
    }

    public String getString(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        if (!(object instanceof String)) {
            throw new IllegalArgumentException("Key " + key + " must have value of type String");
        }
        return (String) object;
    }

    public int getInt(Map<?, ?> map, String key) {
        Object object = getElement(map, key);
        if (!(object instanceof Integer)) {
            throw new IllegalArgumentException("Key " + key + " must have value of type int");
        }
        return (int) object;
    }

}
