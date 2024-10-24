package dev.aurelium.slate.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperScheduler extends Scheduler {
    public PaperScheduler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void run(Player player, Runnable runnable) {
        player.getScheduler().run(plugin, (t) -> runnable.run(), null);
    }

    @Override
    public void runGlobal(Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().run(plugin, (t) -> runnable.run());
    }

    @Override
    public void runLater(Player player, Runnable runnable, long delay) {
        player.getScheduler().runDelayed(plugin, (t) -> runnable.run(), null, delay);
    }

    @Override
    public void runTimer(Player player, Runnable runnable, long delay, long period) {
        player.getScheduler().runAtFixedRate(plugin, (t) -> runnable.run(), null, delay, period);
    }
}
