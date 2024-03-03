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
import com.archyx.slate.text.TextFormatter;
import com.archyx.slate.util.LoreUtil;
import com.archyx.slate.util.Pair;
import com.archyx.slate.util.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class LoreInterpreter {

    private final Slate slate;
    private final TextFormatter tf = new TextFormatter();

    public LoreInterpreter(Slate slate) {
        this.slate = slate;
    }

    @NotNull
    public List<Component> interpretLore(List<LoreLine> loreLines, @Nullable SingleItemProvider provider, Player player, ActiveMenu activeMenu) {
        List<String> lore = new ArrayList<>();
        for (LoreLine line : loreLines) {
            if (line instanceof TextLore textLore) {
                lore.add(interpretTextLore(textLore, provider, player, activeMenu));
            } else if (line instanceof ComponentLore componentLore) {
                List<String> list = interpretComponent(componentLore, player, activeMenu);
                if (list != null) {
                    lore.addAll(list);
                }
            }
        }
        lore = tf.applyNewLines(lore);
        return tf.toComponentLore(lore);
    }

    @NotNull
    public <T> List<Component> interpretLore(List<LoreLine> loreLines, @Nullable TemplateItemProvider<T> provider, Player player, ActiveMenu activeMenu, T context) {
        List<String> lore = new ArrayList<>();
        for (LoreLine line : loreLines) {
            if (line instanceof TextLore textLore) {
                lore.add(interpretTextLore(textLore, provider, player, activeMenu, context));
            } else if (line instanceof ComponentLore componentLore) {
                List<String> list = interpretComponent(componentLore, player, activeMenu, context);
                if (list != null) {
                    lore.addAll(list);
                }
            }
        }
        lore = tf.applyNewLines(lore);
        return tf.toComponentLore(lore);
    }

    private String interpretTextLore(TextLore textLore, @Nullable SingleItemProvider provider, Player player, ActiveMenu activeMenu) {
        String text = textLore.getText();
        if (provider != null) { // Replace lore placeholders
            String[] placeholders = TextUtil.substringsBetween(text, "{", "}");
            if (placeholders != null) {
                for (String placeholder : placeholders) {
                    Pair<String, ListData> pair = detectListPlaceholder(placeholder);

                    String replacedLine = provider.onPlaceholderReplace(pair.first(), player, activeMenu, new PlaceholderData(PlaceholderType.LORE, textLore.getStyles().getStyle(0), pair.second()));
                    if (replacedLine != null) {
                        text = TextUtil.replace(text, "{" + placeholder + "}", replacedLine);
                    }
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
                    if (replacedLine != null) {
                        text = TextUtil.replace(text, "{" + placeholder + "}", replacedLine);
                    }
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
                    if (replacedLine != null) {
                        text = TextUtil.replace(text, "{" + placeholder + "}", replacedLine);
                    }
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
            if (textLore.isSmartWrap()) { // Detect tags inside string to use as insertions
                String firstStyle = textLore.getStyles().getStyle(textLore.getWrapStyle());
                AtomicReference<String> prevStyle = new AtomicReference<>(firstStyle);
                text = firstStyle + textLore.getWrapIndent() + LoreUtil.wrapLore(text, slate.getLoreWrappingWidth(), textLore, (line, lore) -> {
                    // Find the last style tag in the line
                    int lastStartIndex = 0;
                    int tagLength = 0;
                    for (int index : lore.getStyles().getStyleMap().keySet()) {
                        String tag = "<" + index + ">";
                        int startIndex = line.lastIndexOf(tag);
                        if (startIndex >= lastStartIndex) {
                            lastStartIndex = startIndex;
                            tagLength = tag.length();
                        }
                    }
                    String style;
                    if (tagLength > 0) {
                        style = line.substring(lastStartIndex, lastStartIndex + tagLength);
                        prevStyle.set(style);
                    } else {
                        style = prevStyle.get();
                    }
                    return "\n" + lore.getWrapIndent() + style;
                });
            } else { // Use the same string for all insertions
                String style = textLore.getStyles().getStyle(textLore.getWrapStyle());
                text = style + textLore.getWrapIndent() + LoreUtil.wrapLore(text, slate.getLoreWrappingWidth(), "\n" + textLore.getWrapIndent() + style);
            }
        }
        return applyStyleTags(textLore, text);
    }

    private String applyStyleTags(TextLore textLore, String text) {
        // Create a TagResolver for each style
        boolean[] usedTags = new boolean[10];
        for (Map.Entry<Integer, String> entry : textLore.getStyles().getStyleMap().entrySet()) {
            String target = String.valueOf(entry.getKey());
            String style = entry.getValue();
            String styleClose = TextUtil.replace(entry.getValue(), "<", "</"); // Convert style to closing tags

            text = TextUtil.replace(text, "<" + target + ">", style); // Replace opening tag
            text = TextUtil.replace(text, "</" + target + ">", styleClose); // Replacing closing tag

            // Mark as used
            int index = entry.getKey();
            if (index < 10) {
                usedTags[index] = true;
            }
        }
        // Remove unused tags
        for (int i = 0; i < usedTags.length; i++) {
            if (usedTags[i]) continue; // Ignore used tags
            text = TextUtil.replace(text, "<" + i + ">", "");
            text = TextUtil.replace(text, "</" + i + ">", "");
        }
        return text;
    }

}
