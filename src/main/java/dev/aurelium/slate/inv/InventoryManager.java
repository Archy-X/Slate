/*
 * Copyright 2018-2020 Isaac Montagne
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package dev.aurelium.slate.inv;

import dev.aurelium.slate.inv.content.InventoryContents;
import dev.aurelium.slate.inv.content.InventoryProvider;
import dev.aurelium.slate.inv.content.SlotPos;
import dev.aurelium.slate.inv.opener.ChestInventoryOpener;
import dev.aurelium.slate.inv.opener.InventoryOpener;
import dev.aurelium.slate.inv.opener.SpecialInventoryOpener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

public class InventoryManager {

    private final JavaPlugin plugin;
    private final PluginManager pluginManager;

    private final Map<UUID, SmartInventory> inventories;
    private final Map<UUID, InventoryContents> contents;
    private final Map<UUID, BukkitRunnable> updateTasks;

    private final List<InventoryOpener> defaultOpeners;
    private final List<InventoryOpener> openers;

    public InventoryManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pluginManager = Bukkit.getPluginManager();

        this.inventories = new HashMap<>();
        this.contents = new HashMap<>();
        this.updateTasks = new HashMap<>();

        this.defaultOpeners = Arrays.asList(
                new ChestInventoryOpener(),
                new SpecialInventoryOpener()
        );

        this.openers = new ArrayList<>();
    }

    public void init() {
        pluginManager.registerEvents(new InvListener(), plugin);

//        new InvTask().runTaskTimer(plugin, 1, 1);
    }

    public Optional<InventoryOpener> findOpener(InventoryType type) {
        Optional<InventoryOpener> opInv = this.openers.stream()
                .filter(opener -> opener.supports(type))
                .findAny();

        if (!opInv.isPresent()) {
            opInv = this.defaultOpeners.stream()
                    .filter(opener -> opener.supports(type))
                    .findAny();
        }

        return opInv;
    }

    public void registerOpeners(InventoryOpener... openers) {
        this.openers.addAll(Arrays.asList(openers));
    }

    public List<Player> getOpenedPlayers(SmartInventory inv) {
        List<Player> list = new ArrayList<>();

        this.inventories.forEach((player, playerInv) -> {
            if (inv.equals(playerInv))
                list.add(Bukkit.getPlayer(player));
        });

        return list;
    }

    public Optional<SmartInventory> getInventory(Player p) {
        return Optional.ofNullable(this.inventories.get(p.getUniqueId()));
    }

    protected void setInventory(Player p, SmartInventory inv) {
        if (inv == null)
            this.inventories.remove(p.getUniqueId());
        else
            this.inventories.put(p.getUniqueId(), inv);
    }

    public Optional<InventoryContents> getContents(Player p) {
        return Optional.ofNullable(this.contents.get(p.getUniqueId()));
    }

    protected void setContents(Player p, InventoryContents contents) {
        if (contents == null)
            this.contents.remove(p.getUniqueId());
        else
            this.contents.put(p.getUniqueId(), contents);
    }

    public void handleInventoryOpenError(SmartInventory inventory, Player player, Exception exception) {
        inventory.close(player);

        Bukkit.getLogger().log(Level.SEVERE, "Error while opening SmartInventory:", exception);
    }

    public void handleInventoryUpdateError(SmartInventory inventory, Player player, Exception exception) {
        inventory.close(player);

        Bukkit.getLogger().log(Level.SEVERE, "Error while updating SmartInventory:", exception);
    }

    protected void scheduleUpdateTask(Player p, SmartInventory inv) {
    	PlayerInvTask task = new PlayerInvTask(p, inv.getProvider(), contents.get(p.getUniqueId()));
    	task.runTaskTimer(plugin, 1, inv.getUpdateFrequency());
    	this.updateTasks.put(p.getUniqueId(), task);
    }

    protected void cancelUpdateTask(Player p) {
    	if(updateTasks.containsKey(p.getUniqueId())) {
          int bukkitTaskId = this.updateTasks.get(p.getUniqueId()).getTaskId();
          Bukkit.getScheduler().cancelTask(bukkitTaskId);
          this.updateTasks.remove(p.getUniqueId());
    	}
    }

    @SuppressWarnings("unchecked")
    class InvListener implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInventoryClick(InventoryClickEvent e) {
            Player p = (Player) e.getWhoClicked();
            SmartInventory inv = inventories.get(p.getUniqueId());

            if(inv == null)
                return;

            // Restrict putting items from the bottom inventory into the top inventory
            Inventory clickedInventory = e.getClickedInventory();
            if (clickedInventory == p.getOpenInventory().getBottomInventory()) {
                if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    e.setCancelled(true);
                    return;
                }

                if (e.getAction() == InventoryAction.NOTHING && e.getClick() != ClickType.MIDDLE) {
                    e.setCancelled(true);
                    return;
                }
            }

            if (clickedInventory == p.getOpenInventory().getTopInventory()) {
                e.setCancelled(true);

                int row = e.getSlot() / 9;
                int column = e.getSlot() % 9;

                if(!inv.checkBounds(row, column))
                    return;

                InventoryContents invContents = contents.get(p.getUniqueId());
                SlotPos slot = SlotPos.of(row, column);

                if(!invContents.isEditable(slot))
                    e.setCancelled(true);

                inv.getListeners().stream()
                        .filter(listener -> listener.getType() == InventoryClickEvent.class)
                        .forEach(listener -> ((InventoryListener<InventoryClickEvent>) listener).accept(e));

                invContents.get(slot).ifPresent(item -> item.run(new ItemClickData(e, p, e.getCurrentItem(), slot)));

                // Don't update if the clicked slot is editable - prevent item glitching
                if(!invContents.isEditable(slot)) {
                    p.updateInventory();
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInventoryDrag(InventoryDragEvent e) {
            Player p = (Player) e.getWhoClicked();

            if (!inventories.containsKey(p.getUniqueId()))
                return;

            SmartInventory inv = inventories.get(p.getUniqueId());
            InventoryContents content = contents.get(p.getUniqueId());

            for (int slot : e.getRawSlots()) {
                SlotPos pos = SlotPos.of(slot/9, slot%9);
                if (slot >= p.getOpenInventory().getTopInventory().getSize() || content.isEditable(pos))
                    continue;

                e.setCancelled(true);
                break;
            }

            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == InventoryDragEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryDragEvent>) listener).accept(e));
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryOpen(InventoryOpenEvent e) {
            Player p = (Player) e.getPlayer();

            if (!inventories.containsKey(p.getUniqueId()))
                return;

            SmartInventory inv = inventories.get(p.getUniqueId());

            inv.getListeners().stream()
                    .filter(listener -> listener.getType() == InventoryOpenEvent.class)
                    .forEach(listener -> ((InventoryListener<InventoryOpenEvent>) listener).accept(e));
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onInventoryClose(InventoryCloseEvent e) {
            Player p = (Player) e.getPlayer();

            if (!inventories.containsKey(p.getUniqueId()))
                return;

            SmartInventory inv = inventories.get(p.getUniqueId());

            try{
                inv.getListeners().stream()
                        .filter(listener -> listener.getType() == InventoryCloseEvent.class)
                        .forEach(listener -> ((InventoryListener<InventoryCloseEvent>) listener).accept(e));
            } finally {
                if (inv.isCloseable()) {
                    e.getInventory().clear();
                    InventoryManager.this.cancelUpdateTask(p);

                    inventories.remove(p.getUniqueId());
                    contents.remove(p.getUniqueId());
                }
                else
                    Bukkit.getScheduler().runTask(plugin, () -> p.openInventory(e.getInventory()));
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPlayerQuit(PlayerQuitEvent e) {
            Player p = e.getPlayer();

            if (!inventories.containsKey(p.getUniqueId()))
                return;

            SmartInventory inv = inventories.get(p.getUniqueId());

            try{
                inv.getListeners().stream()
                        .filter(listener -> listener.getType() == PlayerQuitEvent.class)
                        .forEach(listener -> ((InventoryListener<PlayerQuitEvent>) listener).accept(e));
            } finally {
                inventories.remove(p.getUniqueId());
                contents.remove(p.getUniqueId());
                p.updateInventory();
            }
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onPluginDisable(PluginDisableEvent e) {
            new HashMap<>(inventories).forEach((player, inv) -> {
                try{
                    inv.getListeners().stream()
                            .filter(listener -> listener.getType() == PluginDisableEvent.class)
                            .forEach(listener -> ((InventoryListener<PluginDisableEvent>) listener).accept(e));
                } finally {
                    inv.close(Bukkit.getPlayer(player));
                }
            });

            inventories.clear();
            contents.clear();
        }

    }

    class InvTask extends BukkitRunnable {

        @Override
        public void run() {
            new HashMap<>(inventories).forEach((uuid, inv) -> {
                Player player = Bukkit.getPlayer(uuid);

                try {
                    inv.getProvider().update(player, contents.get(uuid));
                } catch (Exception e) {
                    handleInventoryUpdateError(inv, player, e);
                }
            });
        }

    }

    class PlayerInvTask extends BukkitRunnable {

        private Player player;
        private InventoryProvider provider;
        private InventoryContents contents;

        public PlayerInvTask(Player player, InventoryProvider provider, InventoryContents contents) {
          this.player = Objects.requireNonNull(player);
          this.provider = Objects.requireNonNull(provider);
          this.contents = Objects.requireNonNull(contents);
        }

        @Override
        public void run() {
            provider.update(this.player, this.contents);
        }

    }

}
