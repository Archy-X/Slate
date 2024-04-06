package com.archyx.slate.function;

import com.archyx.slate.info.MenuInfo;

@FunctionalInterface
public interface PageProvider {

    int getPages(MenuInfo info);

}
