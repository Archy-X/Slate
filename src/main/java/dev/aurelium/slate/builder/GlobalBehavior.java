package dev.aurelium.slate.builder;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.function.LocaleProvider;
import dev.aurelium.slate.info.PlaceholderInfo;
import dev.aurelium.slate.function.ItemReplacer;
import dev.aurelium.slate.item.provider.PlaceholderData;
import dev.aurelium.slate.item.provider.PlaceholderType;
import dev.aurelium.slate.lore.ListData;
import dev.aurelium.slate.lore.LoreInterpreter;
import dev.aurelium.slate.menu.ActiveMenu;
import dev.aurelium.slate.util.LoreUtil;
import dev.aurelium.slate.util.Pair;
import dev.aurelium.slate.util.TextUtil;
import org.bukkit.entity.Player;

import java.util.Set;

public record GlobalBehavior(
        Set<ItemReplacer> globalReplacers,
        LocaleProvider localeProvider
) {

    public String applyGlobalReplacers(String input, Slate slate, Player player, ActiveMenu activeMenu, PlaceholderType type) {
        String[] placeholders = TextUtil.substringsBetween(input, "{", "}");
        if (placeholders != null) {
            String style = LoreUtil.getStyle(input);
            for (String placeholder : placeholders) {
                Pair<String, ListData> pair = LoreInterpreter.detectListPlaceholder(placeholder);
                PlaceholderData data = new PlaceholderData(type, style, pair.second());
                for (ItemReplacer replacer : globalReplacers) {
                    String replaced = replacer.replace(new PlaceholderInfo(slate, player, pair.first(), activeMenu, data));
                    if (replaced != null) {
                        input = TextUtil.replace(input, "{" + placeholder + "}", replaced);
                    }
                }
            }
        }
        return input;
    }

}
