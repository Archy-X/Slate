package com.archyx.slate.action.builder;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.MenuAction;

import java.util.Map;

public class MenuActionBuilder extends ActionBuilder {

    private String menuName;
    private MenuAction.ActionType actionType;
    private Map<String, Object> properties;

    public MenuActionBuilder(Slate slate) {
        super(slate);
    }

    public MenuActionBuilder menuName(String menuName) {
        this.menuName = menuName;
        return this;
    }

    public MenuActionBuilder actionType(MenuAction.ActionType actionType) {
        this.actionType = actionType;
        return this;
    }

    public MenuActionBuilder properties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public Action build() {
        return new MenuAction(slate, actionType, menuName, properties);
    }
}
