package com.archyx.slate.action.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.util.MapParser;
import org.spongepowered.configurate.ConfigurationNode;

public abstract class ActionParser extends MapParser {

    protected final Slate slate;

    public ActionParser(Slate slate) {
        this.slate = slate;
    }

    public abstract Action parse(ConfigurationNode config);

}
