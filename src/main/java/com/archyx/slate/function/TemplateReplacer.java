package com.archyx.slate.function;

import com.archyx.slate.info.TemplatePlaceholderInfo;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TemplateReplacer<T> {

    @Nullable
    String replace(TemplatePlaceholderInfo<T> info);

}
