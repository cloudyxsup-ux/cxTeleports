package com.cxteleport.manager;

import com.cxteleport.CXTeleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpawnManager {

    private final CXTeleport plugin;
    private Location globalSpawn;
    private final Map<String, Location> worldSpawns = new HashMap<>();
    private final File spawnFile;

    public SpawnManager(CXTeleport plugin) {
        this.plugin = plugin;
        File dataDir = new File(plugin.getDataFolder(), "data");
        if (!dataDir.exists()) dataDir.mkdirs();
        this.spawnFile = new File(dataDir, "spawns.yml");
    }

    public void load() {
        worldSpawns.clear();
        globalSpawn = null;
        if (!spawnFile.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(spawnFile);

        if (config.contains("global")) {
            globalSpawn = deserializeLocation(config.getConfigurationSection("global"));
        }

        if (config.contains("worlds")) {
            for (String key : config.getConfigurationSection("worlds").getKeys(false)) {
                Location loc = deserializeLocation(config.getConfigurationSection("worlds." + key));
                if (loc != null) worldSpawns.put(key, loc);
            }
        }
    }

    public void save() {
        YamlConfiguration config = new YamlConfiguration();

        if (globalSpawn != null) {
            serializeLocation(globalSpawn, config.createSection("global"));
        }

        for (Map.Entry<String, Location> entry : worldSpawns.entrySet()) {
            serializeLocation(entry.getValue(), config.createSection("worlds." + entry.getKey()));
        }

        try {
            config.save(spawnFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Falha ao salvar spawns!");
            e.printStackTrace();
        }
    }

    public void setGlobalSpawn(Location location) {
        this.globalSpawn = location;
        save();
    }

    public void setWorldSpawn(String worldName, Location location) {
        worldSpawns.put(worldName, location);
        save();
    }

    public Location getSpawn(String worldName) {
        if (plugin.getConfigUtil().isSpawnPerWorld()) {
            Location worldSpawn = worldSpawns.get(worldName);
            if (worldSpawn != null) return worldSpawn;
        }
        if (globalSpawn != null) return globalSpawn;

        World world = Bukkit.getWorld(plugin.getConfig().getString("spawn.default-world", "world"));
        if (world != null) return world.getSpawnLocation();
        return null;
    }

    public Location getGlobalSpawn() {
        return globalSpawn;
    }

    public boolean hasSpawn() {
        return globalSpawn != null || !worldSpawns.isEmpty();
    }

    private void serializeLocation(Location loc, ConfigurationSection section) {
        if (loc == null) return;
        section.set("world", loc.getWorld().getName());
        section.set("x", loc.getX());
        section.set("y", loc.getY());
        section.set("z", loc.getZ());
        section.set("yaw", loc.getYaw());
        section.set("pitch", loc.getPitch());
    }

    private Location deserializeLocation(ConfigurationSection section) {
        if (section == null) return null;
        String worldName = section.getString("world");
        if (worldName == null) return null;
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        return new Location(
                world,
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("yaw", 0),
                (float) section.getDouble("pitch", 0)
        );
    }
}
