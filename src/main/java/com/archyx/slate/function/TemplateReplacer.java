package com.archyx.slate.function;

import com.archyx.slate.info.TemplatePlaceholderInfo;

@FunctionalInterface
public interface TemplateReplacer<T> {

    String replace(TemplatePlaceholderInfo<T> info);

}
