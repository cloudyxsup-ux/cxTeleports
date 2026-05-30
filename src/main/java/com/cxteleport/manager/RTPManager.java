package com.cxteleport.manager;

import com.cxteleport.CXTeleport;
import com.cxteleport.model.RTPZone;
import com.cxteleport.util.SafeLocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RTPManager {

    private final CXTeleport plugin;
    private final Random random = new Random();
    private final Queue<UUID> queue = new LinkedList<>();
    private final Map<UUID, Long> rtpCooldowns = new ConcurrentHashMap<>();
    private final List<RTPZone> zones = new ArrayList<>();
    private long totalRTPs = 0;
    private boolean queueProcessorRunning = false;

    public RTPManager(CXTeleport plugin) {
        this.plugin = plugin;
        loadZones();
    }

    private void loadZones() {
        zones.clear();
        if (!plugin.getConfig().getBoolean("rtp-zones.enabled", false)) return;

        if (plugin.getConfig().contains("rtp-zones.zones")) {
            for (String key : plugin.getConfig().getConfigurationSection("rtp-zones.zones").getKeys(false)) {
                String base = "rtp-zones.zones." + key;
                RTPZone zone = new RTPZone(
                        key,
                        plugin.getConfig().getString(base + ".world", "world"),
                        plugin.getConfig().getInt(base + ".min-x", -1000),
                        plugin.getConfig().getInt(base + ".max-x", 1000),
                        plugin.getConfig().getInt(base + ".min-z", -1000),
                        plugin.getConfig().getInt(base + ".max-z", 1000),
                        plugin.getConfig().getInt(base + ".countdown", 5),
                        plugin.getConfig().getString(base + ".message", "&aTeleportando...")
                );
                zones.add(zone);
            }
        }
    }

    public void startQueueProcessor() {
        if (!plugin.getConfig().getBoolean("rtp.queue.enabled", true)) return;
        queueProcessorRunning = true;
        processQueue();
    }

    private void processQueue() {
        if (!queueProcessorRunning) return;

        if (!queue.isEmpty()) {
            UUID uuid = queue.poll();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                performRTP(player, null);
            }
        }

        int delay = plugin.getConfig().getInt("rtp.queue.process-delay", 20);
        plugin.getScheduler().runTaskLater(this::processQueue, delay);
    }

    public void addToQueue(Player player) {
        int maxSize = plugin.getConfig().getInt("rtp.queue.max-size", 50);
        if (queue.size() >= maxSize) return;
        if (queue.contains(player.getUniqueId())) return;
        queue.add(player.getUniqueId());
    }

    public void removeFromQueue(UUID uuid) {
        queue.remove(uuid);
    }

    public boolean isInQueue(UUID uuid) {
        return queue.contains(uuid);
    }

    public int getQueuePosition(UUID uuid) {
        int pos = 0;
        for (UUID id : queue) {
            pos++;
            if (id.equals(uuid)) return pos;
        }
        return -1;
    }

    public int getQueueSize() {
        return queue.size();
    }

    public RTPResult performRTP(Player player, String worldName) {
        World world = worldName != null ? Bukkit.getWorld(worldName) : player.getWorld();

        if (world == null) return RTPResult.WORLD_NOT_ALLOWED;

        String worldKey = world.getName();
        if (plugin.getConfig().contains("rtp.worlds." + worldKey)) {
            if (!plugin.getConfig().getBoolean("rtp.worlds." + worldKey + ".enabled", false)) {
                return RTPResult.WORLD_NOT_ALLOWED;
            }
        }

        int minDist = plugin.getConfig().contains("rtp.worlds." + worldKey + ".min-distance")
                ? plugin.getConfig().getInt("rtp.worlds." + worldKey + ".min-distance")
                : plugin.getConfigUtil().getRTPMinDistance();
        int maxDist = plugin.getConfig().contains("rtp.worlds." + worldKey + ".max-distance")
                ? plugin.getConfig().getInt("rtp.worlds." + worldKey + ".max-distance")
                : plugin.getConfigUtil().getRTPMaxDistance();
        int maxAttempts = plugin.getConfigUtil().getRTPMaxAttempts();

        List<String> biomeBlacklist = plugin.getConfig().getStringList("rtp.biomes-blacklist");
        List<String> blockBlacklist = plugin.getConfig().getStringList("rtp.blocks-blacklist");

        Location spawnLoc = world.getSpawnLocation();

        for (int i = 0; i < maxAttempts; i++) {
            int angle = random.nextInt(360);
            int distance = minDist + random.nextInt(maxDist - minDist);
            int x = spawnLoc.getBlockX() + (int) (distance * Math.cos(Math.toRadians(angle)));
            int z = spawnLoc.getBlockZ() + (int) (distance * Math.sin(Math.toRadians(angle)));

            if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                world.loadChunk(x >> 4, z >> 4);
            }

            int y = world.getHighestBlockYAt(x, z);
            Location loc = new Location(world, x + 0.5, y, z + 0.5);

            if (SafeLocationUtil.isSafe(loc, blockBlacklist, biomeBlacklist)) {
                plugin.getPlayerDataManager().setLastLocation(player);
                plugin.getScheduler().teleport(player, loc);

                playRTPEffects(player);
                totalRTPs++;
                return RTPResult.SUCCESS;
            }
        }

        return RTPResult.NO_SAFE_LOCATION;
    }

    public RTPZone getZone(String name) {
        for (RTPZone zone : zones) {
            if (zone.getName().equalsIgnoreCase(name)) return zone;
        }
        return null;
    }

    public List<RTPZone> getZones() {
        return zones;
    }

    public boolean isWorldAllowed(String worldName) {
        if (!plugin.getConfig().contains("rtp.worlds." + worldName)) return true;
        return plugin.getConfig().getBoolean("rtp.worlds." + worldName + ".enabled", false);
    }

    private void playRTPEffects(Player player) {
        try {
            String soundName = plugin.getConfig().getString("rtp.effects.sound", "ENTITY_EVOKER_CAST_SPELL");
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (Exception ignored) {}

        try {
            String particleName = plugin.getConfig().getString("rtp.effects.particles", "PORTAL");
            int count = plugin.getConfig().getInt("rtp.effects.particle-count", 100);
            Particle particle = Particle.valueOf(particleName);
            player.getWorld().spawnParticle(particle, player.getLocation(), count, 0.5, 1.0, 0.5, 0.1);
        } catch (Exception ignored) {}
    }

    public void shutdown() {
        queueProcessorRunning = false;
        queue.clear();
    }

    public long getTotalRTPs() {
        return totalRTPs;
    }

    public enum RTPResult {
        SUCCESS,
        WORLD_NOT_ALLOWED,
        NO_SAFE_LOCATION
    }
}
