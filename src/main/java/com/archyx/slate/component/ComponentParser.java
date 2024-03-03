package com.archyx.slate.component;

import com.archyx.slate.Slate;
import com.archyx.slate.lore.LoreFactory;
import com.archyx.slate.lore.LoreLine;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

public class ComponentParser {

    private final Slate slate;

    public ComponentParser(Slate slate) {
        this.slate = slate;
    }

    public MenuComponent parse(ConfigurationNode config) {
        String contextString = config.node("context").getString();
        Class<?> contextClass = slate.getContextManager().getContextClass(contextString);

        List<LoreLine> lore = new LoreFactory(slate).getLore(config.node("lore"));

        return new MenuComponent(contextClass, lore);
    }

}
