package com.archyx.slate.item.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.context.ContextProvider;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.builder.TemplateItemBuilder;
import com.archyx.slate.lore.LoreLine;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TemplateItemParser<C> extends MenuItemParser {

    private final ContextProvider<C> contextProvider;

    public TemplateItemParser(Slate slate, ContextProvider<C> contextProvider) {
        super(slate);
        this.contextProvider = contextProvider;
    }

    @Override
    public MenuItem parse(ConfigurationNode section, String menuName) {
        TemplateItemBuilder<C> builder = new TemplateItemBuilder<>(slate);

        String name = (String) Objects.requireNonNull(section.key());
        builder.name(name);

        // Look through keys for contexts
        Map<C, ItemStack> baseItems = new HashMap<>();
        Map<C, SlotPos> positions = new HashMap<>();

        Map<C, String> contextualDisplayNames = new HashMap<>();
        Map<C, List<LoreLine>> contextualLore = new HashMap<>();

        if (contextProvider != null) {
            for (ConfigurationNode contextNode : section.node("contexts").childrenMap().values()) {
                String key = (String) Objects.requireNonNull(contextNode.key());
                C context = contextProvider.parse(menuName, key);

                if (context != null) { // Context parse found a match
                    if (!contextNode.node("material").virtual() || !contextNode.node("key").virtual()) {
                        baseItems.put(context, itemParser.parseBaseItem(contextNode));
                    }
                    String positionString = contextNode.node("pos").getString();
                    if (positionString != null) {
                        positions.put(context, parsePosition(positionString));
                    }
                    // Parse contextual display name and lore
                    String contextualDisplayName = itemParser.parseDisplayName(contextNode);
                    if (contextualDisplayName != null) {
                        contextualDisplayNames.put(context, contextualDisplayName);
                    }
                    List<LoreLine> contextualLoreList = itemParser.parseLore(contextNode);
                    if (!contextualLoreList.isEmpty()) {
                        contextualLore.put(context, contextualLoreList);
                    }
                }
            }
        }

        builder.contextualDisplayNames(contextualDisplayNames);
        builder.contextualLore(contextualLore);

        String defaultPos = section.getString("pos");
        if (positions.isEmpty() && defaultPos != null) {
            SlotPos pos = parsePosition(defaultPos);
            builder.defaultPosition(pos);
        }

        builder.baseItems(baseItems);
        builder.positions(positions);

        // Parse default base item if exists
        if (!section.node("material").virtual() || !section.node("key").virtual()) {
            builder.defaultBaseItem(itemParser.parseBaseItem(section));
        }

        builder.displayName(itemParser.parseDisplayName(section));
        builder.lore(itemParser.parseLore(section));

        parseActions(builder, section, menuName, name);

        builder.options(slate.getMenuManager().loadOptions(section));

        return builder.build();
    }
}
