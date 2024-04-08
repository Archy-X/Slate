package dev.aurelium.slate.action;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.menu.MenuInventory;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.entity.Player;

public abstract class Action {

    protected final Slate slate;

    public Action(Slate slate) {
        this.slate = slate;
    }

    public abstract void execute(Player player, MenuInventory menuInventory, InventoryContents contents);

}
