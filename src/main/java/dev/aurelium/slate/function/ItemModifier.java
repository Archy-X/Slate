package dev.aurelium.slate.function;

import dev.aurelium.slate.info.ItemInfo;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ItemModifier {

    ItemStack modify(ItemInfo info);

}
