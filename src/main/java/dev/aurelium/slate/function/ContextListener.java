package dev.aurelium.slate.function;

import dev.aurelium.slate.info.TemplateInfo;

@FunctionalInterface
public interface ContextListener<T> {

    void handle(TemplateInfo<T> info);

}
