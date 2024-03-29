package com.archyx.slate.lore.type;

import com.archyx.slate.lore.LoreStyles;
import com.archyx.slate.lore.LoreType;
import com.archyx.slate.lore.LoreLine;

public class TextLore extends LoreLine {

    private final String text;
    private final LoreStyles styles;
    private final boolean wrap;
    private final int wrapStyle;
    private final boolean smartWrap;
    private final String wrapIndent;

    public TextLore(String text, LoreStyles styles, boolean wrap, int wrapStyle, boolean smartWrap, String wrapIndent) {
        super(LoreType.TEXT);
        this.text = text;
        this.styles = styles;
        this.wrap = wrap;
        this.wrapStyle = wrapStyle;
        this.smartWrap = smartWrap;
        this.wrapIndent = wrapIndent;
    }

    public String getText() {
        return text;
    }

    public LoreStyles getStyles() {
        return styles;
    }

    public boolean shouldWrap() {
        return wrap;
    }

    public int getWrapStyle() {
        return wrapStyle;
    }

    public boolean isSmartWrap() {
        return smartWrap;
    }

    public String getWrapIndent() {
        return wrapIndent;
    }
}
