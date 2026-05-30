package com.cxteleport.scheduler;

import com.cxteleport.CXTeleport;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SchedulerAdapter {

    private final CXTeleport plugin;
    private final boolean folia;

    public SchedulerAdapter(CXTeleport plugin) {
        this.plugin = plugin;
        this.folia = plugin.isFolia();
    }

    public void runTask(Runnable task) {
        if (folia) {
            Bukkit.getGlobalRegionScheduler().run(plugin, t -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public void runTaskAt(Location location, Runnable task) {
        if (folia) {
            Bukkit.getRegionScheduler().run(plugin, location, t -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public void runTaskForEntity(Entity entity, Runnable task) {
        if (folia) {
            entity.getScheduler().run(plugin, t -> task.run(), null);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public void runTaskLater(Runnable task, long delayTicks) {
        if (folia) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> task.run(), delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public void runTaskLaterForEntity(Entity entity, Runnable task, long delayTicks) {
        if (folia) {
            entity.getScheduler().runDelayed(plugin, t -> task.run(), null, delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public void runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        if (folia) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> task.run(), delayTicks, periodTicks);
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        }
    }

    public void runTaskTimerForEntity(Entity entity, Runnable task, long delayTicks, long periodTicks) {
        if (folia) {
            entity.getScheduler().runAtFixedRate(plugin, t -> task.run(), null, delayTicks, periodTicks);
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        }
    }

    public void runAsync(Runnable task) {
        if (folia) {
            Bukkit.getAsyncScheduler().runNow(plugin, t -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    public void runAsyncLater(Runnable task, long delayMs) {
        if (folia) {
            Bukkit.getAsyncScheduler().runDelayed(plugin, t -> task.run(), delayMs, TimeUnit.MILLISECONDS);
        } else {
            long ticks = Math.max(1, delayMs / 50);
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, ticks);
        }
    }

    public void runAsyncTimer(Runnable task, long delayMs, long periodMs) {
        if (folia) {
            Bukkit.getAsyncScheduler().runAtFixedRate(plugin, t -> task.run(), delayMs, periodMs, TimeUnit.MILLISECONDS);
        } else {
            long delayTicks = Math.max(1, delayMs / 50);
            long periodTicks = Math.max(1, periodMs / 50);
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
        }
    }

    public void teleport(Player player, Location location) {
        if (folia) {
            player.getScheduler().run(plugin, t -> {
                player.teleportAsync(location).thenAccept(success -> {});
            }, null);
        } else {
            plugin.getServer().getScheduler().runTask(plugin, () -> player.teleport(location));
        }
    }

    public void shutdown() {
        if (!folia) {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
    }
}
