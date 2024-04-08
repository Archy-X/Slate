package dev.aurelium.slate.item.provider;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface KeyedItemProvider {

    @Nullable ItemStack getItem(String key);

}
