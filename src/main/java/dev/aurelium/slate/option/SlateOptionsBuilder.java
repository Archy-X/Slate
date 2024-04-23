package dev.aurelium.slate.option;

import dev.aurelium.slate.item.provider.KeyedItemProvider;
import dev.aurelium.slate.util.Validate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SlateOptionsBuilder {

    private File mainDirectory;
    private List<File> mergeDirectories = new ArrayList<>();
    private int loreWrappingWidth = 40;
    private KeyedItemProvider keyedItemProvider = k -> null;
    private boolean nbtEnabled = false;

    public SlateOptionsBuilder mainDirectory(File mainDirectory) {
        this.mainDirectory = mainDirectory;
        return this;
    }

    public SlateOptionsBuilder mergeDirectories(List<File> mergeDirectories) {
        this.mergeDirectories = mergeDirectories;
        return this;
    }

    public SlateOptionsBuilder loreWrappingWidth(int loreWrappingWidth) {
        this.loreWrappingWidth = loreWrappingWidth;
        return this;
    }

    public SlateOptionsBuilder keyedItemProvider(KeyedItemProvider keyedItemProvider) {
        this.keyedItemProvider = keyedItemProvider;
        return this;
    }

    public SlateOptionsBuilder nbtEnabled(boolean nbtEnabled) {
        this.nbtEnabled = nbtEnabled;
        return this;
    }

    public SlateOptions build() {
        Validate.notNull(mainDirectory, "mainDirectory not defined in SlateOptions");
        return new SlateOptions(mainDirectory, mergeDirectories, loreWrappingWidth, keyedItemProvider, nbtEnabled);
    }

}
