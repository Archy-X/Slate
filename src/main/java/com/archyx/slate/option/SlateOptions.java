package com.archyx.slate.option;

public record SlateOptions(int loreWrappingWidth) {

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
