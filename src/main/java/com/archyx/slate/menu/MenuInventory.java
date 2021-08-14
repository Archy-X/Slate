package com.archyx.slate.menu;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.click.ClickAction;
import com.archyx.slate.fill.FillData;
import com.archyx.slate.fill.FillItem;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.SingleItem;
import com.archyx.slate.item.TemplateItem;
import com.archyx.slate.item.active.ActiveItem;
import com.archyx.slate.item.active.ActiveSingleItem;
import com.archyx.slate.item.active.ActiveTemplateItem;
import com.archyx.slate.item.provider.PlaceholderType;
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

public class MenuInventory implements InventoryProvider {

    private final Slate slate;
    private final ConfigurableMenu menu;
    private final ActiveMenu activeMenu;
    private final Map<String, ActiveItem> activeItems;
    private final Map<String, Object> properties;
    private final int totalPages;
    private final int currentPage;

    public MenuInventory(Slate slate, ConfigurableMenu menu, Player player, Map<String, Object> properties, int currentPage) {
        this.slate = slate;
        this.menu = menu;
        this.activeItems = new HashMap<>();
        this.activeMenu = new ActiveMenu(this);
        this.properties = properties;
        MenuProvider provider = menu.getProvider();
        if (provider != null) {
            this.totalPages = provider.getPages(player);
        } else {
            this.totalPages = 1;
        }
        this.currentPage = currentPage;
    }

    public Slate getSlate() {
        return slate;
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

    public void removeActiveItem(String itemName) {
        activeItems.remove(itemName);
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
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
        // Place fill items
        FillData fillData = menu.getFillData();
        if (fillData.isEnabled()) {
            FillItem fillItem = fillData.getItem();
            ItemStack itemStack = fillItem.getBaseItem();
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                String displayName = fillItem.getDisplayName();
                if (displayName != null) {
                    meta.setDisplayName(displayName);
                }
                List<String> lore = fillItem.getLore();
                if (lore != null) {
                    lore = TextUtil.applyNewLines(lore);
                    meta.setLore(lore);
                }
                itemStack.setItemMeta(meta);
            }
            contents.fill(ClickableItem.empty(itemStack));
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
                    String[] placeholders = StringUtils.substringsBetween(displayName, "{", "}");
                    if (placeholders != null) {
                        for (String placeholder : placeholders) {
                            displayName = TextUtil.replace(displayName, "{" + placeholder + "}",
                                    provider.onPlaceholderReplace(placeholder, player, activeMenu, PlaceholderType.DISPLAY_NAME));
                        }
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
                        String[] placeholders = StringUtils.substringsBetween(line, "{", "}");
                        if (placeholders != null) {
                            for (String placeholder : placeholders) {
                                line = TextUtil.replace(line, "{" + placeholder + "}",
                                        provider.onPlaceholderReplace(placeholder, player, activeMenu, PlaceholderType.LORE));
                            }
                        }
                        replacedLore.add(line);
                    }
                    lore = replacedLore;
                }
                lore = TextUtil.applyNewLines(lore);
                meta.setLore(lore);
            }
            itemStack.setItemMeta(meta);
        }

        // Add item to inventory
        SlotPos pos = item.getPosition();
        addSingleItemToInventory(item, itemStack, pos, contents, player);
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
                        String[] placeholders = StringUtils.substringsBetween(displayName, "{", "}");
                        if (placeholders != null) {
                            for (String placeholder : placeholders) {
                                displayName = TextUtil.replace(displayName, "{" + placeholder + "}",
                                        provider.onPlaceholderReplace(placeholder, player, activeMenu, PlaceholderType.DISPLAY_NAME, context));
                            }
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
                            String[] placeholders = StringUtils.substringsBetween(line, "{", "}");
                            if (placeholders != null) {
                                for (String placeholder : placeholders) {
                                    line = TextUtil.replace(line, "{" + placeholder + "}",
                                            provider.onPlaceholderReplace(placeholder, player, activeMenu, PlaceholderType.LORE, context));
                                }
                            }
                            replacedLore.add(line);
                        }
                        lore = replacedLore;
                    }
                    lore = TextUtil.applyNewLines(lore);
                    meta.setLore(lore);
                }
                itemStack.setItemMeta(meta);
            }
            // Add item to inventory
            SlotPos pos = item.getPosition(context);
            addTemplateItemToInventory(item, itemStack, pos, contents, player, context);
        }
    }

    /**
     * Adds the menu item itself to the inventory menu and registers click listeners, both listeners and actions
     */
    private void addSingleItemToInventory(SingleItem singleItem, ItemStack itemStack, SlotPos pos, InventoryContents contents, Player player) {
        contents.set(pos, ClickableItem.from(itemStack, c -> {
            if (!(c.getEvent() instanceof InventoryClickEvent)) return;
            InventoryClickEvent event = (InventoryClickEvent) c.getEvent();

            // Run coded click functionality
            SingleItemProvider provider = singleItem.getProvider();
            if (provider != null) {
                provider.onClick(player, event, c.getItem(), pos);
            }

            executeActions(singleItem, player, contents, c); // Run custom click actions
        }));
    }

    private <C> void addTemplateItemToInventory(TemplateItem<C> templateItem, ItemStack itemStack, SlotPos pos, InventoryContents contents, Player player, C context) {
        contents.set(pos, ClickableItem.from(itemStack, c -> {
            if (!(c.getEvent() instanceof InventoryClickEvent)) return;
            InventoryClickEvent event = (InventoryClickEvent) c.getEvent();

            // Run coded click functionality
            TemplateItemProvider<C> provider = templateItem.getProvider();
            if (provider != null) {
                provider.onClick(player, event, c.getItem(), pos, context);
            }

            executeActions(templateItem, player, contents, c); // Run custom click actions
        }));
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

    public Map<String, Object> getProperties() {
        return properties;
    }

}
