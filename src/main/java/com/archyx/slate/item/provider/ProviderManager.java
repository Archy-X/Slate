package com.archyx.slate.item.provider;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ProviderManager {

    private final Map<String, SingleItemProvider> singleItemProviders;
    private final Map<String, TemplateItemProvider<?>> templateItemProviders;
    private KeyedItemProvider keyedItemProvider;

    public ProviderManager() {
        this.singleItemProviders = new HashMap<>();
        this.templateItemProviders = new HashMap<>();
    }

    @Nullable
    public SingleItemProvider getSingleItem(String itemName) {
        return singleItemProviders.get(itemName);
    }

    @Nullable
    public TemplateItemProvider<?> getTemplateItem(String itemName) {
        return templateItemProviders.get(itemName);
    }

    @Nullable
    public KeyedItemProvider getKeyedItemProvider() {
        return keyedItemProvider;
    }

    /**
     * Registers an item provider for a single item. Providers are used to define unique behavior for items.
     *
     * @param name The name of the single item
     * @param provider The provider instance
     */
    public void registerSingleItem(String name, SingleItemProvider provider) {
        singleItemProviders.put(name, provider);
    }

    /**
     * Registers an item provider for a template item. Providers are used to define unique behavior for items.
     *
     * @param name The name of the template item
     * @param provider The provider instance
     */
    public void registerTemplateItem(String name, TemplateItemProvider<?> provider) {
        templateItemProviders.put(name, provider);
    }

    public void registerKeyedItemProvider(KeyedItemProvider provider) {
        keyedItemProvider = provider;
    }

}
