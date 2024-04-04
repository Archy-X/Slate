package com.archyx.slate.builder;

import com.archyx.slate.function.ItemReplacer;
import com.archyx.slate.function.LocaleProvider;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class GlobalOptionsBuilder {

    private final Set<ItemReplacer> globalReplacers = new HashSet<>();
    private LocaleProvider localeProvider = p -> Locale.ENGLISH;

    private GlobalOptionsBuilder() {

    }

    public static GlobalOptionsBuilder builder() {
        return new GlobalOptionsBuilder();
    }

    public GlobalOptionsBuilder replacer(ItemReplacer replacer) {
        globalReplacers.add(replacer);
        return this;
    }

    public GlobalOptionsBuilder localeProvider(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
        return this;
    }

    public GlobalOptions build() {
        return new GlobalOptions(globalReplacers, localeProvider);
    }

}