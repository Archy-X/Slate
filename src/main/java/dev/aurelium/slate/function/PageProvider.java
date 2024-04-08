package dev.aurelium.slate.function;

import dev.aurelium.slate.info.MenuInfo;

@FunctionalInterface
public interface PageProvider {

    int getPages(MenuInfo info);

}
