package dev.aurelium.slate.function;

import dev.aurelium.slate.info.TemplateInfo;

@FunctionalInterface
public interface ComponentInstances<T> {

   int getInstances(TemplateInfo<T> info);

}
