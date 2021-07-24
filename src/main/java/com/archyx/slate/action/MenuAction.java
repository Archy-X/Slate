package com.archyx.slate.action;

import com.archyx.slate.Slate;
import com.archyx.slate.menu.MenuInventory;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.entity.Player;

public class MenuAction extends Action {

    private final ActionType actionType;
    private final String menuName;

    public MenuAction(Slate slate, ActionType actionType, String menuName) {
        super(slate);
        this.actionType = actionType;
        this.menuName = menuName;
    }

    @Override
    public void execute(Player player, MenuInventory menuInventory, InventoryContents contents) {
        switch (actionType) {
            case OPEN:
                slate.getMenuManager().openMenu(player, menuName);
                break;
            case CLOSE:
                player.closeInventory();
                break;
            case NEXT_PAGE:
                contents.pagination().next();
                break;
            case PREVIOUS_PAGE:
                contents.pagination().previous();
                break;
        }

    }

    public enum ActionType {

        OPEN,
        CLOSE,
        NEXT_PAGE,
        PREVIOUS_PAGE

    }

}
