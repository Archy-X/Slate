package dev.aurelium.slate.item.parser;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.click.ClickAction;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.builder.MenuItemBuilder;
import dev.aurelium.slate.util.MapParser;
import fr.minuskube.inv.content.SlotPos;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class MenuItemParser extends MapParser {

    protected final Slate slate;
    protected final ConfigurateItemParser itemParser;

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
