package com.archyx.slate.lore.parser;

import com.archyx.slate.lore.LoreLine;
import com.archyx.slate.lore.type.ComponentLore;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Objects;

public class ComponentLoreParser implements LoreParser {

    @Override
    public LoreLine parse(ConfigurationNode config) throws SerializationException {
        // Single component
        if (!config.node("component").virtual()) {
            String component = Objects.requireNonNull(config.node("component").getString());
            return new ComponentLore(new String[] {component});
        } else if (config.node("components").isList()) {
            List<String> components = config.node("components").getList(String.class);
            Objects.requireNonNull(components);
            return new ComponentLore(components.toArray(new String[0]));
        }
        throw new IllegalArgumentException("No key named component or components was found!");
    }
}
