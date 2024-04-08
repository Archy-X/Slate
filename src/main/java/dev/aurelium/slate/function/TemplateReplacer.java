package dev.aurelium.slate.function;

import dev.aurelium.slate.info.TemplatePlaceholderInfo;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TemplateReplacer<T> {

    @Nullable
    String replace(TemplatePlaceholderInfo<T> info);

}
