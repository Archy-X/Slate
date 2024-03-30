package com.archyx.slate.builder;

import com.archyx.slate.info.PlaceholderInfo;
import com.archyx.slate.function.ItemReplacer;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.lore.ListData;
import com.archyx.slate.lore.LoreInterpreter;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.util.LoreUtil;
import com.archyx.slate.util.Pair;
import com.archyx.slate.util.TextUtil;
import org.bukkit.entity.Player;

import java.util.Set;

public record GlobalOptions(
        Set<ItemReplacer> globalReplacers
) {

    public String applyGlobalReplacers(String input, Player player, ActiveMenu activeMenu, PlaceholderType type) {
        String[] placeholders = TextUtil.substringsBetween(input, "{", "}");
        if (placeholders != null) {
            String style = LoreUtil.getStyle(input);
            for (String placeholder : placeholders) {
                Pair<String, ListData> pair = LoreInterpreter.detectListPlaceholder(placeholder);
                PlaceholderData data = new PlaceholderData(type, style, pair.second());
                for (ItemReplacer replacer : globalReplacers) {
                    String replaced = replacer.replace(new PlaceholderInfo(player, pair.first(), activeMenu, data));
                    if (replaced != null) {
                        input = TextUtil.replace(input, "{" + placeholder + "}", replaced);
                    }
                }
            }
        }
        return input;
    }

}
