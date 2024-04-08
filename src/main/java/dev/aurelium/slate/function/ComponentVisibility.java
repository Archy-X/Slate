package dev.aurelium.slate.function;

import dev.aurelium.slate.info.TemplateInfo;

@FunctionalInterface
public interface ComponentVisibility<T> {

    boolean shouldShow(TemplateInfo<T> info);

}
