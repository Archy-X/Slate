package com.archyx.slate.item.parser;

import com.archyx.slate.Slate;
import com.archyx.slate.action.Action;
import com.archyx.slate.action.click.ClickAction;
import com.archyx.slate.item.MenuItem;
import com.archyx.slate.item.builder.MenuItemBuilder;
import com.archyx.slate.item.provider.KeyedItemProvider;
import com.archyx.slate.util.MapParser;
import com.archyx.slate.util.TextUtil;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.dbassett.skullcreator.SkullCreator;
import fr.minuskube.inv.content.SlotPos;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MenuItemParser extends MapParser {

    protected final Slate slate;
    private final String[] KEY_WORDS = new String[] {
        "pos", "material", "display_name", "lore", "enchantments", "potion_data", "custom_effects", "glow", "nbt", "flags", "durability", "skull_meta"
    };
    private final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public MenuItemParser(Slate slate) {
        this.slate = slate;
    }

    public abstract MenuItem parse(ConfigurationSection section, String menuName);

    public ItemStack parseBaseItem(ConfigurationSection section) {
        String key = section.getString("key");
        if (key != null) {
            ItemStack item = parseItemKey(key);
            if (item != null) {
                return item; // Returns the item if key parse was successful
            }
        }

        String materialString = section.getString("material");
        Validate.notNull(materialString, "Item must specify a material");

        ItemStack item = parseMaterialString(materialString);

        parseAmount(item, section);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        // Enchantments
        if (section.contains("enchantments")) {
            parseEnchantments(item, section);
        }
        // Potions
        ConfigurationSection potionDataSection = section.getConfigurationSection("potion_data");
        if (potionDataSection != null) {
            parsePotionData(item, potionDataSection);
        }
        // Custom potion effects
        if (section.contains("custom_effects")) {
            parseCustomEffects(section, item);
        }
        // Glowing w/o enchantments visible
        if (section.getBoolean("glow", false)) {
            parseGlow(item);
        }
        // Custom NBT
        if (section.contains("nbt")) {
            if (section.isConfigurationSection("nbt")) {
                ConfigurationSection nbtSection = section.getConfigurationSection("nbt");
                if (nbtSection != null) {
                    Map<?, ?> nbtMap = nbtSection.getValues(true);
                    item = parseNBT(item, nbtMap);
                }
            } else if (section.isString("nbt")) {
                String nbtString = section.getString("nbt");
                if (nbtString != null) {
                    item = parseNBTString(item, nbtString);
                }
            }
        }
        if (section.contains("flags")) {
            parseFlags(section, item);
        }
        if (section.contains("durability")) {
            parseDurability(section, item);
        }
        ConfigurationSection skullMetaSection = section.getConfigurationSection("skull_meta");
        if (skullMetaSection != null) {
            parseSkullMeta(item, item.getItemMeta(), skullMetaSection);
        }
        return item;
    }

    @Nullable
    private ItemStack parseItemKey(String key) {
        KeyedItemProvider provider = slate.getMenuManager().getGlobalProviderManager().getKeyedItemProvider();
        if (provider != null) {
            return provider.getItem(key);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private void parseDurability(ConfigurationSection section, ItemStack item) {
        ItemMeta meta = getMeta(item);
        int durability = section.getInt("durability");
        if (XMaterial.isNewVersion()) {
            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;
                short maxDurability = item.getType().getMaxDurability();
                damageable.setDamage(Math.max(maxDurability - durability, maxDurability));
                item.setItemMeta(meta);
            }
        } else {
            // For old versions
            short maxDurability = item.getType().getMaxDurability();
            item.setDurability((short) Math.max(maxDurability - durability, maxDurability));
        }
    }

    private void parseFlags(ConfigurationSection section, ItemStack item) {
        ItemMeta meta = getMeta(item);
        List<String> flags = section.getStringList("flags");
        for (String flagName : flags) {
            ItemFlag itemFlag = ItemFlag.valueOf(flagName.toUpperCase(Locale.ROOT));
            meta.addItemFlags(itemFlag);
        }
        item.setItemMeta(meta);
    }

    private void parseGlow(ItemStack item) {
        ItemMeta meta = getMeta(item);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    private void parseCustomEffects(ConfigurationSection section, ItemStack item) {
        PotionMeta potionMeta = (PotionMeta) getMeta(item);
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

    private void parsePotionData(ItemStack item, ConfigurationSection potionDataSection) {
        PotionMeta potionMeta = (PotionMeta) getMeta(item);
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

    private void parseEnchantments(ItemStack item, ConfigurationSection section) {
        ItemMeta meta = getMeta(item);
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
                Enchantment enchantment = xEnchantment.get().getEnchant();
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

    @SuppressWarnings("deprecation")
    private ItemStack parseMaterialString(String materialString) {
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
        return item;
    }

    private @NotNull ItemMeta getMeta(ItemStack item) {
        return Objects.requireNonNull(item.getItemMeta());
    }

    @Nullable
    protected String parseDisplayName(ConfigurationSection section) {
        if (section.contains("display_name")) {
            return applyColor(section.getString("display_name"));
        }
        return null;
    }

    private String applyColor(String message) {
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            char COLOR_CHAR = ChatColor.COLOR_CHAR;
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        message = matcher.appendTail(buffer).toString();
        return TextUtil.replaceNonEscaped(message, "&", "§");
    }

    protected List<String> parseLore(ConfigurationSection section) {
        if (section.contains("lore")) {
            List<String> lore = section.getStringList("lore");
            List<String> formattedLore = new ArrayList<>();
            for (String line : lore) {
                formattedLore.add(applyColor(line));
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

    private void applyMapToNBT(NBTCompound item, Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key instanceof String) {
                if (value instanceof Map<?, ?>) { // Recursively apply sub maps
                    applyMapToNBT(item.getOrCreateCompound((String) key), (Map<?, ?>) value);
                } else {
                    if (value instanceof Integer) {
                        item.setInteger((String) key, (int) value);
                    } else if (value instanceof Double) {
                        item.setDouble((String) key, (double) value);
                    } else if (value instanceof Boolean) {
                        item.setBoolean((String) key, (boolean) value);
                    } else if (value instanceof String) {
                        item.setString((String) key, (String) value);
                    }
                }
            }
        }
    }

    private ItemStack parseNBTString(ItemStack item, String nbtString) {
        NBTContainer container = new NBTContainer(nbtString);
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.mergeCompound(container);
        return nbtItem.getItem();
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

    private void parseSkullMeta(ItemStack item, ItemMeta meta, ConfigurationSection section) {
        if (!(meta instanceof SkullMeta)) {
            return;
        }
        SkullMeta skullMeta = (SkullMeta) meta;
        String uuid = section.getString("uuid");
        if (uuid != null) { // From UUID of player
            UUID id = UUID.fromString(uuid);
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
            item.setItemMeta(meta);
        }
        String base64 = section.getString("base64");
        if (base64 != null) { // From base64 string
            SkullCreator.itemWithBase64(item, base64);
        }
        String url = section.getString("url");
        if (url != null) { // From Mojang URL
            SkullCreator.itemWithUrl(item, url);
        }
        if (XMaterial.getVersion() >= 14) { // Persistent data container requires 1.14+
            String placeholder = section.getString("placeholder_uuid");
            if (placeholder != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(slate.getPlugin(), "skull_placeholder_uuid");
                container.set(key, PersistentDataType.STRING, placeholder);
                item.setItemMeta(meta);
            }
        }
    }

    private void parseAmount(ItemStack item, ConfigurationSection section) {
        if (section.contains("amount")) {
            int amount = section.getInt("amount", 1);
            item.setAmount(amount);
        }
    }

}
