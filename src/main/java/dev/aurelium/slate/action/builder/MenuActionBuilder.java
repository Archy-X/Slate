package dev.aurelium.slate.action.builder;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.MenuAction;
import dev.aurelium.slate.action.MenuAction.ActionType;

import java.util.Map;

public class MenuActionBuilder extends ActionBuilder {

    private String menuName;
    private ActionType actionType;
    private Map<String, Object> properties;

    public MenuActionBuilder(Slate slate) {
        super(slate);
    }

    public MenuActionBuilder menuName(String menuName) {
        this.menuName = menuName;
        return this;
    }

    public MenuActionBuilder actionType(ActionType actionType) {
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
