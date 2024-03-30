package com.archyx.slate;

import com.archyx.slate.action.ActionManager;
import com.archyx.slate.builder.BuiltMenu;
import com.archyx.slate.builder.GlobalOptions;
import com.archyx.slate.builder.GlobalOptionsBuilder;
import com.archyx.slate.builder.MenuBuilder;
import com.archyx.slate.context.ContextManager;
import com.archyx.slate.menu.MenuManager;
import com.archyx.slate.option.SlateOptions;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Slate {

    private final JavaPlugin plugin;
    private final MenuManager menuManager;
    private final ContextManager contextManager;
    private final InventoryManager inventoryManager;
    private final ActionManager actionManager;
    private final boolean placeholderAPIEnabled;
    private final SlateOptions options;
    private final Map<String, BuiltMenu> builtMenus = new HashMap<>();
    private GlobalOptions globalOptions = GlobalOptionsBuilder.builder().build();

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

    public void buildMenu(String name, Consumer<MenuBuilder> menu) {
        MenuBuilder builder = MenuBuilder.builder();
        menu.accept(builder);
        builtMenus.put(name, builder.build());
    }

    @NotNull
    public BuiltMenu getBuiltMenu(String name) {
        return builtMenus.getOrDefault(name, BuiltMenu.createEmpty());
    }

    public Map<String, BuiltMenu> getBuiltMenus() {
        return builtMenus;
    }

    public void setGlobalOptions(Consumer<GlobalOptionsBuilder> options) {
        GlobalOptionsBuilder builder = GlobalOptionsBuilder.builder();
        options.accept(builder);
        this.globalOptions = builder.build();
    }

    public GlobalOptions getGlobalOptions() {
        return globalOptions;
    }
}
