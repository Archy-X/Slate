package com.archyx.slate.action.builder;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;

public abstract class ActionBuilder {

    protected final Slate slate;

    public ActionBuilder(Slate slate) {
        this.slate = slate;
    }

    public abstract Action build();

}
