package dev.aurelium.slate.function;

import dev.aurelium.slate.info.TemplateInfo;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface TemplateModifier<T> {

    ItemStack modify(TemplateInfo<T> info);

}
