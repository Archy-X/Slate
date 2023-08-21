package com.archyx.slate.item.provider;

public enum ListInsertion {

    NOTHING(""),
    NEWLINE("\\n"),
    COMMA_SPACE(", ");

    private final String insert;

    ListInsertion(String insert) {
        this.insert = insert;
    }

    public String getInsert() {
        return insert;
    }

    public static ListInsertion parseFromInsert(String insert) {
        for (ListInsertion insertion : values()) {
            if (insertion.getInsert().equals(insert)) {
                return insertion;
            }
        }
        return NOTHING;
    }

}
