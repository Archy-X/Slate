package com.archyx.slate.menu;

import com.archyx.slate.Slate;
import com.archyx.slate.builder.BuiltTemplate;
import com.archyx.slate.component.ComponentParser;
import com.archyx.slate.component.MenuComponent;
import com.archyx.slate.context.ContextProvider;
import com.archyx.slate.fill.FillData;
import com.archyx.slate.fill.FillItem;
import com.archyx.slate.fill.FillItemParser;
import com.archyx.slate.fill.SlotParser;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.parser.SingleItemParser;
import com.archyx.slate.item.parser.TemplateItemParser;
import com.archyx.slate.util.YamlLoader;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MenuLoader {

    private final Slate slate;
    private final File mainDir;
    private final List<File> mergeDirs;
    private final YamlLoader loader;

    public MenuLoader(Slate slate, File mainDir, List<File> mergeDirs) {
        this.slate = slate;
        this.mainDir = mainDir;
        this.mergeDirs = mergeDirs;
        this.loader = new YamlLoader(slate.getPlugin());
    }

    public int loadMenus() {
        File[] files = mainDir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return 0;

        int menusLoaded = 0;
        for (File menuFile : files) {
            try {
                loadAndAddMenu(menuFile);
                menusLoaded++;
            } catch (ConfigurateException | RuntimeException e) {
                slate.getPlugin().getLogger().warning("Error loading menu file " + menuFile.getName());
                e.printStackTrace();
            }
        }
        return menusLoaded;
    }

    private void loadAndAddMenu(File file) throws ConfigurateException, RuntimeException {
        String menuName = file.getName();
        int pos = menuName.lastIndexOf(".");
        if (pos > 0) {
            menuName = menuName.substring(0, pos);
        }
        LoadedMenu menu = loadMenu(file, menuName);
        slate.addLoadedMenu(menu);
    }

    private ConfigurationNode mergeAndLoad(File mainFile) throws ConfigurateException {
        ConfigurationNode base = loader.loadUserFile(mainFile);
        List<ConfigurationNode> nodesToMerge = new ArrayList<>();
        nodesToMerge.add(base);

        for (File mergeDir : mergeDirs) {
            if (!mergeDir.isDirectory()) continue;

            File[] files = mergeDir.listFiles((d, name) -> name.equals(mainFile.getName()));
            if (files == null || files.length == 0) continue;

            File mergingFile = files[0];
            ConfigurationNode mergingNode = loader.loadUserFile(mergingFile);
            nodesToMerge.add(mergingNode);
        }

        return loader.mergeNodes(nodesToMerge.toArray(new ConfigurationNode[0]));
    }

    /**
     * Attempts to load a menu from a file
     *
     * @param file The file to load from, must be in Yaml syntax
     * @param menuName The name of the menu to be used when opening
     */
    private LoadedMenu loadMenu(File file, String menuName) throws ConfigurateException, RuntimeException {
        ConfigurationNode config = mergeAndLoad(file);

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
                    BuiltTemplate<?> builtTemplate = slate.getBuiltMenu(menuName).templates().get(templateName);
                    if (builtTemplate != null) {
                        contextProvider = slate.getContextManager().getContextProvider(builtTemplate.contextType());
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

        generateDefaultOptions(menuName, file, config);
        Map<String, Object> options = MenuLoader.loadOptions(config);
        // Add menu to map
        return new LoadedMenu(menuName, title, size, items, components, formats, fillData, options);
    }

    private void generateDefaultOptions(String menuName, File file, ConfigurationNode mainConfig) throws SerializationException {
        Map<String, Object> defaultOptions = slate.getBuiltMenu(menuName).defaultOptions();
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
                loader.saveFile(file, mainConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, Object> loadOptions(ConfigurationNode config) {
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

}
