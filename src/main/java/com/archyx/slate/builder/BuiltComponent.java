package com.archyx.slate.builder;

import com.archyx.slate.Slate;
import com.archyx.slate.component.ComponentData;
import com.archyx.slate.function.ComponentInstances;
import com.archyx.slate.function.ComponentReplacer;
import com.archyx.slate.function.ComponentVisibility;
import com.archyx.slate.info.ComponentPlaceholderInfo;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.lore.ListData;
import com.archyx.slate.lore.LoreInterpreter;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.util.LoreUtil;
import com.archyx.slate.util.Pair;
import com.archyx.slate.util.TextUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public record BuiltComponent<T>(
        Class<T> contextType,
        Map<String, ComponentReplacer<T>> replacers,
        ComponentReplacer<T> anyReplacer,
        ComponentVisibility<T> visibility,
        ComponentInstances<T> instances
) {

    public static <T> BuiltComponent<T> createEmpty(Class<T> contextType) {
        return new BuiltComponent<>(contextType, new HashMap<>(), p -> null, t -> true, t -> 1);
    }

    public String applyReplacers(String input, Slate slate, Player player, ActiveMenu activeMenu, ComponentData componentData, T value) {
        input = slate.getGlobalOptions().applyGlobalReplacers(input, slate, player, activeMenu, PlaceholderType.LORE);
        // Detect placeholders
        String[] placeholders = TextUtil.substringsBetween(input, "{", "}");
        if (placeholders != null) {
            String style = LoreUtil.getStyle(input);
            for (String placeholder : placeholders) {
                // Get list data
                Pair<String, ListData> pair = LoreInterpreter.detectListPlaceholder(placeholder);
                PlaceholderData data = new PlaceholderData(PlaceholderType.LORE, style, pair.second());

                String coreName = pair.first(); // The name of the placeholder without list data formatting
                var info = new ComponentPlaceholderInfo<>(slate, player, coreName, activeMenu, data, componentData, value);

                // Apply single replacers
                for (Entry<String, ComponentReplacer<T>> entry : replacers.entrySet()) {
                    if (!entry.getKey().equals(coreName)) continue;
                    // Replacer target string matches current placeholder name
                    String replaced = entry.getValue().replace(info);
                    if (replaced != null) {
                        input = TextUtil.replace(input, "{" + placeholder + "}", replaced);
                    }
                }
                // Apply anyReplacer
                String replaced = anyReplacer.replace(info);
                if (replaced != null) {
                    input = TextUtil.replace(input, "{" + placeholder + "}", replaced);
                }
            }
        }
        return input;
    }

}
