package dev.aurelium.slate.action.builder;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.action.Action;
import dev.aurelium.slate.action.CommandAction;
import dev.aurelium.slate.action.CommandAction.Executor;

public class CommandActionBuilder extends ActionBuilder {

    private String command;
    private Executor executor;

    public CommandActionBuilder(Slate slate) {
        super(slate);
    }

    public CommandActionBuilder command(String command) {
        this.command = command;
        return this;
    }

    public CommandActionBuilder executor(Executor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public Action build() {
        return new CommandAction(slate, command, executor);
    }
}
