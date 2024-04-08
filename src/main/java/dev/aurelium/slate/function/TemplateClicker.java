package dev.aurelium.slate.function;

import dev.aurelium.slate.item.TemplateClick;

@FunctionalInterface
public interface TemplateClicker<T> {

    void click(TemplateClick<T> click);

}
