package com.archyx.slate;

import com.archyx.slate.context.ContextManager;
import com.archyx.slate.menu.MenuManager;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Slate {

    private final JavaPlugin plugin;
    private final MenuManager menuManager;
    private final ContextManager contextManager;
    private final InventoryManager inventoryManager;
    private final boolean placeholderAPIEnabled;

    public Slate(JavaPlugin plugin) {
        this.plugin = plugin;
        this.menuManager = new MenuManager(this);
        this.contextManager = new ContextManager();
        this.inventoryManager = new InventoryManager(plugin);
        inventoryManager.init();
        this.placeholderAPIEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public ContextManager getContextManager() {
        return contextManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }

}
