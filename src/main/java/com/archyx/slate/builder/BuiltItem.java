package com.archyx.slate.builder;

import com.archyx.slate.Slate;
import com.archyx.slate.action.click.ClickAction;
import com.archyx.slate.function.ItemClicker;
import com.archyx.slate.function.ItemModifier;
import com.archyx.slate.function.ItemReplacer;
import com.archyx.slate.info.ItemInfo;
import com.archyx.slate.info.PlaceholderInfo;
import com.archyx.slate.item.*;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.lore.ListData;
import com.archyx.slate.lore.LoreInterpreter;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.util.LoreUtil;
import com.archyx.slate.util.Pair;
import com.archyx.slate.util.TextUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public record BuiltItem(
        Map<String, ItemReplacer> replacers,
        ItemReplacer anyReplacer,
        Map<ClickAction, ItemClicker> clickers,
        @NotNull ItemModifier modifier,
        boolean enableProvider
) {

    public static BuiltItem createEmpty() {
        return new BuiltItem(new HashMap<>(), p -> null, new HashMap<>(), ItemInfo::item, true);
    }

    public String applyReplacers(String input, Slate slate, Player player, ActiveMenu activeMenu, PlaceholderType type) {
        input = slate.getGlobalOptions().applyGlobalReplacers(input, player, activeMenu, type);
        // Detect placeholders
        String[] placeholders = TextUtil.substringsBetween(input, "{", "}");
        if (placeholders != null) {
            String style = LoreUtil.getStyle(input);
            for (String placeholder : placeholders) {
                // Get list data
                Pair<String, ListData> pair = LoreInterpreter.detectListPlaceholder(placeholder);
                PlaceholderData data = new PlaceholderData(type, style, pair.second());

                String coreName = pair.first(); // The name of the placeholder without list data formatting
                PlaceholderInfo info = new PlaceholderInfo(player, coreName, activeMenu, data);

                // Apply single replacers
                for (Entry<String, ItemReplacer> entry : replacers.entrySet()) {
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

    public void handleClick(Set<ClickAction> actions, ItemClick itemClick) {
        for (Entry<ClickAction, ItemClicker> entry : clickers.entrySet()) {
            if (actions.contains(entry.getKey())) { // Only click if click action matches a defined clicker
                entry.getValue().click(itemClick);
            }
        }
    }

}
