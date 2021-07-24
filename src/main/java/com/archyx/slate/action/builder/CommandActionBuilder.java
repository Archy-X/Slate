package com.archyx.slate.action.builder;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.CommandAction;

public class CommandActionBuilder extends ActionBuilder {

    private String command;
    private CommandAction.Executor executor;

    public CommandActionBuilder(Slate slate) {
        super(slate);
    }

    public CommandActionBuilder command(String command) {
        this.command = command;
        return this;
    }

    public CommandActionBuilder executor(CommandAction.Executor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public Action build() {
        return new CommandAction(slate, command, executor);
    }
}
