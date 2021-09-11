package com.archyx.slate.menu;

import com.archyx.slate.Slate;
import com.archyx.slate.fill.FillData;
import com.archyx.slate.fill.FillItem;
import com.archyx.slate.fill.FillItemParser;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.parser.SingleItemParser;
import com.archyx.slate.item.parser.TemplateItemParser;
import com.archyx.slate.item.provider.ProviderManager;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.util.TextUtil;
import fr.minuskube.inv.SmartInventory;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MenuManager {

    private final Slate slate;
    private final Map<String, ConfigurableMenu> menus;
    private final ProviderManager globalProviderManager;
    private final Map<String, ProviderManager> menuProviderManagers;
    private final Map<String, MenuProvider> menuProviders;

    public MenuManager(Slate slate) {
        this.slate = slate;
        this.menus = new LinkedHashMap<>();
        this.globalProviderManager = new ProviderManager();
        this.menuProviderManagers = new HashMap<>();
        this.menuProviders = new HashMap<>();
    }

    @Nullable
    public ConfigurableMenu getMenu(String name) {
        return menus.get(name);
    }

    @Nullable
    public SingleItemProvider getSingleItemProvider(String itemName, String menuName) {
        // Use skill specific provider if exits
        ProviderManager menuProviderManager = menuProviderManagers.get(menuName);
        if (menuProviderManager != null) {
            SingleItemProvider provider = menuProviderManager.getSingle(itemName);
            if (provider != null) {
                return provider;
            }
        }
        return globalProviderManager.getSingle(itemName); // Otherwise use global provider
    }

    @Nullable
    public TemplateItemProvider<?> getTemplateItemProvider(String itemName, String menuName) {
        // Use skill specific provider if exits
        ProviderManager menuProviderManager = menuProviderManagers.get(menuName);
        if (menuProviderManager != null) {
            TemplateItemProvider<?> provider = menuProviderManager.getTemplate(itemName);
            if (provider != null) {
                return provider;
            }
        }
        return globalProviderManager.getTemplate(itemName);
    }

    /**
     * Registers an item provider for a single item. Providers are used to define unique behavior for items.
     *
     * @param name The name of the single item
     * @param provider The provider instance
     */
    public void registerItemProvider(String name, SingleItemProvider provider) {
        globalProviderManager.registerSingle(name, provider);
    }

    /**
     * Registers an item provider for a template item. Providers are used to define unique behavior for items.
     *
     * @param name The name of the template item
     * @param provider The provider instance
     */
    public void registerItemProvider(String name, TemplateItemProvider<?> provider) {
        globalProviderManager.registerTemplate(name, provider);
    }

    /**
     * Registers a menu provider for a menu. Providers are used to define unique behavior for menus.
     *
     * @param name The name of the menu
     * @param provider The provider instance
     */
    public void registerMenuProvider(String name, MenuProvider provider) {
        menuProviders.put(name, provider);
    }

    public ProviderManager getProviderManager(String menuName) {
        return menuProviderManagers.computeIfAbsent(menuName, k -> new ProviderManager());
    }

    /**
     * Attempts to load a menu from a file
     *
     * @param file The file to load from, must be in Yaml syntax
     */
    public void loadMenu(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String menuName = file.getName();
        int pos = menuName.lastIndexOf(".");
        if (pos > 0) {
            menuName = menuName.substring(0, pos);
        }

        String title = config.getString("title", menuName);
        int size = config.getInt("size", 6);

        Map<String, MenuItem> items = new HashMap<>();
        // Load single items
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String itemName : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemName);
                if (itemSection != null) {
                    MenuItem item = new SingleItemParser(slate).parse(itemSection, menuName);
                    items.put(itemName, item);
                }
            }
        }
        // Load template items
        ConfigurationSection templatesSection = config.getConfigurationSection("templates");
        if (templatesSection != null) {
            for (String templateName : templatesSection.getKeys(false)) {
                ConfigurationSection templateSection = templatesSection.getConfigurationSection(templateName);
                if (templateSection != null) {
                    TemplateItemProvider<?> provider = slate.getMenuManager().getTemplateItemProvider(templateName, menuName);
                    if (provider != null) {
                        MenuItem item = new TemplateItemParser<>(slate, provider).parse(templateSection, menuName);
                        items.put(templateName, item);
                    } else {
                        throw new IllegalArgumentException("Could not find registered template item provider for name " + templateName);
                    }
                }
            }
        }
        // Load fill item
        ConfigurationSection fillSection = config.getConfigurationSection("fill");
        FillData fillData;
        if (fillSection != null) {
            boolean fillEnabled = fillSection.getBoolean("enabled", false);
            FillItem fillItem = new FillItemParser(slate).parse(fillSection, menuName);
            fillData = new FillData(fillItem, fillEnabled);
        } else {
            fillData = new FillData(FillItem.getDefault(slate), false);
        }

        MenuProvider provider = menuProviders.get(menuName);
        // Add menu to map
        ConfigurableMenu menu = new ConfigurableMenu(menuName, title, size, items, provider, fillData);
        menus.put(menuName, menu);
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
        ConfigurableMenu menu = menus.get(name);
        if (menu == null) {
            throw new IllegalArgumentException("Menu with name " + name + " not registered");
        }
        MenuInventory menuInventory = new MenuInventory(slate, menu, player, properties, page);
        String title = menu.getTitle();
        // Replace title placeholders
        if (menu.getProvider() != null) {
            for (String placeholder : StringUtils.substringsBetween(title, "{", "}")) {
                title = TextUtil.replace(title, "{" + placeholder + "}",
                        menu.getProvider().onPlaceholderReplace(placeholder, player, menuInventory.getActiveMenu()));
            }
        }
        // Build inventory and open
        SmartInventory smartInventory = SmartInventory.builder()
                .title(title)
                .size(menu.getSize(), 9)
                .manager(slate.getInventoryManager())
                .provider(menuInventory)
                .build();
        smartInventory.open(player);
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

    public Set<String> getMenuProviderNames() {
        return menuProviders.keySet();
    }

}
