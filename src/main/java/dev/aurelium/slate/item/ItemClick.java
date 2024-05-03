package dev.aurelium.slate.item;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.builder.GlobalBehavior;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * Stores contextual data for when a player clicks an item.
 */
public class ItemClick {

    private final Slate slate;
    private final Player player;
    private final InventoryClickEvent event;
    private final ItemStack item;
    private final SlotPos pos;
    private final ActiveMenu menu;

    public ItemClick(Slate slate, Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu menu) {
        this.slate = slate;
        this.player = player;
        this.event = event;
        this.item = item;
        this.pos = pos;
        this.menu = menu;
    }

    /**
     * Gets the player that clicked the item.
     *
     * @return the player
     */
    public Player player() {
        return player;
    }

    /**
     * Gets the locale of the player as defined by the {@link GlobalBehavior#localeProvider()}.
     * If the locale provider is not set, this will always return {@code Locale.ENGLISH}. This is useful if you
     * have player-dependent locales.
     *
     * @return the locale of the player
     */
    public Locale locale() {
        return slate.getGlobalBehavior().localeProvider().get(player);
    }

    /**
     * Gets the original Bukkit event for the inventory click.
     *
     * @return the InventoryClickEvent
     */
    public InventoryClickEvent event() {
        return event;
    }

    /**
     * Gets the ItemStack that was clicked.
     *
     * @return the clicked ItemStack
     */
    public ItemStack item() {
        return item;
    }

    /**
     * Gets the slot position that was clicked.
     *
     * @return the clicked slot
     */
    public SlotPos pos() {
        return pos;
    }

    /**
     * Gets the active menu the player is viewing.
     *
     * @return the active menu
     */
    public ActiveMenu menu() {
        return menu;
    }
}
