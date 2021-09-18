package com.archyx.slate.action.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.MenuAction;
import com.archyx.slate.action.builder.MenuActionBuilder;
import com.archyx.slate.context.ContextProvider;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MenuActionParser extends ActionParser {

    public MenuActionParser(Slate slate) {
        super(slate);
    }

    @Override
    public Action parse(Map<?, ?> map) {
        return new MenuActionBuilder(slate)
                .actionType(MenuAction.ActionType.valueOf(getString(map, "action").toUpperCase(Locale.ROOT)))
                .menuName(getString(map, "menu", null))
                .properties(getProperties(map))
                .build();
    }

    private Map<String, Object> getProperties(Map<?, ?> map) {
        Map<?, ?> propertiesConfig = getMap(map, "properties", null);
        Map<String, Object> properties = new HashMap<>();
        if (propertiesConfig != null) {
            for (Object keyObj : propertiesConfig.keySet()) {
                String key = String.valueOf(keyObj);
                Object valueObj = propertiesConfig.get(keyObj);
                if (valueObj instanceof String) {
                    String value = (String) valueObj;
                    if (value.contains(":")) { // Parse custom object
                        String type = StringUtils.substringBefore(value, ":");
                        value = StringUtils.substringAfter(value, ":");
                        // Get context provider from type
                        ContextProvider<?> contextProvider = slate.getContextManager().getContextProvider(type);
                        if (contextProvider != null) {
                            properties.put(key, contextProvider.parse(value)); // Add the parsed object
                        }
                    } else {
                        properties.put(key, value);
                    }
                } else {
                    properties.put(key, valueObj);
                }
            }
        }
        return properties;
    }
}
