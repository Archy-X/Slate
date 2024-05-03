package dev.aurelium.slate.action.condition;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.menu.MenuInventory;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderCondition extends Condition {

    private final String placeholder;
    private final String value;

    public PlaceholderCondition(Slate slate, String placeholder, String value) {
        super(slate);
        this.placeholder = placeholder;
        this.value = value;
    }

    @Override
    public boolean isMet(Player player, MenuInventory menuInventory) {
        String leftText = placeholder;
        if (slate.isPlaceholderAPIEnabled()) {
            leftText = PlaceholderAPI.setPlaceholders(player, leftText);
        }
        String rightText = value;
        if (slate.isPlaceholderAPIEnabled()) {
            rightText = PlaceholderAPI.setPlaceholders(player, rightText);
        }
        return leftText.equals(rightText);
    }
}
