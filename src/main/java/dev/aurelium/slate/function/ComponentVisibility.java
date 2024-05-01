package dev.aurelium.slate.function;

import dev.aurelium.slate.info.TemplateInfo;

@FunctionalInterface
public interface ComponentVisibility<T> {

    /**
     * Determines if a component should be shown in a template or item.
     *
     * @param info the {@link TemplateInfo} context object
     * @return true if the component should be shown, false otherwise
     */
    boolean shouldShow(TemplateInfo<T> info);

}
