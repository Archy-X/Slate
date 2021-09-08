package com.archyx.slate.fill;

import com.archyx.slate.Slate;
import com.archyx.slate.item.parser.MenuItemParser;
import com.archyx.slate.menu.MenuProvider;
import org.bukkit.configuration.ConfigurationSection;

public class FillItemParser extends MenuItemParser {

    public FillItemParser(Slate slate) {
        super(slate);
    }

    @Override
    public FillItem parse(ConfigurationSection section, String menuName, MenuProvider menuProvider) {
        return new FillItem(slate, parseBaseItem(section));
    }
}
