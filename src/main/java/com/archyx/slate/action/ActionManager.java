package com.archyx.slate.action;

import com.archyx.slate.Slate;
import com.archyx.slate.action.parser.CommandActionParser;
import com.archyx.slate.action.parser.MenuActionParser;
import com.archyx.slate.util.MapParser;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;

public class ActionManager extends MapParser {

    private final Slate slate;

    public ActionManager(Slate slate) {
        this.slate = slate;
    }

    public List<Action> parseActions(ConfigurationNode config, String menuName, String itemName) {
        List<Action> actions = new ArrayList<>();
        int index = 0;
        for (ConfigurationNode actionNode : config.childrenList()) {
            try {
                String type = Objects.requireNonNull(actionNode.node("type").getString()).toLowerCase(Locale.ROOT);
                if (type.equals("command")) {
                    actions.add(new CommandActionParser(slate).parse(actionNode));
                } else if (type.equals("menu")) {
                    actions.add(new MenuActionParser(slate).parse(actionNode));
                } else {
                    throw new IllegalArgumentException("Action with type " + type + " not found");
                }
            } catch (RuntimeException e) {
                slate.getPlugin().getLogger().warning("Error parsing action in menu " + menuName + " at path " + itemName + ".[" + index + "], see below for error:");
                e.printStackTrace();
            }
            index++;
        }
        return actions;
    }

}
