package com.archyx.slate.function;

import com.archyx.slate.info.TemplateInfo;

@FunctionalInterface
public interface ContextListener<T> {

    void handle(TemplateInfo<T> info);

}
