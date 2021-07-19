package com.archyx.slate.menu;

import com.archyx.slate.Slate;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.SingleItem;
import com.archyx.slate.item.TemplateItem;
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

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MenuInventory implements InventoryProvider {

    private final Slate slate;
    private final ConfigurableMenu menu;
    private Map<String, Consumer<ItemClickData>> itemListeners;
    private Map<String, BiConsumer<ItemClickData, ?>> templateListeners;

    public MenuInventory(Slate slate, ConfigurableMenu menu) {
        this.slate = slate;
        this.menu = menu;
        this.itemListeners = new HashMap<>();
        this.templateListeners = new HashMap<>();
    }

    public void addItemClickListener(String itemName, Consumer<ItemClickData> listener) {
        itemListeners.put(itemName, listener);
    }

    public <C> void addTemplateClickListener(String templateName, C context, BiConsumer<ItemClickData, C> listener) {
        templateListeners.put(templateName, listener);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        // Allow provider to add listeners and custom behavior
        MenuProvider provider = menu.getProvider();
        if (provider != null) {
            provider.onOpen(player, this);
        }
        // Place items
        for (MenuItem menuItem : menu.getItems().values()) {
            if (menuItem instanceof SingleItem) { // Create single item
                addSingleItem((SingleItem) menuItem, contents, player);
            } else if (menuItem instanceof TemplateItem) { // Create template item
                addTemplateItem((TemplateItem<?>) menuItem, contents, player);
            }
        }
    }

    private void addSingleItem(SingleItem item, InventoryContents contents, Player player) {
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
        Consumer<ItemClickData> listener = itemListeners.get(item.getName());
        if (listener != null) { // If listener is defined
            contents.set(item.getPosition(), ClickableItem.from(itemStack, listener));
        } else {
            contents.set(item.getPosition(), ClickableItem.empty(itemStack));
        }
    }

    private <C> void addTemplateItem(TemplateItem<C> templateItem, InventoryContents contents, Player player) {
        TemplateItemProvider<C> provider = templateItem.getProvider();

        Set<C> contexts;
        if (provider != null) {
            contexts = provider.getDefinedContexts();
        } else {
            contexts = templateItem.getBaseItems().keySet();
        }

        for (C context : contexts) {
            ItemStack itemStack = templateItem.getBaseItems().get(context);
            if (itemStack == null) {
                itemStack = templateItem.getDefaultBaseItem();
                if (itemStack == null) {
                    continue;
                }
            }
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                String displayName = templateItem.getDisplayName();
                if (displayName != null) {
                    // Replace display name placeholders
                    if (provider != null) {
                        for (String placeholder : StringUtils.substringsBetween(displayName, "{", "}")) {
                            displayName = TextUtil.replace(placeholder, "{" + placeholder + "}", provider.replacePlaceholder(placeholder, player, context));
                        }
                    }
                    meta.setDisplayName(displayName);
                }
                List<String> lore = templateItem.getLore();
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
            BiConsumer<ItemClickData, ?> listener = templateListeners.get(templateItem.getName());
            if (listener != null) { // If listener is defined
                // contents.set(item.getPosition(), ClickableItem.from(itemStack, listener));
            } else {
                // contents.set(item.getPosition(), ClickableItem.empty(itemStack));
            }
        }
    }

}
