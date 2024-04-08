package dev.aurelium.slate.builder;

import dev.aurelium.slate.action.click.ClickAction;
import dev.aurelium.slate.function.ItemClicker;
import dev.aurelium.slate.function.ItemModifier;
import dev.aurelium.slate.function.ItemReplacer;
import dev.aurelium.slate.function.MenuListener;
import dev.aurelium.slate.info.ItemInfo;

import java.util.HashMap;
import java.util.Map;

public class ItemBuilder {

    private final Map<String, ItemReplacer> replacers = new HashMap<>();
    private ItemReplacer anyReplacer = p -> null; // Default anyReplacer doesn't replace by returning null
    private final Map<ClickAction, ItemClicker> clickers = new HashMap<>();
    private ItemModifier modifier = ItemInfo::item;
    private MenuListener initListener = m -> {};

    public static ItemBuilder builder() {
        return new ItemBuilder();
    }

    public ItemBuilder replace(String from, ItemReplacer replacer) {
        replacers.put(from, replacer);
        return this;
    }

    public ItemBuilder replaceAny(ItemReplacer replacer) {
        anyReplacer = replacer;
        return this;
    }

    public ItemBuilder onClick(ItemClicker clicker) {
        clickers.put(ClickAction.ANY, clicker);
        return this;
    }

    public ItemBuilder onClick(ClickAction action, ItemClicker clicker) {
        clickers.put(action, clicker);
        return this;
    }

    public ItemBuilder modify(ItemModifier modifier) {
        this.modifier = modifier;
        return this;
    }

    public ItemBuilder initialize(MenuListener listener) {
        this.initListener = listener;
        return this;
    }

    public BuiltItem build() {
        return new BuiltItem(replacers, anyReplacer, clickers, modifier, initListener);
    }

}
