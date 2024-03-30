package com.archyx.slate.builder;

import com.archyx.slate.Slate;
import com.archyx.slate.function.ComponentInstances;
import com.archyx.slate.function.ComponentVisibility;
import com.archyx.slate.function.TemplateReplacer;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.text.TemplateTextReplacer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public record BuiltComponent<T>(
        Class<T> contextType,
        Map<String, TemplateReplacer<T>> replacers,
        TemplateReplacer<T> anyReplacer,
        ComponentVisibility<T> visibility,
        ComponentInstances<T> instances
) {

    public static <T> BuiltComponent<T> createEmpty(Class<T> contextType) {
        return new BuiltComponent<>(contextType, new HashMap<>(), p -> null, t -> true, t -> 1);
    }

    public String applyReplacers(String input, Slate slate, Player player, ActiveMenu activeMenu, T value) {
        var replacer = new TemplateTextReplacer<>(slate, replacers, anyReplacer);
        // Components are always in lore
        return replacer.applyReplacers(input, player, activeMenu, PlaceholderType.LORE, value);
    }

}
