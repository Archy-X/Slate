package dev.aurelium.slate.menu;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.click.ClickAction;
import dev.aurelium.slate.builder.BuiltItem;
import dev.aurelium.slate.builder.BuiltMenu;
import dev.aurelium.slate.builder.BuiltTemplate;
import dev.aurelium.slate.fill.FillData;
import dev.aurelium.slate.fill.FillItem;
import dev.aurelium.slate.info.ItemInfo;
import dev.aurelium.slate.info.MenuInfo;
import dev.aurelium.slate.info.TemplateInfo;
import dev.aurelium.slate.inv.ClickableItem;
import dev.aurelium.slate.inv.ItemClickData;
import dev.aurelium.slate.inv.content.InventoryContents;
import dev.aurelium.slate.inv.content.InventoryProvider;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.item.*;
import dev.aurelium.slate.item.active.ActiveItem;
import dev.aurelium.slate.item.active.ActiveSingleItem;
import dev.aurelium.slate.item.active.ActiveTemplateItem;
import dev.aurelium.slate.item.provider.PlaceholderType;
import dev.aurelium.slate.lore.LoreInterpreter;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.position.PositionProvider;
import dev.aurelium.slate.text.TextFormatter;
import dev.aurelium.slate.util.PaperUtil;
import dev.aurelium.slate.util.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
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
    private final LoreInterpreter loreInterpreter;
    private final LoadedMenu menu;
    private final ActiveMenu activeMenu;
    private final Map<String, ActiveItem> activeItems;
    private final Map<String, Object> properties;
    private final int totalPages;
    private final int currentPage;
    private final Player player;
    private InventoryContents contents;
    private final TextFormatter tf = new TextFormatter();
    private final BuiltMenu builtMenu;

    public MenuInventory(Slate slate, LoadedMenu menu, Player player, Map<String, Object> properties, int currentPage) {
        this.slate = slate;
        this.loreInterpreter = new LoreInterpreter(slate);
        this.menu = menu;
        this.activeItems = new LinkedHashMap<>();
        this.activeMenu = new ActiveMenu(this);
        this.properties = new HashMap<>(properties); // Make a copy in case the properties map passed in is immutable
        this.player = player;
        this.builtMenu = slate.getBuiltMenu(menu.name());
        this.totalPages = builtMenu.pageProvider().getPages(new MenuInfo(slate, player, activeMenu));
        this.currentPage = currentPage;
    }

    public Slate getSlate() {
        return slate;
    }

    public LoadedMenu getMenu() {
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
        for (MenuItem menuItem : menu.items().values()) {
            ActiveItem activeItem = activeItems.get(menuItem.getName());
            if (activeItem != null && activeItem.isHidden()) {
                continue;
            }
            if (menuItem instanceof SingleItem) {
                activeItem = new ActiveSingleItem((SingleItem) menuItem);
            } else if (menuItem instanceof TemplateItem<?> templateItem) {
                activeItem = new ActiveTemplateItem<>(templateItem);
            } else {
                continue;
            }
            activeItems.put(menuItem.getName(), activeItem);
        }
        // Handle onOpen
        builtMenu.openListener().handle(new MenuInfo(slate, player, activeMenu));
        // Place fill items
        FillData fillData = menu.fillData();
        if (fillData.enabled()) {
            FillItem fillItem = fillData.item();
            ItemStack providedFill = builtMenu.fillItem().modify(new ItemInfo(slate, player, activeMenu, fillItem.getBaseItem()));
            if (providedFill != null) {
                fillItem = new FillItem(slate, providedFill);
            }
            ItemStack itemStack = fillItem.getBaseItem();
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                String displayName = fillItem.getDisplayName();
                if (displayName != null) {
                    setDisplayName(meta, tf.toComponent(displayName));
                }
                List<LoreLine> loreLines = fillItem.getLore();
                if (loreLines != null) {
                    setLore(meta, loreInterpreter.interpretLore(loreLines, player, activeMenu, BuiltItem.createEmpty()));
                }
                itemStack.setItemMeta(meta);
            }
            if (fillData.slots() == null) { // Use default fill
                contents.fill(ClickableItem.empty(itemStack));
            } else { // Use defined slot positions
                for (SlotPos slot : fillData.slots()) {
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
        builtMenu.updateListener().handle(new MenuInfo(slate, player, activeMenu));
    }

    private void addSingleItem(ActiveSingleItem activeItem, InventoryContents contents, Player player) {
        SingleItem item = activeItem.getItem();
        BuiltItem builtItem = slate.getBuiltMenu(menu.name()).getBackingItem(item.getName());

        ItemStack itemStack = item.getBaseItem().clone();
        builtItem.initListener().handle(new MenuInfo(slate, player, activeMenu));

        replaceItemPlaceholders(itemStack);
        // Apply ItemModifier of built item
        itemStack = builtItem.modifier().modify(new ItemInfo(slate, player, activeMenu, itemStack));
        if (itemStack == null) return;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            String displayName = item.getDisplayName();
            if (displayName != null) {
                // BuiltItem replacers
                displayName = builtItem.applyReplacers(displayName, slate, player, activeMenu, PlaceholderType.DISPLAY_NAME);

                if (slate.isPlaceholderAPIEnabled()) {
                    displayName = PlaceholderAPI.setPlaceholders(player, displayName);
                }
                setDisplayName(meta, tf.toComponent(displayName));
            }
            List<LoreLine> loreLines = item.getLore();
            if (loreLines != null) {
                setLore(meta, loreInterpreter.interpretLore(loreLines, player, activeMenu, builtItem));
            }
            itemStack.setItemMeta(meta);
        }

        // Add item to inventory
        addSingleItemToInventory(item, itemStack, item.getPositions(), contents, player, builtItem);
    }

    private <C> void addTemplateItem(ActiveTemplateItem<C> activeItem, InventoryContents contents, Player player) {
        TemplateItem<C> item = activeItem.getItem();
        BuiltTemplate<C> builtTemplate = slate.getBuiltMenu(menu.name()).getTemplate(item.getName(), item.getContextClass());

        Set<C> contexts;
        builtTemplate.initListener().handle(new MenuInfo(slate, player, activeMenu));
        Set<C> builtDefined = builtTemplate.definedContexts().get(new MenuInfo(slate, player, activeMenu));
        if (!builtDefined.isEmpty()) {
            contexts = builtDefined;
        } else {
            contexts = item.getBaseItems().keySet();
        }

        for (C context : contexts) {
            addContextItem(contents, player, context, item, builtTemplate, contexts);
        }
    }

    private <C> void addContextItem(InventoryContents contents, Player player, C context, TemplateItem<C> item, BuiltTemplate<C> builtTemplate, Set<C> contexts) {
        ItemStack itemStack = item.getBaseItems().get(context); // Get a context-specific base item
        if (itemStack == null) {
            itemStack = item.getDefaultBaseItem(); // Otherwise use default base item
        }
        if (itemStack != null) {
            itemStack = itemStack.clone();
        }
        // Handle initializeContext
        builtTemplate.contextListener().handle(new TemplateInfo<>(slate, player, activeMenu, itemStack, context));

        replaceItemPlaceholders(itemStack);
        // Apply TemplateModifier of built template
        itemStack = builtTemplate.modifier().modify(new TemplateInfo<>(slate, player, activeMenu, itemStack, context));

        if (itemStack == null) return;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            setContextMeta(player, context, item, builtTemplate, meta, itemStack);
        }
        // Add item to inventory
        PositionProvider posProvider = item.getPosition(context);
        SlotPos pos = null;
        if (posProvider != null) {
            List<PositionProvider> providers = new ArrayList<>();
            for (C cont : contexts) {
                providers.add(item.getPosition(cont));
            }
            // Parse the fixed or group position from providers
            pos = posProvider.getPosition(providers);
        } else {
            @Nullable SlotPos builtSlot = builtTemplate.slotProvider().get(new TemplateInfo<>(slate, player, activeMenu, itemStack, context));
            if (builtSlot != null) {
                pos = builtSlot;
            }
        }
        if (pos == null) {
            pos = item.getDefaultPosition();
        }
        if (pos != null) {
            addTemplateItemToInventory(item, itemStack, pos, contents, player, builtTemplate, context);
        }
    }

    private <C> void setContextMeta(Player player, C context, TemplateItem<C> item, BuiltTemplate<C> builtTemplate, ItemMeta meta, ItemStack itemStack) {
        String displayName = item.getActiveDisplayName(context); // Get the context-specific display name, or default if not defined
        if (displayName != null) {
            // BuiltTemplate replacers
            displayName = builtTemplate.applyReplacers(displayName, slate, player, activeMenu, PlaceholderType.DISPLAY_NAME, context);

            if (slate.isPlaceholderAPIEnabled()) {
                displayName = PlaceholderAPI.setPlaceholders(player, displayName);
            }
            setDisplayName(meta, tf.toComponent(displayName));
        }
        List<LoreLine> loreLines = item.getActiveLore(context);
        if (loreLines != null) {
            setLore(meta, loreInterpreter.interpretLore(loreLines, player, activeMenu, builtTemplate, context));
        }
        itemStack.setItemMeta(meta);
    }

    private void setDisplayName(ItemMeta meta, Component component) {
        String displayName = tf.toString(component);
        if (displayName.contains("!!REMOVE!!")) {
            return;
        }
        PaperUtil.setDisplayName(meta, component);
    }

    private void setLore(ItemMeta meta, List<Component> components) {
        if (components.isEmpty()) return;
        PaperUtil.setLore(meta, components);
    }

    /**
     * Adds the menu item itself to the inventory menu and registers click listeners, both listeners and actions
     */
    private void addSingleItemToInventory(SingleItem singleItem, ItemStack itemStack, List<SlotPos> positions, InventoryContents contents, Player player, BuiltItem builtItem) {
        for (SlotPos pos : positions) { // Set item for each position
            contents.set(pos, ClickableItem.from(itemStack, c -> {
                if (!(c.getEvent() instanceof InventoryClickEvent event)) return;

                ActiveItem activeItem = activeItems.get(singleItem.getName());
                if (activeItem != null && activeItem.getCooldown() != 0) {
                    return;
                }

                // Run coded click functionality
                builtItem.handleClick(getClickActions(event.getClick()), new ItemClick(player, event, c.getItem(), pos, activeMenu));

                executeActions(singleItem, player, contents, c); // Run custom click actions
            }));
        }
    }

    private <C> void addTemplateItemToInventory(TemplateItem<C> templateItem, ItemStack itemStack, SlotPos pos, InventoryContents contents, Player player, BuiltTemplate<C> builtTemplate, C context) {
        contents.set(pos, ClickableItem.from(itemStack, c -> {
            if (!(c.getEvent() instanceof InventoryClickEvent event)) return;

            ActiveItem activeItem = activeItems.get(templateItem.getName());
            if (activeItem != null && activeItem.getCooldown() != 0) {
                return;
            }

            // Run coded click functionality
            builtTemplate.handleClick(getClickActions(event.getClick()), new TemplateClick<>(player, event, c.getItem(), pos, activeMenu, context));

            executeActions(templateItem, player, contents, c); // Run custom click actions
        }));
    }

    /**
     * Executes the configured click actions of an item
     */
    private void executeActions(MenuItem menuItem, Player player, InventoryContents contents, ItemClickData clickData) {
        // Convert click data event to InventoryClickEvent
        if (!(clickData.getEvent() instanceof InventoryClickEvent event)) {
            return;
        }

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

    private void replaceItemPlaceholders(ItemStack item) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof SkullMeta skullMeta) {
            PersistentDataContainer container = skullMeta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(slate.getPlugin(), "skull_placeholder_uuid");
            String placeholder = container.get(key, PersistentDataType.STRING);
            if (placeholder != null) {
                placeholder = TextUtil.replace(placeholder, "{player}", player.getUniqueId().toString());
                if (slate.isPlaceholderAPIEnabled()) { // Replace PlaceholderAPI placeholders inside skull_placeholder_uuid
                    placeholder = PlaceholderAPI.setPlaceholders(player, placeholder);
                }
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
