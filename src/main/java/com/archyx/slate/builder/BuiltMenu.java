package com.archyx.slate.builder;

import com.archyx.slate.Slate;
import com.archyx.slate.function.PropertyProvider;
import com.archyx.slate.info.PlaceholderInfo;
import com.archyx.slate.function.ItemReplacer;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.function.PageProvider;
import com.archyx.slate.util.LoreUtil;
import com.archyx.slate.util.TextUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public record BuiltMenu(
        Map<String, BuiltItem> items,
        Map<String, BuiltTemplate<?>> templates,
        Map<String, BuiltComponent<?>> components,
        Map<String, ItemReplacer> titleReplacers,
        ItemReplacer titleAnyReplacer,
        PageProvider pageProvider,
        PropertyProvider propertyProvider
) {

    public static BuiltMenu createEmpty() {
        return new BuiltMenu(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), p -> null, m -> 1, m -> new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public <T> BuiltTemplate<T> getTemplate(String name, Class<T> contextType) {
        BuiltTemplate<?> template = templates.get(name);
        if (template != null && template.contextType().equals(contextType)) {
            return (BuiltTemplate<T>) template;
        }
        return BuiltTemplate.createEmpty(contextType);
    }

    @SuppressWarnings("unchecked")
    public <T> BuiltComponent<T> getComponent(String name, Class<T> contextType) {
        BuiltComponent<?> component = components.get(name);
        if (component != null && component.contextType().equals(contextType)) {
            return (BuiltComponent<T>) component;
        }
        return BuiltComponent.createEmpty(contextType);
    }

    public String applyTitleReplacers(String input, Slate slate, Player player, ActiveMenu activeMenu) {
        PlaceholderType type = PlaceholderType.TITLE;
        input = slate.getGlobalOptions().applyGlobalReplacers(input, slate, player, activeMenu, type);
        for (Entry<String, ItemReplacer> entry : titleReplacers.entrySet()) {
            String placeholder = entry.getKey();
            PlaceholderData data = new PlaceholderData(type, LoreUtil.getStyle(input), null);

            String replaced = entry.getValue().replace(new PlaceholderInfo(slate, player, placeholder, activeMenu, data));
            if (replaced != null) {
                input = TextUtil.replace(input, "{" + placeholder + "}", replaced);
            }
        }
        // Detect placeholders and replace with anyReplacer
        String[] placeholders = TextUtil.substringsBetween(input, "{", "}");
        if (placeholders != null) {
            PlaceholderData data = new PlaceholderData(type, LoreUtil.getStyle(input), null);
            for (String placeholder : placeholders) {
                String replaced = titleAnyReplacer.replace(new PlaceholderInfo(slate, player, placeholder, activeMenu, data));
                if (replaced != null) {
                    input = TextUtil.replace(input, "{" + placeholder + "}", replaced);
                }
            }
        }
        return input;
    }

}
