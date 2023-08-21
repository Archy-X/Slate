package com.archyx.slate.item.provider;

public class ListBuilder {

    private final ListInsertion insertion;
    private String list;

    public ListBuilder(ListInsertion insertion) {
        this.insertion = insertion;
    }

    public ListBuilder append(String text) {
        if (list == null) {
            list = text;
        } else {
            list += insertion.getInsert() + text;
        }
        return this;
    }

    public String build() {
        return list;
    }

}
