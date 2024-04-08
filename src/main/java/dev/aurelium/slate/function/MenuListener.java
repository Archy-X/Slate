package dev.aurelium.slate.function;

import dev.aurelium.slate.info.MenuInfo;

@FunctionalInterface
public interface MenuListener {

    void handle(MenuInfo info);

}
