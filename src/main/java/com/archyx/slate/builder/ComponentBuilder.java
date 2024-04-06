package com.archyx.slate.builder;

import com.archyx.slate.function.ComponentInstances;
import com.archyx.slate.function.ComponentReplacer;
import com.archyx.slate.function.ComponentVisibility;

import java.util.HashMap;
import java.util.Map;

public class ComponentBuilder<T> {

    private final Class<T> contextType;
    private final Map<String, ComponentReplacer<T>> replacers = new HashMap<>();
    private ComponentReplacer<T> anyReplacer = p -> null;
    private ComponentVisibility<T> visibility = t -> true;
    private ComponentInstances<T> instances = t -> 1;

    private ComponentBuilder(Class<T> contextType) {
        this.contextType = contextType;
    }

    public static <T> ComponentBuilder<T> builder(Class<T> contextType) {
        return new ComponentBuilder<>(contextType);
    }

    public ComponentBuilder<T> replace(String from, ComponentReplacer<T> replacer) {
        replacers.put(from, replacer);
        return this;
    }

    public ComponentBuilder<T> replaceAny(ComponentReplacer<T> replacer) {
        anyReplacer = replacer;
        return this;
    }

    public ComponentBuilder<T> shouldShow(ComponentVisibility<T> visibility) {
        this.visibility = visibility;
        return this;
    }

    public ComponentBuilder<T> instances(ComponentInstances<T> instances) {
        this.instances = instances;
        return this;
    }

    public BuiltComponent<T> build() {
        return new BuiltComponent<>(contextType, replacers, anyReplacer, visibility, instances);
    }
}
