package com.archyx.slate.action;

import com.archyx.slate.Slate;
import com.archyx.slate.menu.MenuInventory;
import com.archyx.slate.util.TextUtil;
import fr.minuskube.inv.content.InventoryContents;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandAction extends Action {

    private final String command;
    private final Executor executor;

    public CommandAction(Slate slate, String command, Executor executor) {
        super(slate);
        this.command = command;
        this.executor = executor;
    }

    @Override
    public void execute(Player player, MenuInventory menuInventory, InventoryContents contents) {
        String formattedCommand = formatCommand(player, command);
        if (executor == Executor.CONSOLE) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), formattedCommand);
        } else if (executor == Executor.PLAYER) {
            player.performCommand(formattedCommand);
        }
    }

    private String formatCommand(Player player, String command) {
        command = TextUtil.replace(command, "{player}", player.getName());
        command = PlaceholderAPI.setPlaceholders(player, command);
        return command;
    }

    public enum Executor {

        CONSOLE,
        PLAYER

    }

}
