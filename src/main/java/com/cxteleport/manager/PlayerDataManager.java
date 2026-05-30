package com.cxteleport.manager;

import com.cxteleport.CXTeleport;
import com.cxteleport.model.PlayerData;
import com.cxteleport.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private final CXTeleport plugin;
    private final Map<UUID, PlayerData> dataMap = new ConcurrentHashMap<>();

    public PlayerDataManager(CXTeleport plugin) {
        this.plugin = plugin;
    }

    public PlayerData get(Player player) {
        return get(player.getUniqueId());
    }

    public PlayerData get(UUID uuid) {
        return dataMap.computeIfAbsent(uuid, PlayerData::new);
    }

    public void loadAll() {
        // Data loaded on-demand via get()
    }

    public void saveAll() {
        // Persist to disk if needed in future
    }

    public void remove(UUID uuid) {
        dataMap.remove(uuid);
    }

    public boolean isJailed(UUID uuid) {
        PlayerData data = dataMap.get(uuid);
        return data != null && data.isJailed();
    }

    public boolean isTpaEnabled(UUID uuid) {
        PlayerData data = dataMap.get(uuid);
        return data == null || data.isTpaEnabled();
    }

    public boolean isTpaHereEnabled(UUID uuid) {
        PlayerData data = dataMap.get(uuid);
        return data == null || data.isTpaHereEnabled();
    }

    public boolean isAutoAccept(UUID uuid) {
        PlayerData data = dataMap.get(uuid);
        return data != null && data.isAutoAccept();
    }

    public void setLastLocation(Player player) {
        get(player).setLastLocation(player.getLocation());
    }
}
