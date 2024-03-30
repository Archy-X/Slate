package com.archyx.slate.function;

import com.archyx.slate.item.TemplateClick;

@FunctionalInterface
public interface TemplateClicker<T> {

    void click(TemplateClick<T> click);

}
