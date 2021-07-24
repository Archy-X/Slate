package com.archyx.slate.menu;

import com.archyx.slate.Slate;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.SingleItem;
import com.archyx.slate.item.TemplateItem;
import com.archyx.slate.item.active.ActiveItem;
import com.archyx.slate.item.active.ActiveSingleItem;
import com.archyx.slate.item.active.ActiveTemplateItem;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.util.TextUtil;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.ItemClickData;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class MenuInventory implements InventoryProvider {

    private final Slate slate;
    private final ConfigurableMenu menu;
    private final ActiveMenu activeMenu;
    private final Map<String, ActiveItem> activeItems;

    public MenuInventory(Slate slate, ConfigurableMenu menu) {
        this.slate = slate;
        this.menu = menu;
        this.activeItems = new HashMap<>();
        this.activeMenu = new ActiveMenu(this);
    }

    public ConfigurableMenu getMenu() {
        return menu;
    }

    public ActiveMenu getActiveMenu() {
        return activeMenu;
    }

    @Nullable
    public ActiveItem getActiveItem(String itemName) {
        return activeItems.get(itemName);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        // Add active items
        for (MenuItem menuItem : menu.getItems().values()) {
            ActiveItem activeItem;
            if (menuItem instanceof SingleItem) {
                activeItem = new ActiveSingleItem((SingleItem) menuItem);
            } else if (menuItem instanceof TemplateItem) {
                TemplateItem<?> templateItem = (TemplateItem<?>) menuItem;
                activeItem = new ActiveTemplateItem<>(templateItem);
            } else {
                continue;
            }
            activeItems.put(menuItem.getName(), activeItem);
        }
        // Allow provider to add listeners and custom behavior
        MenuProvider provider = menu.getProvider();
        if (provider != null) {
            provider.onOpen(player, activeMenu);
        }
        // Place items
        for (ActiveItem activeItem : activeItems.values()) {
            if (activeItem instanceof ActiveSingleItem) { // Create single item
                addSingleItem((ActiveSingleItem) activeItem, contents, player);
            } else if (activeItem instanceof ActiveTemplateItem) { // Create template item
                addTemplateItem((ActiveTemplateItem<?>) activeItem, contents, player);
            }
        }
    }

    private void addSingleItem(ActiveSingleItem activeItem, InventoryContents contents, Player player) {
        SingleItem item = activeItem.getItem();
        SingleItemProvider provider = item.getProvider();

        ItemStack itemStack = item.getBaseItem();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            String displayName = item.getDisplayName();
            if (displayName != null) {
                // Replace display name placeholders
                if (provider != null) {
                    for (String placeholder : StringUtils.substringsBetween(displayName, "{", "}")) {
                        displayName = TextUtil.replace(placeholder, "{" + placeholder + "}", provider.replacePlaceholder(placeholder, player));
                    }
                }
                meta.setDisplayName(displayName);
            }
            List<String> lore = item.getLore();
            if (lore != null && lore.size() > 0) {
                // Replace lore placeholders
                if (provider != null) {
                    List<String> replacedLore = new ArrayList<>();
                    for (String line : lore) {
                        for (String placeholder : StringUtils.substringsBetween(line, "{", "}")) {
                            replacedLore.add(TextUtil.replace(placeholder, "{" + placeholder + "}", provider.replacePlaceholder(placeholder, player)));
                        }
                    }
                    lore = replacedLore;
                }
                meta.setLore(lore);
            }
            itemStack.setItemMeta(meta);
        }

        // Add item to inventory
        Consumer<ItemClickData> listener = activeItem.getClickListener();
        if (listener != null) { // If listener is defined
            contents.set(item.getPosition(), ClickableItem.from(itemStack, listener));
        } else {
            contents.set(item.getPosition(), ClickableItem.empty(itemStack));
        }
    }

    private <C> void addTemplateItem(ActiveTemplateItem<C> activeItem, InventoryContents contents, Player player) {
        TemplateItem<C> item = activeItem.getItem();
        TemplateItemProvider<C> provider = item.getProvider();

        Set<C> contexts;
        if (provider != null) {
            contexts = provider.getDefinedContexts();
        } else {
            contexts = item.getBaseItems().keySet();
        }

        for (C context : contexts) {
            ItemStack itemStack = item.getBaseItems().get(context);
            if (itemStack == null) {
                itemStack = item.getDefaultBaseItem();
                if (itemStack == null) {
                    continue;
                }
            }
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                String displayName = item.getDisplayName();
                if (displayName != null) {
                    // Replace display name placeholders
                    if (provider != null) {
                        for (String placeholder : StringUtils.substringsBetween(displayName, "{", "}")) {
                            displayName = TextUtil.replace(placeholder, "{" + placeholder + "}", provider.replacePlaceholder(placeholder, player, context));
                        }
                    }
                    meta.setDisplayName(displayName);
                }
                List<String> lore = item.getLore();
                if (lore != null && lore.size() > 0) {
                    // Replace lore placeholders
                    if (provider != null) {
                        List<String> replacedLore = new ArrayList<>();
                        for (String line : lore) {
                            for (String placeholder : StringUtils.substringsBetween(line, "{", "}")) {
                                replacedLore.add(TextUtil.replace(placeholder, "{" + placeholder + "}", provider.replacePlaceholder(placeholder, player, context)));
                            }
                        }
                        lore = replacedLore;
                    }
                    meta.setLore(lore);
                }
                itemStack.setItemMeta(meta);
            }
            // Add item to inventory
            Consumer<ItemClickData> listener = activeItem.bindContext(context);
            if (listener != null) { // If listener is defined
                contents.set(item.getPosition(context), ClickableItem.from(itemStack, listener));
            } else {
                contents.set(item.getPosition(context), ClickableItem.empty(itemStack));
            }
        }
    }

}
