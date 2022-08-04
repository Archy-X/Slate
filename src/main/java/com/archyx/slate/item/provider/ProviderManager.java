package com.archyx.slate.item.provider;

import com.archyx.slate.Slate;
import com.archyx.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ProviderManager {

    private final Slate slate;
    private final Map<String, SingleItemConstructor<? extends SingleItemProvider>> singleItemConstructors;
    private final Map<String, TemplateItemConstructor<? extends TemplateItemProvider<?>>> templateItemConstructors;
    private final Map<String, ContextProvider<?>> templateContextProviders;
    private KeyedItemProvider keyedItemProvider;

    public ProviderManager(Slate slate) {
        this.slate = slate;
        this.singleItemConstructors = new HashMap<>();
        this.templateItemConstructors = new HashMap<>();
        this.templateContextProviders = new HashMap<>();
    }

    @Nullable
    public SingleItemProvider constructSingleItem(String itemName) {
        SingleItemConstructor<? extends SingleItemProvider> constructor = singleItemConstructors.get(itemName);
        if (constructor != null) {
            return constructor.construct();
        }
        return null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <C> TemplateItemProvider<C> constructTemplateItem(String itemName) {
        TemplateItemConstructor<? extends TemplateItemProvider<C>> constructor = (TemplateItemConstructor<? extends TemplateItemProvider<C>>) templateItemConstructors.get(itemName);
        if (constructor != null) {
            return constructor.construct();
        }
        return null;
    }

    @Nullable
    public ContextProvider<?> getContextProvider(String itemName) {
        return templateContextProviders.get(itemName);
    }

    @Nullable
    public KeyedItemProvider getKeyedItemProvider() {
        return keyedItemProvider;
    }

    /**
     * Registers an item constructor for a single item. Constructors create providers which are used to define unique behavior for items.
     *
     * @param name The name of the single item
     * @param constructor The constructor instance
     */
    public void registerSingleItem(String name, SingleItemConstructor<? extends SingleItemProvider> constructor) {
        singleItemConstructors.put(name, constructor);
    }

    /**
     * Registers an item provider for a template item. Providers are used to define unique behavior for items.
     *
     * @param name The name of the template item
     * @param constructor The constructor instance
     */
    public void registerTemplateItem(String name, Class<?> contextClass, TemplateItemConstructor<? extends TemplateItemProvider<?>> constructor) {
        ContextProvider<?> contextProvider = slate.getContextManager().getContextProvider(contextClass);
        templateItemConstructors.put(name, constructor);
        if (contextProvider != null) {
            templateContextProviders.put(name, contextProvider);
        }
    }

    public void registerKeyedItemProvider(KeyedItemProvider provider) {
        keyedItemProvider = provider;
    }

}
