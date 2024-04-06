package com.archyx.slate.function;

import com.archyx.slate.info.TemplateInfo;

@FunctionalInterface
public interface ComponentInstances<T> {

   int getInstances(TemplateInfo<T> info);

}
