package com.archyx.slate.function;

import com.archyx.slate.info.TemplateInfo;

@FunctionalInterface
public interface ComponentVisibility<T> {

    boolean shouldShow(TemplateInfo<T> info);

}
