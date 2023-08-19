package com.archyx.slate.lore;

import com.archyx.slate.lore.parser.ComponentLoreParser;
import com.archyx.slate.lore.parser.LoreParser;
import com.archyx.slate.lore.parser.TextLoreParser;

public enum LoreType {

    TEXT(TextLoreParser.class),
    COMPONENT(ComponentLoreParser.class);

    private final Class<? extends LoreParser> parserClass;

    LoreType(Class<? extends LoreParser> parserClass) {
        this.parserClass = parserClass;
    }

    public Class<? extends LoreParser> getParserClass() {
        return parserClass;
    }
}
