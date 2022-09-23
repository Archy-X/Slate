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
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.util.LoreUtil;
import com.archyx.slate.util.TextUtil;
import com.cryptomorin.xseries.XMaterial;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.ItemClickData;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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
    private final Player player;
    private InventoryContents contents;

    public MenuInventory(Slate slate, ConfigurableMenu menu, Player player, Map<String, Object> properties, int currentPage) {
        this.slate = slate;
        this.menu = menu;
        this.activeItems = new LinkedHashMap<>();
        this.activeMenu = new ActiveMenu(this);
        this.properties = properties;
        this.player = player;
        MenuProvider provider = menu.getProvider();
        if (provider != null) {
            this.totalPages = provider.getPages(player, activeMenu);
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

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        this.contents = contents;
        // Add active items
        for (MenuItem menuItem : menu.getItems().values()) {
            ActiveItem activeItem = activeItems.get(menuItem.getName());
            if (activeItem != null && activeItem.isHidden()) {
                continue;
            }
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
            if (provider != null) { // Check for provided fill item
                ItemStack providedFill = provider.getFillItem(player, activeMenu);
                if (providedFill != null) {
                    fillItem =  new FillItem(slate, providedFill);
                }
            }
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
            if (fillData.getSlots() == null) { // Use default fill
                contents.fill(ClickableItem.empty(itemStack));
            } else { // Use defined slot positions
                for (SlotPos slot : fillData.getSlots()) {
                    contents.set(slot, ClickableItem.empty(itemStack));
                }
            }
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

    @Override
    public void update(Player player, InventoryContents contents) {
        // Decrement item cooldowns
        for (ActiveItem activeItem : activeItems.values()) {
            if (activeItem.getCooldown() > 0) {
                activeItem.setCooldown(activeItem.getCooldown() - 1);
            }
        }
        MenuProvider provider = menu.getProvider();
        if (provider != null) {
            provider.onUpdate(player, activeMenu);
        }
    }

    private void addSingleItem(ActiveSingleItem activeItem, InventoryContents contents, Player player) {
        SingleItem item = activeItem.getItem();
        SingleItemProvider provider = slate.getMenuManager().constructSingleItem(item.getName(), menu.getName());

        ItemStack itemStack = item.getBaseItem().clone();
        if (provider != null) {
            provider.onInitialize(player, activeMenu);
            itemStack = modifyBaseItem(provider, itemStack, player, activeMenu); // Apply provider base item modifications
        }
        if (itemStack == null) {
            return;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            String displayName = item.getDisplayName();
            if (displayName != null) {
                // Replace display name placeholders
                if (provider != null) {
                    String[] placeholders = TextUtil.substringsBetween(displayName, "{", "}");
                    if (placeholders != null) {
                        String style = LoreUtil.getStyle(displayName);
                        for (String placeholder : placeholders) {
                            String replacedText = provider.onPlaceholderReplace(placeholder, player, activeMenu, new PlaceholderData(PlaceholderType.DISPLAY_NAME, style));
                            replacedText = TextUtil.applyColor(replacedText);
                            displayName = TextUtil.replace(displayName, "{" + placeholder + "}", replacedText);
                        }
                    }
                }
                if (slate.isPlaceholderAPIEnabled()) {
                    displayName = PlaceholderAPI.setPlaceholders(player, displayName);
                }
                meta.setDisplayName(displayName);
            }
            List<String> lore = item.getLore();
            if (lore != null && lore.size() > 0) {

                List<String> replacedLore = new ArrayList<>();
                for (String line : lore) {

                    if (provider != null) { // Replace lore placeholders
                        String[] placeholders = TextUtil.substringsBetween(line, "{", "}");
                        if (placeholders != null) {
                            String style = LoreUtil.getStyle(line);
                            for (String placeholder : placeholders) {
                                String replacedLine = provider.onPlaceholderReplace(placeholder, player, activeMenu, new PlaceholderData(PlaceholderType.LORE, style));
                                line = TextUtil.replace(line, "{" + placeholder + "}", replacedLine);
                            }
                        }
                    }

                    if (slate.isPlaceholderAPIEnabled()) {
                        line = PlaceholderAPI.setPlaceholders(player, line);
                    }
                    replacedLore.add(line);
                }
                lore = replacedLore;
                lore = TextUtil.applyNewLines(lore);
                lore = applyColorToLore(lore);
                meta.setLore(lore);
            }
            itemStack.setItemMeta(meta);
        }

        // Add item to inventory
        SlotPos pos = item.getPosition();
        addSingleItemToInventory(item, itemStack, pos, contents, player, provider);
    }

    private <C> void addTemplateItem(ActiveTemplateItem<C> activeItem, InventoryContents contents, Player player) {
        TemplateItem<C> item = activeItem.getItem();
        TemplateItemProvider<C> provider = slate.getMenuManager().constructTemplateItem(item.getName(), menu.getName());

        Set<C> contexts;
        if (provider != null) {
            contexts = provider.getDefinedContexts(player, activeMenu);
        } else {
            contexts = item.getBaseItems().keySet();
        }

        for (C context : contexts) {
            ItemStack itemStack = item.getBaseItems().get(context);
            if (itemStack == null) {
                itemStack = item.getDefaultBaseItem();
            }
            if (itemStack != null) {
                itemStack = itemStack.clone();
            }
            if (provider != null) {
                provider.onInitialize(player, activeMenu, context);
                itemStack = modifyBaseItem(provider, itemStack, player, activeMenu, context); // Apply provider base item modifications
            }
            if (itemStack == null) {
                continue;
            }
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                String displayName = item.getDisplayName();
                if (displayName != null) {
                    // Replace display name placeholders
                    if (provider != null) {
                        String[] placeholders = TextUtil.substringsBetween(displayName, "{", "}");
                        if (placeholders != null) {
                            String style = LoreUtil.getStyle(displayName);
                            for (String placeholder : placeholders) {
                                String replacedText = provider.onPlaceholderReplace(placeholder, player, activeMenu, new PlaceholderData(PlaceholderType.DISPLAY_NAME, style), context);
                                replacedText = TextUtil.applyColor(replacedText);
                                displayName = TextUtil.replace(displayName, "{" + placeholder + "}", replacedText);
                            }
                        }
                    }
                    if (slate.isPlaceholderAPIEnabled()) {
                        displayName = PlaceholderAPI.setPlaceholders(player, displayName);
                    }
                    meta.setDisplayName(displayName);
                }
                List<String> lore = item.getLore();
                if (lore != null && lore.size() > 0) {

                    List<String> replacedLore = new ArrayList<>();
                    for (String line : lore) {

                        if (provider != null) { // Replace lore placeholders
                            String[] placeholders = TextUtil.substringsBetween(line, "{", "}");
                            if (placeholders != null) {
                                String style = LoreUtil.getStyle(line);
                                for (String placeholder : placeholders) {
                                    String replacedLine = provider.onPlaceholderReplace(placeholder, player, activeMenu, new PlaceholderData(PlaceholderType.LORE, style), context);
                                    line = TextUtil.replace(line, "{" + placeholder + "}", replacedLine);
                                }
                            }
                        }

                        if (slate.isPlaceholderAPIEnabled()) {
                            line = PlaceholderAPI.setPlaceholders(player, line);
                        }
                        replacedLore.add(line);
                    }
                    lore = replacedLore;
                    lore = TextUtil.applyNewLines(lore);
                    lore = applyColorToLore(lore);
                    meta.setLore(lore);
                }
                itemStack.setItemMeta(meta);
            }
            // Add item to inventory
            SlotPos pos = item.getPosition(context);
            if (pos == null && provider != null) {
                pos = provider.getSlotPos(player, activeMenu, context); // Use provider position if config pos is not defined
            }
            if (pos == null) {
                pos = item.getDefaultPosition();
            }
            if (pos != null) {
                addTemplateItemToInventory(item, itemStack, pos, contents, player, provider, context);
            }
        }
    }

    private List<String> applyColorToLore(List<String> lore) {
        List<String> appliedLore = new ArrayList<>();
        for (String line : lore) {
            appliedLore.add(TextUtil.applyColor(line));
        }
        return appliedLore;
    }

    /**
     * Adds the menu item itself to the inventory menu and registers click listeners, both listeners and actions
     */
    private void addSingleItemToInventory(SingleItem singleItem, ItemStack itemStack, SlotPos pos, InventoryContents contents, Player player, SingleItemProvider provider) {
        contents.set(pos, ClickableItem.from(itemStack, c -> {
            if (!(c.getEvent() instanceof InventoryClickEvent)) return;
            InventoryClickEvent event = (InventoryClickEvent) c.getEvent();

            ActiveItem activeItem = activeItems.get(singleItem.getName());
            if (activeItem != null && activeItem.getCooldown() != 0) {
                return;
            }

            // Run coded click functionality
            if (provider != null) {
                provider.onClick(player, event, c.getItem(), pos, activeMenu);
            }

            executeActions(singleItem, player, contents, c); // Run custom click actions
        }));
    }

    private <C> void addTemplateItemToInventory(TemplateItem<C> templateItem, ItemStack itemStack, SlotPos pos, InventoryContents contents, Player player, TemplateItemProvider<C> provider, C context) {
        contents.set(pos, ClickableItem.from(itemStack, c -> {
            if (!(c.getEvent() instanceof InventoryClickEvent)) return;
            InventoryClickEvent event = (InventoryClickEvent) c.getEvent();

            ActiveItem activeItem = activeItems.get(templateItem.getName());
            if (activeItem != null && activeItem.getCooldown() != 0) {
                return;
            }

            // Run coded click functionality
            if (provider != null) {
                provider.onClick(player, event, c.getItem(), pos, activeMenu, context);
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

    private ItemStack modifyBaseItem(SingleItemProvider provider, ItemStack baseItem, Player player, ActiveMenu activeMenu) {
        replaceItemPlaceholders(baseItem);
        return provider.onItemModify(baseItem, player, activeMenu);
    }

    private <C> ItemStack modifyBaseItem(TemplateItemProvider<C> provider, ItemStack baseItem, Player player, ActiveMenu activeMenu, C context) {
        replaceItemPlaceholders(baseItem);
        return provider.onItemModify(baseItem, player, activeMenu, context);
    }

    private void replaceItemPlaceholders(ItemStack item) {
        if (item == null) return;
        if (XMaterial.getVersion() < 14) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            PersistentDataContainer container = skullMeta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(slate.getPlugin(), "skull_placeholder_uuid");
            String placeholder = container.get(key, PersistentDataType.STRING);
            if (placeholder != null) {
                placeholder = TextUtil.replace(placeholder, "{player}", player.getUniqueId().toString());
                placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                try {
                    UUID uuid = UUID.fromString(placeholder);
                    skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
                } catch (IllegalArgumentException e) {
                    slate.getPlugin().getLogger().warning("Error while opening menu: Unable to parse UUID for skull placeholder " + placeholder);
                }
            }
            item.setItemMeta(skullMeta);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public InventoryContents getContents() {
        return contents;
    }

}
