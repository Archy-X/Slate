package dev.aurelium.slate.item.parser;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.item.MenuItem;
import dev.aurelium.slate.item.builder.SingleItemBuilder;
import dev.aurelium.slate.util.Validate;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;
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

        ConfigurationNode posNode = section.node("pos");
        if (posNode.isList()) { // Multiple positions
            List<SlotPos> positions = new ArrayList<>();
            // Parse each position and add to list
            for (ConfigurationNode entry : posNode.childrenList()) {
                String positionString = entry.getString();
                if (positionString == null) continue;

                positions.add(parsePosition(positionString));
            }
            builder.positions(positions);
        } else { // Single position
            String positionString = posNode.getString();
            Validate.notNull(positionString, "Item must specify pos");
            builder.positions(List.of(parsePosition(positionString)));
        }

        parseCommonOptions(builder, section, menuName);

        return builder.build();
    }

}
