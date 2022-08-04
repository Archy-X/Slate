package com.archyx.slate.item.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.context.ContextProvider;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.builder.TemplateItemBuilder;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TemplateItemParser<C> extends MenuItemParser {

    private final ContextProvider<C> contextProvider;

    public TemplateItemParser(Slate slate, ContextProvider<C> contextProvider) {
        super(slate);
        this.contextProvider = contextProvider;
    }

    @Override
    public MenuItem parse(ConfigurationSection section, String menuName) {
        TemplateItemBuilder<C> builder = new TemplateItemBuilder<>(slate);

        String name = section.getName();
        builder.name(name);

        // Look through keys for contexts
        Map<C, ItemStack> baseItems = new HashMap<>();
        Map<C, SlotPos> positions = new HashMap<>();

        if (contextProvider != null) {
            for (String key : section.getKeys(false)) {
                if (isKeyWord(key)) continue; // Skip for key words used for default item parsing
                C context = contextProvider.parse(menuName, key);
                ConfigurationSection contextSection = section.getConfigurationSection(key);
                if (context != null && contextSection != null) { // Context parse found a match
                    if (contextSection.contains("material")) {
                        baseItems.put(context, parseBaseItem(contextSection));
                    }
                    String positionString = contextSection.getString("pos");
                    if (positionString != null) {
                        positions.put(context, parsePosition(positionString));
                    }
                }
            }
        }

        String defaultPos = section.getString("pos");
        if (positions.size() == 0 && defaultPos != null) {
            SlotPos pos = parsePosition(defaultPos);
            builder.defaultPosition(pos);
        }

        builder.baseItems(baseItems);
        builder.positions(positions);

        // Parse default base item if exists
        if (section.contains("material")) {
            builder.defaultBaseItem(parseBaseItem(section));
        }

        builder.displayName(parseDisplayName(section));
        builder.lore(parseLore(section));

        parseActions(builder, section.getValues(false), menuName, name);

        builder.options(slate.getMenuManager().loadOptions(section));

        return builder.build();
    }
}
