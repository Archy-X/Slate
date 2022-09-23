package com.archyx.slate.action.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.MenuAction;
import com.archyx.slate.action.builder.MenuActionBuilder;
import com.archyx.slate.context.ContextProvider;
import com.archyx.slate.util.TextUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MenuActionParser extends ActionParser {

    public MenuActionParser(Slate slate) {
        super(slate);
    }

    @Override
    public Action parse(Map<?, ?> map) {
        MenuActionBuilder builder = new MenuActionBuilder(slate);
        builder.actionType(MenuAction.ActionType.valueOf(getString(map, "action").toUpperCase(Locale.ROOT)));
        String menuName = getString(map, "menu", null);
        builder.menuName(menuName);
        builder.properties(getProperties(menuName, map));
        return builder.build();
    }

    private Map<String, Object> getProperties(String menuName, Map<?, ?> map) {
        Map<?, ?> propertiesConfig = getMap(map, "properties", null);
        Map<String, Object> properties = new HashMap<>();
        if (propertiesConfig != null) {
            for (Object keyObj : propertiesConfig.keySet()) {
                String key = String.valueOf(keyObj);
                Object valueObj = propertiesConfig.get(keyObj);
                if (valueObj instanceof String) {
                    String value = (String) valueObj;
                    if (value.contains(":")) { // Parse custom object
                        String type = TextUtil.substringBefore(value, ":");
                        value = TextUtil.substringAfter(value, ":");
                        // Get context provider from type
                        ContextProvider<?> contextProvider = slate.getContextManager().getContextProvider(type);
                        if (contextProvider != null) {
                            properties.put(key, contextProvider.parse(menuName, value)); // Add the parsed object
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
