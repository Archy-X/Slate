package com.archyx.slate.action.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.CommandAction;
import com.archyx.slate.action.builder.CommandActionBuilder;

import java.util.Locale;
import java.util.Map;

public class CommandActionParser extends ActionParser {

    public CommandActionParser(Slate slate) {
        super(slate);
    }

    @Override
    public Action parse(Map<?, ?> map) {
        return new CommandActionBuilder(slate)
                .command(getString(map, "command"))
                .executor(CommandAction.Executor.valueOf(getString(map, "executor", "console").toUpperCase(Locale.ROOT)))
                .build();
    }
}
