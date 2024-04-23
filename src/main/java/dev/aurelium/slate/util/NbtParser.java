package dev.aurelium.slate.util;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Map;

public class NbtParser {

    public ItemStack parseNBT(ItemStack item, Map<Object, ? extends ConfigurationNode> map) {
        NBTItem nbtItem = new NBTItem(item);
        applyMapToNBT(nbtItem, map);
        return nbtItem.getItem();
    }

    private void applyMapToNBT(NBTCompound item, Map<Object, ? extends ConfigurationNode> map) {
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : map.entrySet()) {
            Object keyObj = entry.getKey();
            Object value = entry.getValue().raw();
            if (keyObj instanceof String key) {
                if (key.equals("CustomModelData")) { // Parsed elsewhere
                    continue;
                }
                if (value instanceof ConfigurationNode childNode) {
                    // Recursively apply sub maps
                    applyMapToNBT(item.getOrCreateCompound(key), childNode.childrenMap());
                } else {
                    if (value instanceof Integer) {
                        item.setInteger(key, (int) value);
                    } else if (value instanceof Double) {
                        item.setDouble(key, (double) value);
                    } else if (value instanceof Boolean) {
                        item.setBoolean(key, (boolean) value);
                    } else if (value instanceof String) {
                        item.setString(key, (String) value);
                    }
                }
            }
        }
    }

    public ItemStack parseNBTString(ItemStack item, String nbtString) {
        NBTContainer container = new NBTContainer(nbtString);
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.mergeCompound(container);
        return nbtItem.getItem();
    }

}
