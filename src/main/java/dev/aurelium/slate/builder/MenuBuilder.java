package dev.aurelium.slate.builder;

import dev.aurelium.slate.function.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class MenuBuilder {

    private final Map<String, ItemBuilder> itemBuilders = new HashMap<>();
    private final Map<String, TemplateBuilder<?>> templateBuilders = new HashMap<>();
    private final Map<String, ComponentBuilder<?>> componentBuilders = new HashMap<>();
    private final Map<String, ItemReplacer> titleReplacers = new HashMap<>();
    private ItemReplacer titleAnyReplacer = p -> null;
    private PageProvider pageProvider = m -> 1;
    private PropertyProvider propertyProvider = m -> new HashMap<>();
    private ItemModifier fillItem = i -> null;
    private MenuListener openListener = m -> {};
    private MenuListener updateListener = m -> {};
    private Map<String, Object> defaultOptions = new HashMap<>();

    private MenuBuilder() {

    }

    public static MenuBuilder builder() {
        return new MenuBuilder();
    }

    public MenuBuilder item(String name, Consumer<ItemBuilder> item) {
        ItemBuilder builder = ItemBuilder.builder();
        item.accept(builder);
        itemBuilders.put(name, builder);
        return this;
    }

    public MenuBuilder item(String name, ItemBuilder builder) {
        itemBuilders.put(name, builder);
        return this;
    }

    public <T> MenuBuilder template(String name, Class<T> contextType, Consumer<TemplateBuilder<T>> template) {
        TemplateBuilder<T> builder = TemplateBuilder.builder(contextType);
        template.accept(builder);
        templateBuilders.put(name, builder);
        return this;
    }

    public <T> MenuBuilder template(String name, Class<T> contextType, TemplateBuilder<T> builder) {
        templateBuilders.put(name, builder);
        return this;
    }

    public <T> MenuBuilder component(String name, Class<T> contextType, Consumer<ComponentBuilder<T>> template) {
        ComponentBuilder<T> builder = ComponentBuilder.builder(contextType);
        template.accept(builder);
        componentBuilders.put(name, builder);
        return this;
    }

    public <T> MenuBuilder component(String name, Class<T> contextType, ComponentBuilder<T> builder) {
        componentBuilders.put(name, builder);
        return this;
    }

    public MenuBuilder replaceTitle(String from, ItemReplacer replacer) {
        titleReplacers.put(from, replacer);
        return this;
    }

    public MenuBuilder replaceTitleAny(ItemReplacer replacer) {
        this.titleAnyReplacer = replacer;
        return this;
    }

    public MenuBuilder pages(PageProvider provider) {
        this.pageProvider = provider;
        return this;
    }

    public MenuBuilder properties(PropertyProvider provider) {
        this.propertyProvider = provider;
        return this;
    }

    public MenuBuilder fillItem(ItemModifier modifier) {
        this.fillItem = modifier;
        return this;
    }

    public MenuBuilder onOpen(MenuListener listener) {
        this.openListener = listener;
        return this;
    }

    public MenuBuilder onUpdate(MenuListener listener) {
        this.updateListener = listener;
        return this;
    }

    public MenuBuilder defaultOptions(Map<String, Object> defaultOptions) {
        this.defaultOptions = defaultOptions;
        return this;
    }

    public BuiltMenu build() {
        Map<String, BuiltItem> items = new HashMap<>();
        for (Entry<String, ItemBuilder> entry : itemBuilders.entrySet()) {
            items.put(entry.getKey(), entry.getValue().build());
        }
        Map<String, BuiltTemplate<?>> templates = new HashMap<>();
        for (Entry<String, TemplateBuilder<?>> entry : templateBuilders.entrySet()) {
            templates.put(entry.getKey(), entry.getValue().build());
        }
        Map<String, BuiltComponent<?>> components = new HashMap<>();
        for (Entry<String, ComponentBuilder<?>> entry : componentBuilders.entrySet()) {
            components.put(entry.getKey(), entry.getValue().build());
        }
        return new BuiltMenu(items, templates, components, titleReplacers, titleAnyReplacer, pageProvider, propertyProvider,
                fillItem, openListener, updateListener, defaultOptions);
    }

}
