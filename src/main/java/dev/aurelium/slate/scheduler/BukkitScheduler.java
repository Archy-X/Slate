package dev.aurelium.slate.scheduler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitScheduler extends Scheduler {
    public BukkitScheduler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void run(Player player, Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    @Override
    public void runGlobal(Runnable runnable) {
        run(null, runnable);
    }

    @Override
    public void runLater(Player player, Runnable runnable, long delay) {
        plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    @Override
    public void runTimer(Player player, Runnable runnable, long delay, long period) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }
}
