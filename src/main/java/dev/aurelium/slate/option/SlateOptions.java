package dev.aurelium.slate.option;

import dev.aurelium.slate.item.provider.KeyedItemProvider;

import java.io.File;
import java.util.List;

public record SlateOptions(
        File mainDirectory,
        List<File> mergeDirectories,
        int loreWrappingWidth,
        KeyedItemProvider keyedItemProvider,
        boolean nbtEnabled
) {

    public static SlateOptionsBuilder builder() {
        return new SlateOptionsBuilder();
    }

}
