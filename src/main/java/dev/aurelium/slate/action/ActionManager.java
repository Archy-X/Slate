package dev.aurelium.slate.action;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.parser.CommandActionParser;
import dev.aurelium.slate.action.parser.MenuActionParser;
import dev.aurelium.slate.util.MapParser;
import dev.aurelium.slate.util.YamlLoader;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;

public class ActionManager extends MapParser {

    private final Slate slate;

    public ActionManager(Slate slate) {
        this.slate = slate;
    }

    public List<Action> parseActions(ConfigurationNode config, String menuName) {
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
                slate.getPlugin().getLogger().warning("Error parsing action in menu " + menuName + " at path " + YamlLoader.toDotString(config.path()) + ".[" + index + "], see below for error:");
                e.printStackTrace();
            }
            index++;
        }
        return actions;
    }

}
