package com.cxteleport.manager;

import com.cxteleport.CXTeleport;
import com.cxteleport.model.WarpData;
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
import java.util.concurrent.ConcurrentHashMap;

public class WarpManager {

    private final CXTeleport plugin;
    private final Map<String, WarpData> warps = new ConcurrentHashMap<>();
    private final File dataFile;
    private final Gson gson;

    public WarpManager(CXTeleport plugin) {
        this.plugin = plugin;
        File dataDir = new File(plugin.getDataFolder(), "data");
        if (!dataDir.exists()) dataDir.mkdirs();
        this.dataFile = new File(dataDir, "warps.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void loadAll() {
        warps.clear();
        if (!dataFile.exists()) return;
        try {
            String content = new String(java.nio.file.Files.readAllBytes(dataFile.toPath()));
            Type type = new TypeToken<Map<String, WarpData>>(){}.getType();
            Map<String, WarpData> loaded = gson.fromJson(content, type);
            if (loaded != null) warps.putAll(loaded);
        } catch (Exception e) {
            plugin.getLogger().warning("Falha ao carregar warps!");
            e.printStackTrace();
        }
    }

    public void saveAll() {
        try {
            String json = gson.toJson(warps);
            java.nio.file.Files.write(dataFile.toPath(), json.getBytes());
        } catch (IOException e) {
            plugin.getLogger().warning("Falha ao salvar warps!");
            e.printStackTrace();
        }
    }

    public boolean createWarp(String name, Location location) {
        if (warps.containsKey(name.toLowerCase())) return false;
        WarpData warp = new WarpData(name.toLowerCase(), location);
        warps.put(name.toLowerCase(), warp);
        saveAll();
        return true;
    }

    public boolean deleteWarp(String name) {
        if (warps.remove(name.toLowerCase()) != null) {
            saveAll();
            return true;
        }
        return false;
    }

    public WarpData getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    public Collection<WarpData> getWarps() {
        return Collections.unmodifiableCollection(warps.values());
    }

    public int getWarpCount() {
        return warps.size();
    }

    public boolean hasWarp(String name) {
        return warps.containsKey(name.toLowerCase());
    }

    public boolean canUse(Player player, WarpData warp) {
        if (warp.getPermission() != null && !warp.getPermission().isEmpty()) {
            return player.hasPermission(warp.getPermission());
        }
        return player.hasPermission("cxteleport.warp");
    }
}
