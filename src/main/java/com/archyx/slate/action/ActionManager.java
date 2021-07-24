package com.archyx.slate.action;

import com.archyx.slate.Slate;
import com.archyx.slate.action.parser.CommandActionParser;
import com.archyx.slate.action.parser.MenuActionParser;
import com.archyx.slate.util.MapParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActionManager extends MapParser {

    private final Slate slate;

    public ActionManager(Slate slate) {
        this.slate = slate;
    }

    public List<Action> parseActions(List<Map<?, ?>> mapList, String menuName, String itemName) {
        List<Action> actions = new ArrayList<>();
        int index = 0;
        for (Map<?, ?> map : mapList) {
            try {
                String type = getString(map, "type").toLowerCase(Locale.ROOT);
                if (type.equals("command")) {
                    actions.add(new CommandActionParser(slate).parse(map));
                } else if (type.equals("menu")) {
                    actions.add(new MenuActionParser(slate).parse(map));
                } else {
                    throw new IllegalArgumentException("Action with type " + type + " not found");
                }
            } catch (IllegalArgumentException e) {
                slate.getPlugin().getLogger().warning("Error parsing action in menu " + menuName + " at path " + itemName + ".[" + index + "], see below for error:");
                e.printStackTrace();
            }
            index++;
        }
        return actions;
    }

}
