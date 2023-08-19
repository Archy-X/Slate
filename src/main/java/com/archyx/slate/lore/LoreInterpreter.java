package com.archyx.slate.lore;

import com.archyx.slate.Slate;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.lore.type.ComponentLore;
import com.archyx.slate.lore.type.TextLore;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.util.LoreUtil;
import com.archyx.slate.util.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LoreInterpreter {

    private final Slate slate;

    public LoreInterpreter(Slate slate) {
        this.slate = slate;
    }

    @NotNull
    public List<String> interpretLore(List<LoreLine> loreLines, @Nullable SingleItemProvider provider, Player player, ActiveMenu activeMenu) {
        List<String> lore = new ArrayList<>();
        for (LoreLine line : loreLines) {
            if (line instanceof TextLore) {
                TextLore textLore = (TextLore) line;
                lore.add(interpretTextLore(textLore, provider, player, activeMenu));
            } else if (line instanceof ComponentLore) {
                // TODO Interpret components
            }
        }
        lore = TextUtil.applyNewLines(lore);
        lore = applyColorToLore(lore);
        return lore;
    }

    @NotNull
    public <T> List<String> interpretLore(List<LoreLine> loreLines, @Nullable TemplateItemProvider<T> provider, Player player, ActiveMenu activeMenu, T context) {
        List<String> lore = new ArrayList<>();
        for (LoreLine line : loreLines) {
            if (line instanceof TextLore) {
                TextLore textLore = (TextLore) line;
                lore.add(interpretTextLore(textLore, provider, player, activeMenu, context));
            } else if (line instanceof ComponentLore) {
                // TODO Interpret components
            }
        }
        lore = TextUtil.applyNewLines(lore);
        lore = applyColorToLore(lore);
        return lore;
    }

    private String interpretTextLore(TextLore textLore, @Nullable SingleItemProvider provider, Player player, ActiveMenu activeMenu) {
        String text = textLore.getText();
        if (provider != null) { // Replace lore placeholders
            String[] placeholders = TextUtil.substringsBetween(text, "{", "}");
            if (placeholders != null) {
                for (String placeholder : placeholders) {
                    String replacedLine = provider.onPlaceholderReplace(placeholder, player, activeMenu, new PlaceholderData(PlaceholderType.LORE, textLore.getStyles().getStyle(0)));
                    text = TextUtil.replace(text, "{" + placeholder + "}", replacedLine);
                }
            }
        }
        return replaceAndWrap(textLore, player, text);
    }

    private String replaceAndWrap(TextLore textLore, Player player, String text) {
        if (slate.isPlaceholderAPIEnabled()) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        if (textLore.shouldWrap()) {
            String style = textLore.getStyles().getStyle(textLore.getWrapStyle());
            text = LoreUtil.wrapLore(text, slate.getLoreWrappingWidth(), "\n" + style);
        }
        return text;
    }

    private <T> String interpretTextLore(TextLore textLore, @Nullable TemplateItemProvider<T> provider, Player player, ActiveMenu activeMenu, T context) {
        String text = textLore.getText();
        if (provider != null) { // Replace lore placeholders
            String[] placeholders = TextUtil.substringsBetween(text, "{", "}");
            if (placeholders != null) {
                for (String placeholder : placeholders) {
                    String replacedLine = provider.onPlaceholderReplace(placeholder, player, activeMenu, new PlaceholderData(PlaceholderType.LORE, textLore.getStyles().getStyle(0)), context);
                    text = TextUtil.replace(text, "{" + placeholder + "}", replacedLine);
                }
            }
        }
        return replaceAndWrap(textLore, player, text);
    }

    private List<String> applyColorToLore(List<String> lore) {
        List<String> appliedLore = new ArrayList<>();
        for (String line : lore) {
            appliedLore.add(TextUtil.applyColor(line));
        }
        return appliedLore;
    }

}
