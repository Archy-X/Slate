package com.archyx.slate.item.provider;

import com.archyx.slate.util.TextUtil;

public class ListBuilder {

    private final String insertion;
    private String list;

    public ListBuilder(String insertion) {
        this.insertion = insertion;
    }

    public ListBuilder append(String text, String... rep) {
        if (rep != null) {
            text = replace(text, rep);
        }
        if (list == null) {
            list = text;
        } else {
            list += insertion + text;
        }
        return this;
    }

    public String build() {
        return list;
    }

    private String replace(String source, String... rep) {
        if (source == null) {
            return null;
        }
        if (rep.length % 2 != 0) {
            throw new IllegalArgumentException("The number of arguments must be even!");
        }
        for (int i = 0; i < rep.length; i += 2) {
            source = TextUtil.replace(source, rep[i], rep[i + 1]);
        }
        return source;
    }

}
