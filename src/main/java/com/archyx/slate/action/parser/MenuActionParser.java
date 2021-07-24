package com.archyx.slate.action.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.MenuAction;
import com.archyx.slate.action.builder.MenuActionBuilder;

import java.util.Locale;
import java.util.Map;

public class MenuActionParser extends ActionParser {

    public MenuActionParser(Slate slate) {
        super(slate);
    }

    @Override
    public Action parse(Map<?, ?> map) {
        return new MenuActionBuilder(slate)
                .actionType(MenuAction.ActionType.valueOf(getString(map, "action").toUpperCase(Locale.ROOT)))
                .menuName(getString(map, "menu", null))
                .build();
    }
}
