package com.archyx.slate.item.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.click.ClickAction;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.builder.MenuItemBuilder;
import com.archyx.slate.util.MapParser;
import fr.minuskube.inv.content.SlotPos;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class MenuItemParser extends MapParser {

    protected final Slate slate;
    protected final ConfigurateItemParser itemParser;
    private final String[] KEY_WORDS = new String[] {
        "pos", "material", "display_name", "lore", "enchantments", "potion_data", "custom_effects", "glow", "nbt", "flags", "durability", "skull_meta", "key"
    };
    private final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public MenuItemParser(Slate slate) {
        this.slate = slate;
        this.itemParser = new ConfigurateItemParser(slate);
    }

    public abstract MenuItem parse(ConfigurationNode config, String menuName);

    protected SlotPos parsePosition(String input) {
        String[] splitInput = input.split(",", 2);
        if (splitInput.length == 2) {
            int row = Integer.parseInt(splitInput[0]);
            int column = Integer.parseInt(splitInput[1]);
            return SlotPos.of(row, column);
        } else {
            int slot = Integer.parseInt(input);
            int row = slot / 9;
            int column = slot % 9;
            return SlotPos.of(row, column);
        }
    }

    protected void parseActions(MenuItemBuilder builder, ConfigurationNode config, String menuName, String itemName) {
        Map<ClickAction, List<Action>> actions = new LinkedHashMap<>();
        for (ClickAction clickAction : ClickAction.values()) {
            String id = clickAction.getId();
            if (!config.node(id).virtual()) {
                List<Action> clickActions = slate.getActionManager().parseActions(config.node(id), menuName, itemName);
                actions.put(clickAction, clickActions);
            }
        }
        builder.actions(actions);
    }

}
