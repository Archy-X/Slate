package com.archyx.slate.item.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.builder.SingleItemBuilder;
import com.archyx.slate.item.provider.SingleItemProvider;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class SingleItemParser extends MenuItemParser {

    public SingleItemParser(Slate slate) {
        super(slate);
    }

    @Override
    public MenuItem parse(ConfigurationSection section, String menuName) {
        SingleItemBuilder builder = new SingleItemBuilder(slate);

        String name = section.getName();
        builder.name(name);
        builder.baseItem(parseBaseItem(section));

        String positionString = section.getString("pos");
        Validate.notNull(positionString, "Item must specify pos");
        builder.position(parsePosition(positionString));

        builder.displayName(parseDisplayName(section));
        builder.lore(parseLore(section));

        SingleItemProvider provider = slate.getMenuManager().getSingleItem(name, menuName);
        if (provider != null) {
            builder.provider(provider);
        }

        parseActions(builder, section.getValues(false), menuName, name);

        builder.options(slate.getMenuManager().loadOptions(section));

        return builder.build();
    }

}
