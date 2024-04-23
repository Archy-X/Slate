package dev.aurelium.slate.function;

import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

@FunctionalInterface
public interface ItemMetaParser {

    ItemStack parse(ItemStack item, ConfigurationNode config);

}
