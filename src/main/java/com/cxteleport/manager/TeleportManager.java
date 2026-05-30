package com.cxteleport.manager;

import com.cxteleport.CXTeleport;
import com.cxteleport.model.TeleportEntry;
import com.cxteleport.util.MessageUtil;
import com.cxteleport.util.SafeLocationUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportManager {

    private final CXTeleport plugin;
    private final Map<UUID, TeleportWarmup> warmups = new ConcurrentHashMap<>();
    private final List<TeleportEntry> log = Collections.synchronizedList(new LinkedList<>());
    private long totalTeleports = 0;

    public TeleportManager(CXTeleport plugin) {
        this.plugin = plugin;
    }

    public void teleport(Player player, Location destination, String type) {
        if (canTeleport(player) != TeleportResult.SUCCESS) return;

        plugin.getPlayerDataManager().setLastLocation(player);

        int warmup = player.hasPermission("cxteleport.bypass.warmup") ? 0 : plugin.getConfigUtil().getWarmup();

        if (warmup > 0) {
            startWarmup(player, destination, type, warmup);
        } else {
            executeTeleport(player, destination, type);
        }
    }

    public void teleportInstant(Player player, Location destination, String type) {
        plugin.getPlayerDataManager().setLastLocation(player);
        executeTeleport(player, destination, type);
    }

    private void startWarmup(Player player, Location destination, String type, int seconds) {
        TeleportWarmup existing = warmups.get(player.getUniqueId());
        if (existing != null) {
            existing.cancel();
        }

        plugin.getMessageUtil().send(player, "general.teleport-warmup",
                MessageUtil.ph("seconds", String.valueOf(seconds)));

        TeleportWarmup warmup = new TeleportWarmup(player, destination, type, seconds);
        warmups.put(player.getUniqueId(), warmup);
        warmup.start();
    }

    private void executeTeleport(Player player, Location destination, String type) {
        if (plugin.getConfigUtil().isCheckSafeLocation()) {
            Location safe = SafeLocationUtil.findSafeLocation(destination, plugin.getConfigUtil().getSafeLocationRange());
            if (safe == null) {
                plugin.getMessageUtil().send(player, "general.teleport-unsafe");
                return;
            }
            destination = safe;
        }

        Location finalDest = destination;
        plugin.getScheduler().teleport(player, finalDest);

        playEffects(player);
        applyProtection(player);

        totalTeleports++;
        log.add(new TeleportEntry(
                player.getUniqueId(), player.getName(),
                destination.getWorld().getName(), type,
                player.getLocation(), destination
        ));

        if (log.size() > 10000) {
            log.subList(0, 1000).clear();
        }

        plugin.getMessageUtil().send(player, "general.teleport-success");
    }

    public void cancelWarmup(UUID uuid) {
        TeleportWarmup warmup = warmups.remove(uuid);
        if (warmup != null) warmup.cancel();
    }

    public boolean hasWarmup(UUID uuid) {
        return warmups.containsKey(uuid);
    }

    public TeleportResult canTeleport(Player player) {
        if (plugin.getJailManager().isJailed(player.getUniqueId())) {
            return TeleportResult.JAILED;
        }

        if (plugin.getConfigUtil().isPvPProtectionEnabled()) {
            int combatCooldown = plugin.getConfigUtil().getCombatCooldown();
            if (plugin.getPlayerDataManager().get(player).isInCombat(combatCooldown) &&
                    !player.hasPermission("cxteleport.bypass.combat")) {
                return TeleportResult.IN_COMBAT;
            }
        }

        return TeleportResult.SUCCESS;
    }

    public void sendCannotTeleportMessage(Player player, TeleportResult result) {
        switch (result) {
            case JAILED -> plugin.getMessageUtil().send(player, "general.jail-blocked");
            case IN_COMBAT -> {
                int remaining = plugin.getConfigUtil().getCombatCooldown() -
                        (int) ((System.currentTimeMillis() - plugin.getPlayerDataManager().get(player).getLastCombatTime()) / 1000);
                plugin.getMessageUtil().send(player, "general.combat-blocked",
                        MessageUtil.ph("seconds", String.valueOf(Math.max(0, remaining))));
            }
        }
    }

    private void playEffects(Player player) {
        try {
            String soundName = plugin.getConfig().getString("teleport.sound", "ENTITY_ENDERMAN_TELEPORT");
            float volume = (float) plugin.getConfig().getDouble("teleport.sound-volume", 1.0);
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, volume, 1.0f);
        } catch (IllegalArgumentException ignored) {}

        try {
            String particleName = plugin.getConfig().getString("teleport.particles", "PORTAL");
            int count = plugin.getConfig().getInt("teleport.particle-count", 50);
            Particle particle = Particle.valueOf(particleName);
            player.getWorld().spawnParticle(particle, player.getLocation(), count, 0.5, 0.5, 0.5, 0.1);
        } catch (IllegalArgumentException ignored) {}

        if (plugin.getConfig().getBoolean("teleport.nausea-effect", true)) {
            int duration = plugin.getConfig().getInt("teleport.nausea-duration", 60);
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.CONFUSION, duration, 0, false, false));
        }
    }

    private void applyProtection(Player player) {
        int duration = plugin.getConfigUtil().getInvulnerability();
        if (duration <= 0) return;

        plugin.getPlayerDataManager().get(player).setPvpProtectionEnd(
                System.currentTimeMillis() + duration * 1000L);

        plugin.getMessageUtil().send(player, "pvp.protection-start",
                MessageUtil.ph("seconds", String.valueOf(duration)));

        plugin.getScheduler().runTaskLaterForEntity(player, () -> {
            if (player.isOnline()) {
                plugin.getPlayerDataManager().get(player).setPvpProtectionEnd(0);
                plugin.getMessageUtil().send(player, "pvp.protection-end");
            }
        }, duration * 20L);
    }

    public List<TeleportEntry> getLog() {
        return log;
    }

    public long getTotalTeleports() {
        return totalTeleports;
    }

    private class TeleportWarmup {
        private final Player player;
        private final Location destination;
        private final String type;
        private int remaining;
        private boolean cancelled = false;
        private final Location startLocation;

        TeleportWarmup(Player player, Location destination, String type, int seconds) {
            this.player = player;
            this.destination = destination;
            this.type = type;
            this.remaining = seconds;
            this.startLocation = player.getLocation().clone();
        }

        void start() {
            runTick();
        }

        private void runTick() {
            if (cancelled || !player.isOnline()) {
                warmups.remove(player.getUniqueId());
                return;
            }

            if (remaining <= 0) {
                warmups.remove(player.getUniqueId());
                executeTeleport(player, destination, type);
                return;
            }

            if (plugin.getConfigUtil().isCancelOnMove()) {
                if (hasMoved(player, startLocation)) {
                    warmups.remove(player.getUniqueId());
                    plugin.getMessageUtil().send(player, "general.teleport-cancelled-move");
                    return;
                }
            }

            remaining--;
            plugin.getScheduler().runTaskLaterForEntity(player, this::runTick, 20L);
        }

        private boolean hasMoved(Player player, Location start) {
            return player.getLocation().getX() != start.getX()
                    || player.getLocation().getY() != start.getY()
                    || player.getLocation().getZ() != start.getZ();
        }

        void cancel() {
            this.cancelled = true;
            warmups.remove(player.getUniqueId());
            if (player.isOnline()) {
                plugin.getMessageUtil().send(player, "general.teleport-cancelled");
            }
        }
    }

    public enum TeleportResult {
        SUCCESS,
        JAILED,
        IN_COMBAT
    }
}
