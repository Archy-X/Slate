package dev.aurelium.slate.fill;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.inv.content.SlotPos;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public record FillData(FillItem item, SlotPos[] slots, boolean enabled) {

    public static FillData empty(Slate slate) {
        return new FillData(new FillItem(slate, new ItemStack(Material.BLACK_STAINED_GLASS_PANE)), new SlotPos[0], false);
    }

    @Override
    @Nullable
    public SlotPos[] slots() {
        return slots;
    }

}
