package com.archyx.slate.item.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.builder.SingleItemBuilder;
import com.archyx.slate.util.Validate;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Objects;

public class SingleItemParser extends MenuItemParser {

    public SingleItemParser(Slate slate) {
        super(slate);
    }

    @Override
    public MenuItem parse(ConfigurationNode section, String menuName) {
        SingleItemBuilder builder = new SingleItemBuilder(slate);

        String name = (String) Objects.requireNonNull(section.key());
        builder.name(name);
        builder.baseItem(itemParser.parseBaseItem(section));

        String positionString = section.node("pos").getString();
        Validate.notNull(positionString, "Item must specify pos");
        builder.position(parsePosition(positionString));

        builder.displayName(itemParser.parseDisplayName(section));
        builder.lore(itemParser.parseLore(section));

        parseActions(builder, section, menuName, name);

        builder.options(slate.getMenuManager().loadOptions(section));

        return builder.build();
    }

}
