package com.archyx.slate.menu;

import com.archyx.slate.component.MenuComponent;
import com.archyx.slate.fill.FillData;
import com.archyx.slate.item.MenuItem;

import java.util.Map;

public record LoadedMenu(
        String name,
        String title,
        int size,
        Map<String, MenuItem> items,
        Map<String, MenuComponent> components,
        Map<String, String> formats,
        FillData fillData,
        Map<String, Object> options
) {
}
