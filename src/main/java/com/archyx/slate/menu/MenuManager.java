package com.archyx.slate.menu;

import com.archyx.slate.Slate;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.parser.SingleItemParser;
import com.archyx.slate.item.parser.TemplateItemParser;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.util.TextUtil;
import fr.minuskube.inv.SmartInventory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MenuManager {

    private final Slate slate;
    private final Map<String, ConfigurableMenu> menus;
    private final Map<String, SingleItemProvider> singleItemProviders;
    private final Map<String, TemplateItemProvider<?>> templateItemProviders;
    private final Map<String, MenuProvider> menuProviders;

    public MenuManager(Slate slate) {
        this.slate = slate;
        this.menus = new HashMap<>();
        this.singleItemProviders = new HashMap<>();
        this.templateItemProviders = new HashMap<>();
        this.menuProviders = new HashMap<>();
    }

    @Nullable
    public ConfigurableMenu getMenu(String name) {
        return menus.get(name);
    }

    @Nullable
    public SingleItemProvider getSingleItemProvider(String menuName) {
        return singleItemProviders.get(menuName);
    }

    @Nullable
    public TemplateItemProvider<?> getTemplateItemProvider(String menuName) {
        return templateItemProviders.get(menuName);
    }

    public void registerProvider(String name, SingleItemProvider provider) {
        singleItemProviders.put(name, provider);
    }

    public void registerProvider(String name, TemplateItemProvider<?> provider) {
        templateItemProviders.put(name, provider);
    }

    public void registerProvider(String name, MenuProvider provider) {
        menuProviders.put(name, provider);
    }

    public void loadMenu(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String name = config.getString("name");
        Validate.notNull(name, "Menu must have value name");

        String title = config.getString("title", name);
        int size = config.getInt("size", 6);

        Map<String, MenuItem> items = new HashMap<>();
        // Load single items
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String itemName : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemName);
                if (itemSection != null) {
                    MenuItem item = new SingleItemParser(slate).parse(itemSection);
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
                    TemplateItemProvider<?> provider = slate.getMenuManager().getTemplateItemProvider(name);
                    if (provider != null) {
                        MenuItem item = new TemplateItemParser<>(slate, provider).parse(templateSection);
                        items.put(templateName, item);
                    } else {
                        throw new IllegalArgumentException("Could not find registered template item provider for menu " + name);
                    }
                }
            }
        }

        MenuProvider provider = menuProviders.get(name);
        // Add menu to map
        ConfigurableMenu menu = new ConfigurableMenu(name, title, size, items, provider);
        menus.put(name, menu);
    }

    public void openMenu(Player player, String name) {
        ConfigurableMenu menu = menus.get(name);
        if (menu == null) return;
        String title = menu.getTitle();
        // Replace title placeholders
        if (menu.getProvider() != null) {
            for (String placeholder : StringUtils.substringsBetween(title, "{", "}")) {
                title = TextUtil.replace(title, "{" + placeholder + "}", menu.getProvider().replacePlaceholder(placeholder, player));
            }
        }
        // Build inventory and open
        SmartInventory smartInventory = SmartInventory.builder()
                .title(title)
                .size(menu.getSize(), 9)
                .manager(slate.getInventoryManager())
                .provider(new MenuInventory(slate, menu))
                .build();
        smartInventory.open(player);
    }

}
