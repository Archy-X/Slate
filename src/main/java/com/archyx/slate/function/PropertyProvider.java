package com.archyx.slate.function;

import com.archyx.slate.info.MenuInfo;

import java.util.Map;

@FunctionalInterface
public interface PropertyProvider {

    Map<String, Object> get(MenuInfo info);

}
