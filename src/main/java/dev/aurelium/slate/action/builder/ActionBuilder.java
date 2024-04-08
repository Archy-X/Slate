package dev.aurelium.slate.action.builder;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;

public abstract class ActionBuilder {

    protected final Slate slate;

    public ActionBuilder(Slate slate) {
        this.slate = slate;
    }

    public abstract Action build();

}
