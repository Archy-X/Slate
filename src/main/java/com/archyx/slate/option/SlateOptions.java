package com.archyx.slate.option;

import com.archyx.slate.item.provider.KeyedItemProvider;

import java.io.File;
import java.util.List;

public record SlateOptions(
        File mainDirectory,
        List<File> mergeDirectories,
        int loreWrappingWidth,
        KeyedItemProvider keyedItemProvider
) {

    public static SlateOptionsBuilder builder() {
        return new SlateOptionsBuilder();
    }

}
