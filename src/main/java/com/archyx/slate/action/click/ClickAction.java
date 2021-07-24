package com.archyx.slate.action.click;

public enum ClickAction {

    ANY("on_click"),
    LEFT("on_left_click"),
    RIGHT("on_right_click"),
    MIDDLE("on_middle_click"),
    DROP("on_drop");

    private final String identifier;

    ClickAction(String id) {
        this.identifier = id;
    }

    public String getId() {
        return identifier;
    }

}
