package com.archyx.slate.fill;

import com.archyx.slate.Slate;
import com.archyx.slate.item.parser.MenuItemParser;
import org.bukkit.configuration.ConfigurationSection;

public class FillItemParser extends MenuItemParser {

    public FillItemParser(Slate slate) {
        super(slate);
    }

    @Override
    public FillItem parse(ConfigurationSection section, String menuName) {
        return new FillItem(slate, parseBaseItem(section));
    }
}
