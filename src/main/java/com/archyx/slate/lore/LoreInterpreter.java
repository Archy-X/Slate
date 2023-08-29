package com.archyx.slate.lore;

import com.archyx.slate.Slate;
import com.archyx.slate.component.ComponentData;
import com.archyx.slate.component.ComponentProvider;
import com.archyx.slate.component.MenuComponent;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.item.provider.TemplateItemProvider;
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
import java.util.Map;

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
                    Pair<String, ListData> pair = detectListPlaceholder(placeholder);

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
                    Pair<String, ListData> pair = detectListPlaceholder(placeholder);

                    String replacedLine = provider.onPlaceholderReplace(pair.first(), player, activeMenu, new PlaceholderData(PlaceholderType.LORE, textLore.getStyles().getStyle(0), pair.second()), context);
                    text = TextUtil.replace(text, "{" + placeholder + "}", replacedLine);
                }
            }
        }
        return replaceAndWrap(textLore, player, text);
    }

    private <T> String interpretTextLore(TextLore textLore, @Nullable ComponentProvider provider, Player player, ActiveMenu activeMenu, ComponentData componentData, T context) {
        String text = textLore.getText();
        if (provider != null) { // Replace lore placeholders
            String[] placeholders = TextUtil.substringsBetween(text, "{", "}");
            if (placeholders != null) {
                for (String placeholder : placeholders) {
                    Pair<String, ListData> pair = detectListPlaceholder(placeholder);

                    String replacedLine = provider.onPlaceholderReplace(pair.first(), player, activeMenu, new PlaceholderData(PlaceholderType.LORE, textLore.getStyles().getStyle(0), pair.second()), componentData, context);
                    text = TextUtil.replace(text, "{" + placeholder + "}", replacedLine);
                }
            }
        }
        return replaceAndWrap(textLore, player, text);
    }

    private Pair<String, ListData> detectListPlaceholder(String placeholder) {
        if (!placeholder.endsWith("]") && !placeholder.endsWith(")")) {
            return new Pair<>(placeholder, new ListData(null, 0));
        }
        // Find the index of the opening bracket closest to the end of the placeholder
        int openBracket = placeholder.lastIndexOf("[");
        int closeBracket = placeholder.lastIndexOf("]");
        // No matching opening bracket
        if (openBracket == -1 || closeBracket == -1 || openBracket > closeBracket) {
            return new Pair<>(placeholder, new ListData(null, 0));
        }
        // Get the substring between the brackets
        String insert = placeholder.substring(openBracket + 1, closeBracket);
        // Find the index of the opening parenthesis closest to the end of the placeholder
        int openParen = placeholder.lastIndexOf("(");
        int closeParen = placeholder.lastIndexOf(")");
        // Find the interval between parenthesis
        int interval = 1;
        if (openParen != -1 && closeParen != -1 && openParen < closeParen) {
            String intervalString = placeholder.substring(openParen + 1, closeParen);
            try {
                interval = Integer.parseInt(intervalString);
            } catch (NumberFormatException ignored) {}
        }

        return new Pair<>(placeholder.substring(0, openBracket), new ListData(insert, interval));
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
        int instances = componentProvider != null ? componentProvider.getInstances(player, activeMenu, context) : 1;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < instances; i++) {
            ComponentData componentData = new ComponentData(i);
            // Interpret each line
            for (LoreLine line : component.getLore()) {
                if (!(line instanceof TextLore)) { // Lines in a component must be TextLore
                    continue;
                }
                list.add(interpretTextLore((TextLore) line, componentProvider, player, activeMenu, componentData, context));
            }
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
        int instances = componentProvider != null ? componentProvider.getInstances(player, activeMenu, null) : 1;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < instances; i++) {
            ComponentData componentData = new ComponentData(i);
            // Interpret each line
            for (LoreLine line : component.getLore()) {
                if (!(line instanceof TextLore)) { // Lines in a component must be TextLore
                    continue;
                }
                list.add(interpretTextLore((TextLore) line, componentProvider, player, activeMenu, componentData, null));
            }
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
        return applyStyleTags(textLore, text);
    }

    private String applyStyleTags(TextLore textLore, String text) {
        // Create a TagResolver for each style
        for (Map.Entry<Integer, String> entry : textLore.getStyles().getStyleMap().entrySet()) {
            String target = String.valueOf(entry.getKey());
            String style = entry.getValue();
            String styleClose = TextUtil.replace(entry.getValue(), "<", "</"); // Convert style to closing tags

            text = TextUtil.replace(text, "<" + target + ">", style); // Replace opening tag
            text = TextUtil.replace(text, "</" + target + ">", styleClose); // Replacing closing tag
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
