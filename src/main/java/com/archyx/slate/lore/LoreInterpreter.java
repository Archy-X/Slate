package com.archyx.slate.lore;

import com.archyx.slate.Slate;
import com.archyx.slate.component.ComponentProvider;
import com.archyx.slate.component.MenuComponent;
import com.archyx.slate.item.provider.*;
import com.archyx.slate.lore.type.ComponentLore;
import com.archyx.slate.lore.type.TextLore;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.util.LoreUtil;
import com.archyx.slate.util.Pair;
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
                ComponentLore componentLore = (ComponentLore) line;
                List<String> list = interpretComponent(componentLore, player, activeMenu);
                if (list != null) {
                    lore.addAll(list);
                }
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
                ComponentLore componentLore = (ComponentLore) line;
                List<String> list = interpretComponent(componentLore, player, activeMenu, context);
                if (list != null) {
                    lore.addAll(list);
                }
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
                    Pair<String, String> pair = detectListPlaceholder(placeholder);

                    String replacedLine = provider.onPlaceholderReplace(pair.first(), player, activeMenu, new PlaceholderData(PlaceholderType.LORE, textLore.getStyles().getStyle(0), pair.second()));
                    text = TextUtil.replace(text, "{" + placeholder + "}", replacedLine);
                }
            }
        }
        return replaceAndWrap(textLore, player, text);
    }

    private <T> String interpretTextLore(TextLore textLore, @Nullable TemplateItemProvider<T> provider, Player player, ActiveMenu activeMenu, T context) {
        String text = textLore.getText();
        if (provider != null) { // Replace lore placeholders
            String[] placeholders = TextUtil.substringsBetween(text, "{", "}");
            if (placeholders != null) {
                for (String placeholder : placeholders) {
                    Pair<String, String> pair = detectListPlaceholder(placeholder);

                    String replacedLine = provider.onPlaceholderReplace(pair.first(), player, activeMenu, new PlaceholderData(PlaceholderType.LORE, textLore.getStyles().getStyle(0), pair.second()), context);
                    text = TextUtil.replace(text, "{" + placeholder + "}", replacedLine);
                }
            }
        }
        return replaceAndWrap(textLore, player, text);
    }

    private <T> String interpretTextLore(TextLore textLore, @Nullable ComponentProvider provider, Player player, ActiveMenu activeMenu, T context) {
        String text = textLore.getText();
        if (provider != null) { // Replace lore placeholders
            String[] placeholders = TextUtil.substringsBetween(text, "{", "}");
            if (placeholders != null) {
                for (String placeholder : placeholders) {
                    Pair<String, String> pair = detectListPlaceholder(placeholder);

                    String replacedLine = provider.onPlaceholderReplace(pair.first(), player, activeMenu, new PlaceholderData(PlaceholderType.LORE, textLore.getStyles().getStyle(0), pair.second()), context);
                    text = TextUtil.replace(text, "{" + placeholder + "}", replacedLine);
                }
            }
        }
        return replaceAndWrap(textLore, player, text);
    }

    private Pair<String, String> detectListPlaceholder(String placeholder) {
        if (!placeholder.endsWith("]")) return new Pair<>(placeholder, null);
        // Find the index of the opening bracket closest to the end of the placeholder
        int openIndex = placeholder.lastIndexOf("[");
        // No matching opening bracket
        if (openIndex == -1) {
            return new Pair<>(placeholder, null);
        }
        // Get the substring between the brackets
        String insert = placeholder.substring(openIndex + 1, placeholder.length() - 1);

        return new Pair<>(placeholder.substring(0, openIndex), insert);
    }

    private <T> List<String> interpretComponent(ComponentLore lore, Player player, ActiveMenu activeMenu, T context) {
        // Choose the component if multiple
        String componentName = lore.getComponent();
        MenuComponent component = activeMenu.getComponents().get(componentName);
        if (component == null) {
            return null;
        }
        ComponentProvider componentProvider = slate.getMenuManager().constructComponent(componentName, activeMenu.getName());
        // Decide whether component should be visible
        if (componentProvider != null && !componentProvider.shouldShow(player, activeMenu, context)) {
            return null;
        }
        // Interpret each line
        List<String> list = new ArrayList<>();
        for (LoreLine line : component.getLore()) {
            if (!(line instanceof TextLore)) { // Lines in a component must be TextLore
                continue;
            }
            list.add(interpretTextLore((TextLore) line, componentProvider, player, activeMenu, context));
        }
        return list;
    }

    private List<String> interpretComponent(ComponentLore lore, Player player, ActiveMenu activeMenu) {
        // Choose the component if multiple
        String componentName = lore.getComponent();
        MenuComponent component = activeMenu.getComponents().get(componentName);
        if (component == null) {
            return null;
        }
        ComponentProvider componentProvider = slate.getMenuManager().constructComponent(componentName, activeMenu.getName());
        // Decide whether component should be visible
        if (componentProvider != null && !componentProvider.shouldShow(player, activeMenu, null)) {
            return null;
        }
        // Interpret each line
        List<String> list = new ArrayList<>();
        for (LoreLine line : component.getLore()) {
            if (!(line instanceof TextLore)) { // Lines in a component must be TextLore
                continue;
            }
            list.add(interpretTextLore((TextLore) line, componentProvider, player, activeMenu, null));
        }
        return list;
    }

    private String replaceAndWrap(TextLore textLore, Player player, String text) {
        if (slate.isPlaceholderAPIEnabled()) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        if (textLore.shouldWrap()) {
            String style = textLore.getStyles().getStyle(textLore.getWrapStyle());
            text = style + LoreUtil.wrapLore(text, slate.getLoreWrappingWidth(), "\n" + style);
        }
        return text;
    }

    private List<String> applyColorToLore(List<String> lore) {
        List<String> appliedLore = new ArrayList<>();
        for (String line : lore) {
            appliedLore.add(TextUtil.applyColor(line));
        }
        return appliedLore;
    }

}
