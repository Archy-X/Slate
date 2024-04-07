package com.archyx.slate;

import com.archyx.slate.action.ActionManager;
import com.archyx.slate.builder.BuiltMenu;
import com.archyx.slate.builder.GlobalOptions;
import com.archyx.slate.builder.GlobalOptionsBuilder;
import com.archyx.slate.builder.MenuBuilder;
import com.archyx.slate.context.ContextManager;
import com.archyx.slate.menu.LoadedMenu;
import com.archyx.slate.menu.MenuLoader;
import com.archyx.slate.menu.MenuOpener;
import com.archyx.slate.option.SlateOptions;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Slate {

    private final JavaPlugin plugin;
    private final ContextManager contextManager;
    private final InventoryManager inventoryManager;
    private final ActionManager actionManager;
    private final boolean placeholderAPIEnabled;
    private final SlateOptions options;
    private final Map<String, BuiltMenu> builtMenus = new HashMap<>();
    private final Map<String, LoadedMenu> loadedMenus = new LinkedHashMap<>();
    private final MenuOpener menuOpener = new MenuOpener(this);

    private GlobalOptions globalOptions = GlobalOptionsBuilder.builder().build();

    public Slate(JavaPlugin plugin, SlateOptions options) {
        this.plugin = plugin;
        this.contextManager = new ContextManager();
        this.inventoryManager = new InventoryManager(plugin);
        inventoryManager.init();
        this.actionManager = new ActionManager(this);
        this.placeholderAPIEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        this.options = options;
    }

    /**
     * Loads all menu files in the mainDirectory defined in {@link SlateOptions} and uses any
     * mergeDirectories defined to merge menu files together before loading. Files are registered as
     * a {@link LoadedMenu} after loading.
     *
     * @return the number of menus successfully loaded
     */
    public int loadMenus() {
        MenuLoader loader = new MenuLoader(this, options.mainDirectory(), options.mergeDirectories());
        return loader.loadMenus();
    }

    public void openMenu(Player player, String name, Map<String, Object> properties, int page) {
        menuOpener.openMenu(player, name, properties, page);
    }

    public void openMenu(Player player, String name, Map<String, Object> properties) {
        menuOpener.openMenu(player, name, properties);
    }

    public void openMenu(Player player, String name, int page) {
        menuOpener.openMenu(player, name, page);
    }

    public void openMenu(Player player, String name) {
        menuOpener.openMenu(player, name);
    }

    public void unregisterMenus() {
        this.builtMenus.clear();
    }

    public JavaPlugin getPlugin() {
        return plugin;
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
        return options.loreWrappingWidth();
    }

    public SlateOptions getOptions() {
        return options;
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

    public void addLoadedMenu(LoadedMenu menu) {
        this.loadedMenus.put(menu.name(), menu);
    }

    @Nullable
    public LoadedMenu getLoadedMenu(String name) {
        return loadedMenus.get(name);
    }

    public Map<String, LoadedMenu> getLoadedMenus() {
        return loadedMenus;
    }

    public void setGlobalOptions(Consumer<GlobalOptionsBuilder> options) {
        GlobalOptionsBuilder builder = GlobalOptionsBuilder.builder();
        options.accept(builder);
        this.globalOptions = builder.build();
    }

    public GlobalOptions getGlobalOptions() {
        return globalOptions;
    }

    public void addMergeDirectory(File mergeDir) {
        if (!mergeDir.isDirectory()) return;

        if (options.mergeDirectories().contains(mergeDir)) {
            removeMergeDirectory(mergeDir);
        }
        options.mergeDirectories().add(mergeDir);
    }

    public void removeMergeDirectory(File mergeDir) {
        options.mergeDirectories().remove(mergeDir);
    }

}
