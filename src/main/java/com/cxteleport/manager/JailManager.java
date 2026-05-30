package com.cxteleport.manager;

import com.cxteleport.CXTeleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JailManager {

    private final CXTeleport plugin;
    private final Set<UUID> jailedPlayers = ConcurrentHashMap.newKeySet();
    private Location jailLocation;
    private final File jailFile;

    public JailManager(CXTeleport plugin) {
        this.plugin = plugin;
        File dataDir = new File(plugin.getDataFolder(), "data");
        if (!dataDir.exists()) dataDir.mkdirs();
        this.jailFile = new File(dataDir, "jail.yml");
    }

    public void loadAll() {
        jailedPlayers.clear();
        jailLocation = null;
        if (!jailFile.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(jailFile);

        if (config.contains("location.world")) {
            String world = config.getString("location.world");
            if (world != null && Bukkit.getWorld(world) != null) {
                jailLocation = new Location(
                        Bukkit.getWorld(world),
                        config.getDouble("location.x"),
                        config.getDouble("location.y"),
                        config.getDouble("location.z"),
                        (float) config.getDouble("location.yaw", 0),
                        (float) config.getDouble("location.pitch", 0)
                );
            }
        }

        if (config.contains("jailed")) {
            for (String key : config.getStringList("jailed")) {
                try {
                    jailedPlayers.add(UUID.fromString(key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void saveAll() {
        YamlConfiguration config = new YamlConfiguration();

        if (jailLocation != null) {
            config.set("location.world", jailLocation.getWorld().getName());
            config.set("location.x", jailLocation.getX());
            config.set("location.y", jailLocation.getY());
            config.set("location.z", jailLocation.getZ());
            config.set("location.yaw", jailLocation.getYaw());
            config.set("location.pitch", jailLocation.getPitch());
        }

        config.set("jailed", jailedPlayers.stream().map(UUID::toString).toList());

        try {
            config.save(jailFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Falha ao salvar dados de prisao!");
            e.printStackTrace();
        }
    }

    public boolean jail(UUID uuid) {
        if (jailedPlayers.contains(uuid)) return false;
        jailedPlayers.add(uuid);
        plugin.getPlayerDataManager().get(uuid).setJailed(true);

        if (jailLocation != null) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                plugin.getScheduler().teleport(player, jailLocation);
            }
        }

        saveAll();
        return true;
    }

    public boolean unjail(UUID uuid) {
        if (!jailedPlayers.contains(uuid)) return false;
        jailedPlayers.remove(uuid);
        plugin.getPlayerDataManager().get(uuid).setJailed(false);
        saveAll();
        return true;
    }

    public boolean isJailed(UUID uuid) {
        return jailedPlayers.contains(uuid);
    }

    public Location getJailLocation() {
        return jailLocation;
    }

    public void setJailLocation(Location location) {
        this.jailLocation = location;
        saveAll();
    }

    public boolean hasJailLocation() {
        return jailLocation != null;
    }

    public boolean isCommandBlocked(String command) {
        if (!plugin.getConfig().getBoolean("jail.block-commands", true)) return false;
        for (String blocked : plugin.getConfig().getStringList("jail.blocked-commands")) {
            if (command.equalsIgnoreCase(blocked) || command.toLowerCase().startsWith(blocked.toLowerCase() + " ")) {
                return true;
            }
        }
        return false;
    }

    public int getJailedCount() {
        return jailedPlayers.size();
    }
}
