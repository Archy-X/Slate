package com.archyx.slate.menu;

import com.archyx.slate.Slate;
import com.archyx.slate.fill.FillData;
import com.archyx.slate.fill.FillItem;
import com.archyx.slate.fill.FillItemParser;
import com.archyx.slate.fill.SlotParser;
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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MenuManager {

    private final Slate slate;
    private final Map<String, ConfigurableMenu> menus;
    private final ProviderManager globalProviderManager;
    private final Map<String, ProviderManager> menuProviderManagers;
    private final Map<String, MenuProvider> menuProviders;
    private final Map<String, Class<? extends MenuOptionProvider>> optionProviders;
    private final Map<String, Class<?>> optionProviderArrays;

    public MenuManager(Slate slate) {
        this.slate = slate;
        this.menus = new LinkedHashMap<>();
        this.globalProviderManager = new ProviderManager();
        this.menuProviderManagers = new HashMap<>();
        this.menuProviders = new HashMap<>();
        this.optionProviders = new HashMap<>();
        this.optionProviderArrays = new HashMap<>();
    }

    @Nullable
    public ConfigurableMenu getMenu(String name) {
        return menus.get(name);
    }

    @Nullable
    public SingleItemProvider getSingleItem(String itemName, String menuName) {
        // Use skill specific provider if exits
        ProviderManager menuProviderManager = menuProviderManagers.get(menuName);
        if (menuProviderManager != null) {
            SingleItemProvider provider = menuProviderManager.getSingleItem(itemName);
            if (provider != null) {
                return provider;
            }
        }
        return globalProviderManager.getSingleItem(itemName); // Otherwise use global provider
    }

    @Nullable
    public TemplateItemProvider<?> getTemplateItem(String itemName, String menuName) {
        // Use skill specific provider if exits
        ProviderManager menuProviderManager = menuProviderManagers.get(menuName);
        if (menuProviderManager != null) {
            TemplateItemProvider<?> provider = menuProviderManager.getTemplateItem(itemName);
            if (provider != null) {
                return provider;
            }
        }
        return globalProviderManager.getTemplateItem(itemName);
    }

    /**
     * Registers an item provider for a single item. Providers are used to define unique behavior for items.
     *
     * @param name The name of the single item
     * @param provider The provider instance
     */
    public void registerSingleItem(String name, SingleItemProvider provider) {
        globalProviderManager.registerSingleItem(name, provider);
    }

    /**
     * Registers an item provider for a template item. Providers are used to define unique behavior for items.
     *
     * @param name The name of the template item
     * @param provider The provider instance
     */
    public void registerTemplateItem(String name, TemplateItemProvider<?> provider) {
        globalProviderManager.registerTemplateItem(name, provider);
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
     * Registers a menu option provider for a given menu, used to generate default menu options in configuration files
     *
     * @param name The name of the menu
     * @param provider The provider instance
     */
    public void registerOptionProvider(String name, Class<? extends MenuOptionProvider> provider, Class<?> providerArray) {
        if (!provider.isEnum()) {
            throw new IllegalArgumentException("Provider class must be an enum");
        }
        optionProviders.put(name, provider);
        optionProviderArrays.put(name, providerArray);
    }

    @Nullable
    public Class<? extends MenuOptionProvider> getOptionProvider(String menuName) {
        return optionProviders.get(menuName);
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

        Map<String, MenuItem> items = new LinkedHashMap<>();
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
                    TemplateItemProvider<?> provider = slate.getMenuManager().getTemplateItem(templateName, menuName);
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
            fillData = new FillData(fillItem, new SlotParser().parse(fillSection), fillEnabled);
        } else {
            fillData = new FillData(FillItem.getDefault(slate), null, false);
        }

        MenuProvider provider = menuProviders.get(menuName);
        generateDefaultOptions(menuName, file, config);
        Map<String, Object> options = loadOptions(config);
        // Add menu to map
        ConfigurableMenu menu = new ConfigurableMenu(menuName, title, size, items, provider, fillData, options);
        menus.put(menuName, menu);
    }

    public Map<String, Object> loadOptions(ConfigurationSection config) {
        Map<String, Object> options = new HashMap<>();
        ConfigurationSection optionSection = config.getConfigurationSection("options");
        if (optionSection != null) {
            for (String key : optionSection.getKeys(false)) {
                options.put(key, optionSection.get(key));
            }
        }
        return options;
    }

    private void generateDefaultOptions(String menuName, File file, FileConfiguration mainConfig) {
        Class<? extends MenuOptionProvider> providerClass = getOptionProvider(menuName);
        if (providerClass == null) return;
        Class<?> providerArray = optionProviderArrays.get(menuName);
        if (providerArray == null) return;
        try {
            Method valuesMethod = providerClass.getMethod("values", providerArray);
            MenuOptionProvider[] values = (MenuOptionProvider[]) valuesMethod.invoke(null, (Object) null);
            // Create options section if it does not exist
            ConfigurationSection config = mainConfig.getConfigurationSection("options");
            if (config == null) {
                config = mainConfig.createSection("options");
            }
            // Loop through each option and set default if option does not exist
            for (MenuOptionProvider provider : values) {
                String key = provider.toString().toLowerCase(Locale.ROOT);
                if (!config.contains(key)) {
                    config.set(key, provider.getDefaultValue());
                }
            }
            mainConfig.save(file);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IOException e) {
            e.printStackTrace();
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
        ConfigurableMenu menu = menus.get(name);
        if (menu == null) {
            throw new IllegalArgumentException("Menu with name " + name + " not registered");
        }
        MenuInventory menuInventory = new MenuInventory(slate, menu, player, properties, page);
        String title = menu.getTitle();
        // Replace title placeholders
        if (menu.getProvider() != null) {
            String[] placeholders = StringUtils.substringsBetween(title, "{", "}");
            if (placeholders != null) {
                for (String placeholder : placeholders) {
                    title = TextUtil.replace(title, "{" + placeholder + "}",
                            menu.getProvider().onPlaceholderReplace(placeholder, player, menuInventory.getActiveMenu()));
                }
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

    public Set<String> getMenuProviderNames() {
        return menuProviders.keySet();
    }

    public ProviderManager getGlobalProviderManager() {
        return globalProviderManager;
    }
}
