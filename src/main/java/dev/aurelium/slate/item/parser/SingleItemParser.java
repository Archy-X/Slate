package dev.aurelium.slate.item.parser;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.builder.SingleItemBuilder;
import dev.aurelium.slate.menu.MenuLoader;
import dev.aurelium.slate.util.Validate;
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

        builder.options(MenuLoader.loadOptions(section));

        return builder.build();
    }

}
