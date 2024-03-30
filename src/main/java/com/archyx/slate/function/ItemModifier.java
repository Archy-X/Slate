package com.archyx.slate.function;

import com.archyx.slate.info.ItemInfo;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ItemModifier {

    ItemStack modify(ItemInfo info);

}
