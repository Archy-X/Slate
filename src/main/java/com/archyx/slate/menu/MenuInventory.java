package com.archyx.slate.menu;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.click.ClickAction;
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
import fr.minuskube.inv.content.SlotPos;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        SlotPos pos = item.getPosition();
        addItemToInventory(item, itemStack, pos, contents, listener, player);
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
            SlotPos pos = item.getPosition(context);
            addItemToInventory(item, itemStack, pos, contents, listener, player);
        }
    }

    /**
     * Adds the menu item itself to the inventory menu and registers click listeners, both listeners and actions
     */
    private void addItemToInventory(MenuItem menuItem, ItemStack itemStack, SlotPos pos, InventoryContents contents, Consumer<ItemClickData> listener, Player player) {
        if (listener != null) { // If listener is defined
            contents.set(pos, ClickableItem.from(itemStack, c -> {
                listener.accept(c);
                executeActions(menuItem, player, contents, c);
            }));
        } else {
            contents.set(pos, ClickableItem.from(itemStack, clickData -> executeActions(menuItem, player, contents, clickData)));
        }
    }

    /**
     * Executes the configured click actions of an item
     */
    private void executeActions(MenuItem menuItem, Player player, InventoryContents contents, ItemClickData clickData) {
        // Convert click data event to InventoryClickEvent
        if (!(clickData.getEvent() instanceof InventoryClickEvent)) {
            return;
        }
        InventoryClickEvent event = (InventoryClickEvent) clickData.getEvent();

        Set<ClickAction> clickActions = getClickActions(event.getClick());
        Map<ClickAction, List<Action>> actions = menuItem.getActions();
        for (Map.Entry<ClickAction, List<Action>> entry : actions.entrySet()) {
            ClickAction clickAction = entry.getKey();
            if (clickActions.contains(clickAction)) { // Make sure click matches
                for (Action action : entry.getValue()) { // Execute each action
                    action.execute(player, this, contents);
                }
            }
        }
    }

    private Set<ClickAction> getClickActions(ClickType clickType) {
        Set<ClickAction> clickActions = new HashSet<>();
        clickActions.add(ClickAction.ANY);
        switch (clickType) {
            case LEFT:
            case SHIFT_LEFT:
                clickActions.add(ClickAction.LEFT);
                break;
            case RIGHT:
            case SHIFT_RIGHT:
                clickActions.add(ClickAction.RIGHT);
                break;
            case MIDDLE:
                clickActions.add(ClickAction.MIDDLE);
                break;
            case DROP:
            case CONTROL_DROP:
                clickActions.add(ClickAction.DROP);
                break;
        }
        return clickActions;
    }

}
