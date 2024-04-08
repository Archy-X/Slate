package dev.aurelium.slate.item.parser;

import dev.aurelium.slate.Slate;
import dev.aurelium.slate.item.provider.KeyedItemProvider;
import dev.aurelium.slate.lore.LoreFactory;
import dev.aurelium.slate.lore.LoreLine;
import dev.aurelium.slate.util.NumberUtil;
import dev.aurelium.slate.util.Validate;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

public class ConfigurateItemParser {

    private final Slate slate;
    private final Plugin plugin;

    public ConfigurateItemParser(Slate slate) {
        this.slate = slate;
        this.plugin = slate.getPlugin();
    }

    public ItemStack parseBaseItem(ConfigurationNode config) {
        String key = config.node("key").getString();
        if (key != null) {
            ItemStack item = parseItemKey(key);
            if (item != null) {
                return item; // Returns the item if key parse was successful
            }
        }

        String materialString = config.node("material").getString();
        Validate.notNull(materialString, "Item must specify a material");

        ItemStack item = parseMaterialString(materialString);

        parseAmount(item, config);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        // Enchantments
        if (!config.node("enchantments").virtual()) {
            parseEnchantments(item, config);
        }
        // Potions
        ConfigurationNode potionDataSection = config.node("potion_data");
        if (!potionDataSection.virtual()) {
            parsePotionData(item, potionDataSection);
        }
        // Custom potion effects
        if (!config.node("custom_effects").virtual()) {
            parseCustomEffects(config, item);
        }
        // Glowing w/o enchantments visible
        if (config.node("glow").getBoolean(false)) {
            parseGlow(item);
        }
        // Custom NBT
        if (!config.node("nbt").virtual()) {
            if (config.node("nbt").isMap()) {
                ConfigurationNode nbtSection = config.node("nbt");
                item = parseNBT(item, nbtSection.childrenMap());
            } else if (config.node("nbt").getString() != null) {
                String nbtString = config.getString("nbt");
                if (nbtString != null) {
                    item = parseNBTString(item, nbtString);
                }
            }
        }
        if (!config.node("flags").virtual()) {
            parseFlags(config, item);
        }
        if (!config.node("durability").virtual()) {
            parseDurability(config, item);
        }
        ConfigurationNode skullMetaSection = config.node("skull_meta");
        if (!skullMetaSection.virtual()) {
            parseSkullMeta(item, item.getItemMeta(), skullMetaSection);
        }
        return item;
    }

    @Nullable
    private ItemStack parseItemKey(String key) {
        KeyedItemProvider provider = slate.getOptions().keyedItemProvider();
        if (provider != null) {
            return provider.getItem(key);
        }
        return null;
    }

    private void parseDurability(ConfigurationNode section, ItemStack item) {
        ItemMeta meta = getMeta(item);
        int durability = section.node("durability").getInt();
        if (meta instanceof Damageable damageable) {
            short maxDurability = item.getType().getMaxDurability();
            damageable.setDamage(Math.max(maxDurability - durability, maxDurability));
            item.setItemMeta(meta);
        }
    }

    private void parseFlags(ConfigurationNode section, ItemStack item) {
        try {
            ItemMeta meta = getMeta(item);
            List<String> flags = section.node("flags").getList(String.class, new ArrayList<>());
            for (String flagName : flags) {
                ItemFlag itemFlag = ItemFlag.valueOf(flagName.toUpperCase(Locale.ROOT));
                meta.addItemFlags(itemFlag);
            }
            item.setItemMeta(meta);
        } catch (SerializationException ignored) {

        }
    }

    private void parseGlow(ItemStack item) {
        ItemMeta meta = getMeta(item);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    @SuppressWarnings("deprecation")
    private void parseCustomEffects(ConfigurationNode section, ItemStack item) {
        PotionMeta potionMeta = (PotionMeta) getMeta(item);
        for (ConfigurationNode effectNode : section.node("custom_effects").childrenList()) {
            String effectName = effectNode.node("type").getString("SPEED");
            PotionEffectType type = PotionEffectType.getByName(effectName);
            if (type != null) {
                int duration = effectNode.node("duration").getInt();
                int amplifier = effectNode.node("amplifier").getInt();
                potionMeta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
                potionMeta.setColor(type.getColor());
            } else {
                throw new IllegalArgumentException("Invalid potion effect type " + effectName);
            }
        }
        item.setItemMeta(potionMeta);
    }

    @SuppressWarnings("deprecation")
    private void parsePotionData(ItemStack item, ConfigurationNode node) {
        PotionMeta potionMeta = (PotionMeta) getMeta(item);
        PotionType potionType = PotionType.valueOf(node.node("type").getString("WATER").toUpperCase(Locale.ROOT));
        boolean extended = node.node("extended").getBoolean(false);
        boolean upgraded = node.node("upgraded").getBoolean(false);

        PotionData potionData = new PotionData(potionType, extended, upgraded);
        potionMeta.setBasePotionData(potionData);
        item.setItemMeta(potionMeta);
    }

    private void parseEnchantments(ItemStack item, ConfigurationNode section) {
        try {
            ItemMeta meta = getMeta(item);
            List<String> enchantmentStrings = section.node("enchantments").getList(String.class, new ArrayList<>());
            for (String enchantmentEntry : enchantmentStrings) {
                String[] splitEntry = enchantmentEntry.split(" ");
                String enchantmentName = splitEntry[0];
                int level = 1;
                if (splitEntry.length > 1) {
                    level = NumberUtil.toInt(splitEntry[1], 1);
                }
                Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantmentName.toLowerCase(Locale.ROOT)));
                if (enchantment != null) {
                    if (item.getType() == Material.ENCHANTED_BOOK && meta instanceof EnchantmentStorageMeta esm) {
                        esm.addStoredEnchant(enchantment, level, true);
                        item.setItemMeta(esm);
                    } else {
                        meta.addEnchant(enchantment, level, true);
                        item.setItemMeta(meta);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid enchantment name " + enchantmentName);
                }
            }
        } catch (SerializationException ignored) {

        }
    }

    private ItemStack parseMaterialString(String materialString) {
        ItemStack item;
        String materialName = materialString.toUpperCase(Locale.ROOT);
        Material material = parseMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("Unknown material " + materialString);
        }
        if (!material.isItem()) { // Return fallback item if material isn't an item
            return new ItemStack(Material.GRAY_DYE);
        }
        item = new ItemStack(material);
        return item;
    }

    private @NotNull ItemMeta getMeta(ItemStack item) {
        return Objects.requireNonNull(item.getItemMeta());
    }

    @Nullable
    public String parseDisplayName(ConfigurationNode section) {
        if (!section.node("display_name").virtual()) {
            return section.node("display_name").getString();
        }
        return null;
    }

    @NotNull
    public List<LoreLine> parseLore(ConfigurationNode section) {
        ConfigurationNode loreNode = section.node("lore");
        return new LoreFactory(slate).getLore(loreNode);
    }

    private ItemStack parseNBT(ItemStack item, Map<Object, ? extends ConfigurationNode> map) {
        NBTItem nbtItem = new NBTItem(item);
        applyMapToNBT(nbtItem, map);
        return nbtItem.getItem();
    }

    private void applyMapToNBT(NBTCompound item, Map<Object, ? extends ConfigurationNode> map) {
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue().raw();
            if (key instanceof String) {
                if (value instanceof ConfigurationNode childNode) {
                    // Recursively apply sub maps
                    applyMapToNBT(item.getOrCreateCompound((String) key), childNode.childrenMap());
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
        return Material.getMaterial(name);
    }

    private void parseSkullMeta(ItemStack item, ItemMeta meta, ConfigurationNode section) {
        if (!(meta instanceof SkullMeta skullMeta)) {
            return;
        }
        String uuid = section.node("uuid").getString();
        if (uuid != null) { // From UUID of player
            UUID id = UUID.fromString(uuid);
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
            item.setItemMeta(meta);
        }
        String base64 = section.node("base64").getString();
        if (base64 != null) { // From base64 string
            SkullCreator.itemWithBase64(item, base64);
        }
        String url = section.node("url").getString();
        if (url != null) { // From Mojang URL
            SkullCreator.itemWithUrl(item, url);
        }
        String placeholder = section.node("placeholder_uuid").getString();
        if (placeholder != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "skull_placeholder_uuid");
            container.set(key, PersistentDataType.STRING, placeholder);
            item.setItemMeta(meta);
        }
    }

    private void parseAmount(ItemStack item, ConfigurationNode section) {
        int amount = section.node("amount").getInt(1);
        item.setAmount(amount);
    }

}
