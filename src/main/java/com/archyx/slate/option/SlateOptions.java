package com.archyx.slate.option;

public class SlateOptions {

    private final int loreWrappingWidth;

    public SlateOptions(int loreWrappingWidth) {
        this.loreWrappingWidth = loreWrappingWidth;
    }

    public int getLoreWrappingWidth() {
        return loreWrappingWidth;
    }

    public static class SlateOptionsBuilder {

        private int loreWrappingWidth = 40;

        public SlateOptionsBuilder loreWrappingWidth(int loreWrappingWidth) {
            this.loreWrappingWidth = loreWrappingWidth;
            return this;
        }

        public SlateOptions build() {
            return new SlateOptions(loreWrappingWidth);
        }

    }

}
