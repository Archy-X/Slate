package dev.aurelium.slate.function;

import dev.aurelium.slate.info.MenuInfo;

import java.util.Map;

@FunctionalInterface
public interface PropertyProvider {

    Map<String, Object> get(MenuInfo info);

}
