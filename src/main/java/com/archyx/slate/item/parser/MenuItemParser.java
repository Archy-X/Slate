package com.archyx.slate.item.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.click.ClickAction;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.builder.MenuItemBuilder;
import com.archyx.slate.util.MapParser;
import com.archyx.slate.util.TextUtil;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.minuskube.inv.content.SlotPos;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class MenuItemParser extends MapParser {

    protected final Slate slate;
    private final String[] KEY_WORDS = new String[] {
        "material", "display_name", "lore", "enchantments", "potion_data", "custom_effects", "glow", "nbt", "item_flags"
    };

    public MenuItemParser(Slate slate) {
        this.slate = slate;
    }

    public abstract MenuItem parse(ConfigurationSection section, String menuName);

    @SuppressWarnings("deprecation")
    protected ItemStack parseBaseItem(ConfigurationSection section) {
        String materialString = section.getString("material");
        Validate.notNull(materialString, "Item must specify a material");

        ItemStack item;
        if (!materialString.contains(":")) { // No legacy data
            String materialName = materialString.toUpperCase(Locale.ROOT);
            Material material = parseMaterial(materialName);
            if (material == null) {
                throw new IllegalArgumentException("Unknown material " + materialString);
            }
            item = new ItemStack(material);
        } else { // With legacy data
            String[] splitMaterial = materialString.split(":");
            if (splitMaterial.length == 2) {
                String materialName = splitMaterial[0].toUpperCase(Locale.ROOT);
                Material material = parseMaterial(materialName);
                if (material == null) {
                    throw new IllegalArgumentException("Unknown material " + materialName);
                }
                short data = NumberUtils.toShort(splitMaterial[1]);
                item = new ItemStack(material, 1, data);
            } else {
                throw new IllegalArgumentException("Material with data value can only have one :");
            }
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        // Enchantments
        if (section.contains("enchantments")) {
            List<String> enchantmentStrings = section.getStringList("enchantments");
            for (String enchantmentEntry : enchantmentStrings) {
                String[] splitEntry = enchantmentEntry.split(" ");
                String enchantmentName = splitEntry[0];
                int level = 1;
                if (splitEntry.length > 1) {
                    level = NumberUtils.toInt(splitEntry[1], 1);
                }
                Optional<XEnchantment> xEnchantment = XEnchantment.matchXEnchantment(enchantmentName.toUpperCase(Locale.ROOT));
                if (xEnchantment.isPresent()) {
                    Enchantment enchantment = xEnchantment.get().parseEnchantment();
                    if (enchantment != null) {
                        if (item.getType() == Material.ENCHANTED_BOOK && meta instanceof EnchantmentStorageMeta) {
                            EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
                            esm.addStoredEnchant(enchantment, level, true);
                            item.setItemMeta(esm);
                        } else {
                            meta.addEnchant(enchantment, level, true);
                            item.setItemMeta(meta);
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid enchantment name " + enchantmentName);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid enchantment name " + enchantmentName);
                }
            }
        }
        // Potions
        ConfigurationSection potionDataSection = section.getConfigurationSection("potion_data");
        if (potionDataSection != null) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            PotionType potionType = PotionType.valueOf(potionDataSection.getString("type", "WATER").toUpperCase(Locale.ROOT));
            boolean extended = false;
            if (potionDataSection.contains("extended")) {
                extended = potionDataSection.getBoolean("extended");
            }
            boolean upgraded = false;
            if (potionDataSection.contains("upgraded")) {
                upgraded = potionDataSection.getBoolean("upgraded");
            }

            PotionData potionData = new PotionData(potionType, extended, upgraded);
            potionMeta.setBasePotionData(potionData);
            item.setItemMeta(potionMeta);
        }
        // Custom potion effects
        if (section.contains("custom_effects")) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            for (Map<?, ?> effectMap : section.getMapList("custom_effects")) {
                String effectName = getString(effectMap, "type");
                PotionEffectType type = PotionEffectType.getByName(effectName);
                if (type != null) {
                    int duration = getInt(effectMap, "duration");
                    int amplifier = getInt(effectMap, "amplifier");
                    potionMeta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
                    potionMeta.setColor(type.getColor());
                } else {
                    throw new IllegalArgumentException("Invalid potion effect type " + effectName);
                }
            }
            item.setItemMeta(potionMeta);
        }
        // Glowing w/o enchantments visible
        if (section.getBoolean("glow", false)) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        // Custom NBT
        if (section.contains("nbt")) {
            ConfigurationSection nbtSection = section.getConfigurationSection("nbt");
            if (nbtSection != null) {
                Map<?, ?> nbtMap = nbtSection.getValues(true);
                item = parseNBT(item, nbtMap);
            }
        }
        return item;
    }

    @Nullable
    protected String parseDisplayName(ConfigurationSection section) {
        if (section.contains("display_name")) {
            return TextUtil.replaceNonEscaped(section.getString("display_name"), "&", "§");
        }
        return null;
    }

    protected List<String> parseLore(ConfigurationSection section) {
        if (section.contains("lore")) {
            List<String> lore = section.getStringList("lore");
            List<String> formattedLore = new ArrayList<>();
            for (String line : lore) {
                formattedLore.add(TextUtil.replaceNonEscaped(line, "&", "§"));
            }
            return formattedLore;
        }
        return new ArrayList<>();
    }

    private ItemStack parseNBT(ItemStack item, Map<?, ?> map) {
        NBTItem nbtItem = new NBTItem(item);
        applyMapToNBT(nbtItem, map);
        return nbtItem.getItem();
    }

    private void applyMapToNBT(NBTItem item, Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key instanceof String) {
                if (value instanceof Map<?, ?>) { // Recursively apply sub maps
                    applyMapToNBT(item, (Map<?, ?>) value);
                } else {
                    item.setObject((String) key, value);
                }
            }
        }
    }

    protected Material parseMaterial(String name) {
        Material material = Material.getMaterial(name);
        if (material != null) {
            return material;
        }
        Optional<XMaterial> materialOptional = XMaterial.matchXMaterial(name);
        return materialOptional.map(XMaterial::parseMaterial).orElse(null);
    }

    protected SlotPos parsePosition(String input) {
        String[] splitInput = input.split(",", 2);
        if (splitInput.length == 2) {
            int row = Integer.parseInt(splitInput[0]);
            int column = Integer.parseInt(splitInput[1]);
            return SlotPos.of(row, column);
        } else {
            int slot = Integer.parseInt(input);
            int row = slot / 9;
            int column = slot % 9;
            return SlotPos.of(row, column);
        }
    }

    protected String[] getKeyWords() {
        return KEY_WORDS;
    }

    protected boolean isKeyWord(String word) {
        for (String keyWord : getKeyWords()) {
            if (keyWord.equals(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses actions of each type and adds it to a given item
     */
    protected void parseActions(MenuItemBuilder builder, Map<?, ?> map, String menuName, String itemName) {
        Map<ClickAction, List<Action>> actions = new LinkedHashMap<>();
        for (ClickAction clickAction : ClickAction.values()) {
            String id = clickAction.getId();
            if (map.containsKey(id)) {
                List<Action> clickActions = slate.getActionManager().parseActions(getMapList(map, id), menuName, itemName);
                actions.put(clickAction, clickActions);
            }
        }
        builder.actions(actions);
    }

}
