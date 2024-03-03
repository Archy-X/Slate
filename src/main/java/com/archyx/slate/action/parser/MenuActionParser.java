package com.archyx.slate.action.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.MenuAction;
import com.archyx.slate.action.builder.MenuActionBuilder;
import com.archyx.slate.context.ContextProvider;
import com.archyx.slate.util.TextUtil;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MenuActionParser extends ActionParser {

    public MenuActionParser(Slate slate) {
        super(slate);
    }

    @Override
    public Action parse(ConfigurationNode config) {
        MenuActionBuilder builder = new MenuActionBuilder(slate);
        builder.actionType(MenuAction.ActionType.valueOf(Objects.requireNonNull(config.node("action").getString()).toUpperCase(Locale.ROOT)));
        String menuName = config.node("menu").getString();
        builder.menuName(menuName);
        builder.properties(getProperties(menuName, config));
        return builder.build();
    }

    private Map<String, Object> getProperties(String menuName, ConfigurationNode config) {
        ConfigurationNode propertiesConfig = config.node("properties");
        Map<String, Object> properties = new HashMap<>();
        if (propertiesConfig != null) {
            for (Object keyObj : propertiesConfig.childrenMap().keySet()) {
                String key = String.valueOf(keyObj);
                Object valueObj = propertiesConfig.node(keyObj).raw();
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
