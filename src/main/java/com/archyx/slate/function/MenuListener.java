package com.archyx.slate.function;

import com.archyx.slate.info.MenuInfo;

@FunctionalInterface
public interface MenuListener {

    void handle(MenuInfo info);

}
