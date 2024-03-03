package com.archyx.slate;

import com.archyx.slate.action.ActionManager;
import com.archyx.slate.context.ContextManager;
import com.archyx.slate.menu.MenuManager;
import com.archyx.slate.option.SlateOptions;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Slate {

    private final JavaPlugin plugin;
    private final MenuManager menuManager;
    private final ContextManager contextManager;
    private final InventoryManager inventoryManager;
    private final ActionManager actionManager;
    private final boolean placeholderAPIEnabled;
    private final SlateOptions options;

    public Slate(JavaPlugin plugin, SlateOptions options) {
        this.plugin = plugin;
        this.menuManager = new MenuManager(this);
        this.contextManager = new ContextManager();
        this.inventoryManager = new InventoryManager(plugin);
        inventoryManager.init();
        this.actionManager = new ActionManager(this);
        this.placeholderAPIEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        this.options = options;
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

    public ActionManager getActionManager() {
        return actionManager;
    }

    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }

    public int getLoreWrappingWidth() {
        return options.getLoreWrappingWidth();
    }

}
