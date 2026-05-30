package com.cxteleport.manager;

import com.cxteleport.CXTeleport;
import com.cxteleport.model.HomeData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {

    private final CXTeleport plugin;
    private final Map<UUID, Map<String, HomeData>> homes = new ConcurrentHashMap<>();
    private final File dataFolder;
    private final Gson gson;

    public HomeManager(CXTeleport plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "data" + File.separator + "homes");
        if (!dataFolder.exists()) dataFolder.mkdirs();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void loadAll() {
        homes.clear();
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;
        for (File file : files) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                String uuidStr = file.getName().replace(".json", "");
                UUID uuid = UUID.fromString(uuidStr);
                Type type = new TypeToken<Map<String, HomeData>>(){}.getType();
                Map<String, HomeData> playerHomes = gson.fromJson(content, type);
                if (playerHomes != null) {
                    homes.put(uuid, new ConcurrentHashMap<>(playerHomes));
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Falha ao carregar casas de: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, Map<String, HomeData>> entry : homes.entrySet()) {
            savePlayer(entry.getKey(), entry.getValue());
        }
    }

    private void savePlayer(UUID uuid, Map<String, HomeData> playerHomes) {
        try {
            File file = new File(dataFolder, uuid.toString() + ".json");
            String json = gson.toJson(playerHomes);
            java.nio.file.Files.write(file.toPath(), json.getBytes());
        } catch (IOException e) {
            plugin.getLogger().warning("Falha ao salvar casas de: " + uuid);
            e.printStackTrace();
        }
    }

    public boolean createHome(Player player, String name) {
        UUID uuid = player.getUniqueId();
        Map<String, HomeData> playerHomes = homes.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());

        if (playerHomes.containsKey(name.toLowerCase())) return false;

        int limit = getHomeLimit(player);
        if (playerHomes.size() >= limit && !player.hasPermission("cxteleport.home.unlimited")) return false;

        HomeData home = new HomeData(uuid, name.toLowerCase(), player.getLocation());
        playerHomes.put(name.toLowerCase(), home);
        savePlayer(uuid, playerHomes);
        return true;
    }

    public boolean deleteHome(UUID uuid, String name) {
        Map<String, HomeData> playerHomes = homes.get(uuid);
        if (playerHomes == null) return false;
        if (playerHomes.remove(name.toLowerCase()) != null) {
            savePlayer(uuid, playerHomes);
            return true;
        }
        return false;
    }

    public HomeData getHome(UUID uuid, String name) {
        Map<String, HomeData> playerHomes = homes.get(uuid);
        if (playerHomes == null) return null;
        return playerHomes.get(name.toLowerCase());
    }

    public Collection<HomeData> getHomes(UUID uuid) {
        Map<String, HomeData> playerHomes = homes.get(uuid);
        if (playerHomes == null) return Collections.emptyList();
        return Collections.unmodifiableCollection(playerHomes.values());
    }

    public int getHomeCount(UUID uuid) {
        Map<String, HomeData> playerHomes = homes.get(uuid);
        return playerHomes != null ? playerHomes.size() : 0;
    }

    public int getHomeLimit(Player player) {
        if (player.hasPermission("cxteleport.home.unlimited")) return Integer.MAX_VALUE;
        if (!plugin.getConfigUtil().isHomePermissionLimits()) {
            return plugin.getConfigUtil().getDefaultHomeLimit();
        }
        int max = plugin.getConfigUtil().getDefaultHomeLimit();
        for (int i = 100; i >= 1; i--) {
            if (player.hasPermission("cxteleport.home.limit." + i)) {
                max = Math.max(max, i);
                break;
            }
        }
        return max;
    }

    public boolean hasHome(UUID uuid, String name) {
        Map<String, HomeData> playerHomes = homes.get(uuid);
        return playerHomes != null && playerHomes.containsKey(name.toLowerCase());
    }

    public void save(UUID uuid) {
        Map<String, HomeData> playerHomes = homes.get(uuid);
        if (playerHomes != null) savePlayer(uuid, playerHomes);
    }

    public int getTotalHomeCount() {
        int total = 0;
        for (Map<String, HomeData> map : homes.values()) {
            total += map.size();
        }
        return total;
    }
}
