package com.archyx.slate.function;

import com.archyx.slate.info.TemplateInfo;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface TemplateModifier<T> {

    ItemStack modify(TemplateInfo<T> info);

}
