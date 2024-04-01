package com.archyx.slate.function;

import org.bukkit.entity.Player;

import java.util.Locale;

@FunctionalInterface
public interface LocaleProvider {

    Locale get(Player player);

}
