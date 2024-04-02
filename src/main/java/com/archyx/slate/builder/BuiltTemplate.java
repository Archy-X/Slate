package com.archyx.slate.builder;

import com.archyx.slate.Slate;
import com.archyx.slate.action.click.ClickAction;
import com.archyx.slate.function.*;
import com.archyx.slate.info.ItemInfo;
import com.archyx.slate.item.TemplateClick;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.text.TemplateTextReplacer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public record BuiltTemplate<T>(
        Class<T> contextType,
        Map<String, TemplateReplacer<T>> replacers,
        TemplateReplacer<T> anyReplacer,
        Map<ClickAction, TemplateClicker<T>> clickers,
        TemplateModifier<T> modifier,
        DefinedContexts<T> definedContexts,
        TemplateSlot<T> slotProvider,
        MenuListener initListener,
        ContextListener<T> contextListener
) {

    public static <T> BuiltTemplate<T> createEmpty(Class<T> contextType) {
        return new BuiltTemplate<>(contextType, new HashMap<>(), p -> null, new HashMap<>(), ItemInfo::item,
                m -> new HashSet<>(), t -> null, m -> {}, t -> {});
    }

    public String applyReplacers(String input, Slate slate, Player player, ActiveMenu activeMenu, PlaceholderType type, T value) {
        var replacer = new TemplateTextReplacer<>(slate, replacers, anyReplacer);
        return replacer.applyReplacers(input, player, activeMenu, type, value);
    }

    public void handleClick(Set<ClickAction> actions, TemplateClick<T> templateClick) {
        for (Entry<ClickAction, TemplateClicker<T>> entry : clickers.entrySet()) {
            if (actions.contains(entry.getKey())) { // Only click if click action matches a defined clicker
                entry.getValue().click(templateClick);
            }
        }
    }

}
