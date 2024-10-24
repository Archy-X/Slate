package dev.aurelium.slate.scheduler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Scheduler {
    protected final JavaPlugin plugin;

    public Scheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void run(Player player, Runnable runnable);
    public abstract void runGlobal(Runnable runnable);

    public abstract void runLater(Player player, Runnable runnable, long delay);

    public abstract void runTimer(Player player, Runnable runnable, long delay, long period);

    public static Scheduler createScheduler(JavaPlugin plugin) {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            return new PaperScheduler(plugin);
        } catch (Exception e) {
            return new BukkitScheduler(plugin);
        }
    }
}
