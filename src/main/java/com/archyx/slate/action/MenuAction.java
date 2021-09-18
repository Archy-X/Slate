package com.archyx.slate.action;

import com.archyx.slate.Slate;
import com.archyx.slate.menu.MenuInventory;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.entity.Player;

import java.util.Map;

public class MenuAction extends Action {

    private final ActionType actionType;
    private final String menuName;
    private final Map<String, Object> properties;

    public MenuAction(Slate slate, ActionType actionType, String menuName, Map<String, Object> properties) {
        super(slate);
        this.actionType = actionType;
        this.menuName = menuName;
        this.properties = properties;
    }

    @Override
    public void execute(Player player, MenuInventory menuInventory, InventoryContents contents) {
        switch (actionType) {
            case OPEN:
                slate.getMenuManager().openMenu(player, menuName, properties);
                break;
            case CLOSE:
                player.closeInventory();
                break;
            case NEXT_PAGE:
                int nextPage = menuInventory.getCurrentPage() + 1;
                if (nextPage < menuInventory.getTotalPages()) {
                    slate.getMenuManager().openMenu(player, menuInventory.getMenu().getName(), properties, nextPage);
                }
                break;
            case PREVIOUS_PAGE:
                int previousPage = menuInventory.getCurrentPage() - 1;
                if (previousPage >= 0) {
                    slate.getMenuManager().openMenu(player, menuInventory.getMenu().getName(), properties, previousPage);
                }
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
