package com.archyx.slate.action.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.CommandAction;
import com.archyx.slate.action.builder.CommandActionBuilder;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Locale;
import java.util.Objects;

public class CommandActionParser extends ActionParser {

    public CommandActionParser(Slate slate) {
        super(slate);
    }

    @Override
    public Action parse(ConfigurationNode config) {
        return new CommandActionBuilder(slate)
                .command(Objects.requireNonNull(config.node("command").getString()))
                .executor(CommandAction.Executor.valueOf(config.node("executor").getString("console").toUpperCase(Locale.ROOT)))
                .build();
    }
}
