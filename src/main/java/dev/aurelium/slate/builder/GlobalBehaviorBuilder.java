package dev.aurelium.slate.builder;

import dev.aurelium.slate.function.ItemReplacer;
import dev.aurelium.slate.function.LocaleProvider;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class GlobalBehaviorBuilder {

    private final Set<ItemReplacer> globalReplacers = new HashSet<>();
    private LocaleProvider localeProvider = p -> Locale.ENGLISH;

    private GlobalBehaviorBuilder() {

    }

    public static GlobalBehaviorBuilder builder() {
        return new GlobalBehaviorBuilder();
    }

    public GlobalBehaviorBuilder replacer(ItemReplacer replacer) {
        globalReplacers.add(replacer);
        return this;
    }

    public GlobalBehaviorBuilder localeProvider(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
        return this;
    }

    public GlobalBehavior build() {
        return new GlobalBehavior(globalReplacers, localeProvider);
    }

}
