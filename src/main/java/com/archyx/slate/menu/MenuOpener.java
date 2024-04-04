package com.archyx.slate.menu;

import com.archyx.slate.Slate;
import com.archyx.slate.builder.BuiltMenu;
import com.archyx.slate.text.TextFormatter;
import fr.minuskube.inv.SmartInventory;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MenuOpener {

    private final Slate slate;
    private final TextFormatter tf = new TextFormatter();

    public MenuOpener(Slate slate) {
        this.slate = slate;
    }

    /**
     * Opens a loaded menu for a player, will fail silently if the menu does not exist.
     *
     * @param player The player to display the menu to
     * @param name The name of the menu
     * @param properties Map of menu properties
     * @param page The page number to open, 0 is the first page
     */
    public void openMenu(Player player, String name, Map<String, Object> properties, int page) {
        try {
            LoadedMenu menu = slate.getLoadedMenu(name);
            if (menu == null) {
                throw new IllegalArgumentException("Menu with name " + name + " not registered");
            }
            MenuInventory menuInventory = new MenuInventory(slate, menu, player, properties, page);
            String title = menu.title();
            // Apply BuiltMenu replacers
            BuiltMenu builtMenu = slate.getBuiltMenu(name);
            title = builtMenu.applyTitleReplacers(title, slate, player, menuInventory.getActiveMenu());

            if (slate.isPlaceholderAPIEnabled()) {
                title = PlaceholderAPI.setPlaceholders(player, title);
            }

            // Build inventory and open
            SmartInventory smartInventory = SmartInventory.builder()
                    .title(tf.toString(tf.toComponent(title)))
                    .size(menu.size(), 9)
                    .manager(slate.getInventoryManager())
                    .provider(menuInventory)
                    .build();
            smartInventory.open(player);
        } catch (Exception e) {
            player.closeInventory();
            slate.getPlugin().getLogger().warning("Error opening Slate menu " + name);
            e.printStackTrace();
        }
    }

    public void openMenu(Player player, String name, Map<String, Object> properties) {
        openMenu(player, name, properties, 0);
    }

    /**
     * Opens a loaded menu for a player, will fail silently if the menu does not exist.
     *
     * @param player The player to display the menu to
     * @param name The name of the menu
     * @param page The page number to open, 0 is the first page
     */
    public void openMenu(Player player, String name, int page) {
        openMenu(player, name, new HashMap<>(), page);
    }

    /**
     * Opens a loaded menu for a player, will fail silently if the menu does not exist.
     * Shows the first page.
     *
     * @param player The player to display the menu to
     * @param name The name of the menu
     */
    public void openMenu(Player player, String name) {
        openMenu(player, name, new HashMap<>(), 0);
    }

}