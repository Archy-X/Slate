package com.archyx.slate.menu;

import com.archyx.slate.Slate;
import com.archyx.slate.builder.BuiltMenu;
import com.archyx.slate.builder.BuiltTemplate;
import com.archyx.slate.component.ComponentParser;
import com.archyx.slate.component.MenuComponent;
import com.archyx.slate.context.ContextProvider;
import com.archyx.slate.fill.FillData;
import com.archyx.slate.fill.FillItem;
import com.archyx.slate.fill.FillItemParser;
import com.archyx.slate.fill.SlotParser;
import com.archyx.slate.info.MenuInfo;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.parser.SingleItemParser;
import com.archyx.slate.item.parser.TemplateItemParser;
import com.archyx.slate.item.provider.ProviderManager;
import com.archyx.slate.text.TextFormatter;
import fr.minuskube.inv.SmartInventory;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MenuManager {

    private final Slate slate;
    private final Map<String, ConfigurableMenu> menus;
    private final ProviderManager globalProviderManager;
    private final Map<String, ProviderManager> menuProviderManagers;
    private final Map<String, Map<String, Object>> defaultOptions;
    private final TextFormatter tf = new TextFormatter();

    public MenuManager(Slate slate) {
        this.slate = slate;
        this.menus = new LinkedHashMap<>();
        this.globalProviderManager = new ProviderManager();
        this.menuProviderManagers = new HashMap<>();
        this.defaultOptions = new HashMap<>();
    }

    @Nullable
    public ConfigurableMenu getMenu(String name) {
        return menus.get(name);
    }

    public void unregisterAllMenus() {
        menus.clear();
        globalProviderManager.unregisterAll();
        menuProviderManagers.clear();
        defaultOptions.clear();
    }

    @NotNull
    public Map<String, Object> getDefaultProperties(String name, Player player, ActiveMenu activeMenu) {
        BuiltMenu builtMenu = slate.getBuiltMenu(name);
        // Copy in case inner is immutable
        return new HashMap<>(builtMenu.propertyProvider().get(new MenuInfo(slate, player, activeMenu)));
    }

    public ProviderManager getProviderManager(String menuName) {
        return menuProviderManagers.computeIfAbsent(menuName, k -> new ProviderManager());
    }

    public void registerDefaultOptions(String name, Map<String, Object> map) {
        defaultOptions.put(name, map);
    }

    @Nullable
    public Map<String, Object> getDefaultOptions(String menuName) {
        return defaultOptions.get(menuName);
    }

    /**
     * Attempts to load a menu from a file
     *
     * @param file The file to load from, must be in Yaml syntax
     */
    public void loadMenu(File file) {
        String menuName = file.getName();
        int pos = menuName.lastIndexOf(".");
        if (pos > 0) {
            menuName = menuName.substring(0, pos);
        }
        loadMenu(file, menuName);
    }

    /**
     * Attempts to load a menu from a file
     *
     * @param file The file to load from, must be in Yaml syntax
     * @param menuName The name of the menu to be used when opening
     */
    public void loadMenu(File file, String menuName) {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .file(file)
                .indent(2)
                .nodeStyle(NodeStyle.BLOCK)
                .build();
        try {
            ConfigurationNode config = loader.load();

            String title = config.node("title").getString(menuName);
            int size = config.node("size").getInt(6);

            Map<String, MenuItem> items = new LinkedHashMap<>();
            // Load single items
            ConfigurationNode itemsSection = config.node("items");
            if (!itemsSection.virtual()) {
                for (Object keyObj: itemsSection.childrenMap().keySet()) {
                    String itemName = (String) Objects.requireNonNull(keyObj);
                    ConfigurationNode itemSection = itemsSection.node(keyObj);
                    if (!itemSection.virtual()) {
                        MenuItem item = new SingleItemParser(slate).parse(itemSection, menuName);
                        items.put(itemName, item);
                    }
                }
            }
            // Load template items
            ConfigurationNode templatesSection = config.node("templates");
            if (!templatesSection.virtual()) {
                for (Object keyObj : templatesSection.childrenMap().keySet()) {
                    String templateName = (String) Objects.requireNonNull(keyObj);
                    ConfigurationNode templateSection = templatesSection.node(keyObj);
                    if (!templateSection.virtual()) {
                        ContextProvider<?> contextProvider = null;
                        ProviderManager providerManager = menuProviderManagers.get(menuName);
                        if (providerManager != null) {
                            contextProvider = providerManager.getContextProvider(templateName);
                        } else {
                            BuiltTemplate<?> builtTemplate = slate.getBuiltMenu(menuName).templates().get(templateName);
                            if (builtTemplate != null) {
                                contextProvider = slate.getContextManager().getContextProvider(builtTemplate.contextType());
                            }
                        }
                        if (contextProvider != null) {
                            MenuItem item = new TemplateItemParser<>(slate, contextProvider).parse(templateSection, menuName);
                            items.put(templateName, item);
                        }
                    }
                }
            }
            // Load fill item
            ConfigurationNode fillSection = config.node("fill");
            FillData fillData;
            if (!fillSection.virtual()) {
                boolean fillEnabled = fillSection.node("enabled").getBoolean(false);
                FillItem fillItem = new FillItemParser(slate).parse(fillSection, menuName);
                fillData = new FillData(fillItem, new SlotParser().parse(fillSection), fillEnabled);
            } else {
                fillData = new FillData(FillItem.getDefault(slate), null, false);
            }
            // Load components
            Map<String, MenuComponent> components = new HashMap<>();

            ConfigurationNode componentsSection = config.node("components");
            if (!componentsSection.virtual()) {
                for (Object keyObj : componentsSection.childrenMap().keySet()) {
                    String name = (String) Objects.requireNonNull(keyObj);
                    ConfigurationNode componentNode = componentsSection.node(keyObj);
                    if (!componentNode.virtual()) {
                        components.put(name, new ComponentParser(slate).parse(componentNode));
                    }
                }
            }
            // Load formats
            Map<String, String> formats = new HashMap<>();
            for (Object keyObj : config.node("formats").childrenMap().keySet()) {
                String key = (String) keyObj;
                String value = config.node("formats").node(keyObj).getString();
                if (value != null) {
                    formats.put(key, value);
                }
            }

            generateDefaultOptions(menuName, config, loader);
            Map<String, Object> options = loadOptions(config);
            // Add menu to map
            ConfigurableMenu menu = new ConfigurableMenu(menuName, title, size, items, components, formats, fillData, options);
            menus.put(menuName, menu);
        } catch (ConfigurateException | RuntimeException e) {
            slate.getPlugin().getLogger().warning("Error loading menu " + menuName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<String, Object> loadOptions(ConfigurationNode config) {
        Map<String, Object> options = new HashMap<>();
        ConfigurationNode optionSection = config.node("options");
        for (Object keyObj : optionSection.childrenMap().keySet()) {
            String key = (String) keyObj;
            if (optionSection.node(keyObj).isMap()) continue;
            Object value = optionSection.node(key).raw();
            options.put(key, value);
        }
        return options;
    }

    private void generateDefaultOptions(String menuName, ConfigurationNode mainConfig, YamlConfigurationLoader loader) throws SerializationException {
        Map<String, Object> defaultOptions = getDefaultOptions(menuName);
        if (defaultOptions == null) {
            return;
        }
        // Create options section if it does not exist
        ConfigurationNode config = mainConfig.node("options");
        // Loop through each option and set default if option does not exist
        boolean changed = false;
        for (Map.Entry<String, Object> entry : defaultOptions.entrySet()) {
            if (config.node(entry.getKey()).virtual()) {
                config.node(entry.getKey()).set(entry.getValue());
                if (!changed) {
                    changed = true;
                }
            }
        }
        if (changed) { // Save file if modified
            try {
                loader.save(mainConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            ConfigurableMenu menu = menus.get(name);
            if (menu == null) {
                throw new IllegalArgumentException("Menu with name " + name + " not registered");
            }
            MenuInventory menuInventory = new MenuInventory(slate, menu, player, properties, page);
            String title = menu.getTitle();
            // Apply BuiltMenu replacers
            BuiltMenu builtMenu = slate.getBuiltMenu(name);
            title = builtMenu.applyTitleReplacers(title, slate, player, menuInventory.getActiveMenu());

            if (slate.isPlaceholderAPIEnabled()) {
                title = PlaceholderAPI.setPlaceholders(player, title);
            }

            // Build inventory and open
            SmartInventory smartInventory = SmartInventory.builder()
                    .title(tf.toString(tf.toComponent(title)))
                    .size(menu.getSize(), 9)
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

    public Set<String> getMenuNames() {
        return new HashSet<>(slate.getBuiltMenus().keySet());
    }

    public ProviderManager getGlobalProviderManager() {
        return globalProviderManager;
    }
}
