package com.cxteleport.manager;

import com.cxteleport.CXTeleport;
import com.cxteleport.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final CXTeleport plugin;
    private final Map<String, Map<UUID, Long>> cooldowns = new ConcurrentHashMap<>();

    public CooldownManager(CXTeleport plugin) {
        this.plugin = plugin;
    }

    public void setCooldown(String type, UUID uuid, int seconds) {
        cooldowns.computeIfAbsent(type, k -> new ConcurrentHashMap<>());
        cooldowns.get(type).put(uuid, System.currentTimeMillis() + seconds * 1000L);
    }

    public boolean hasCooldown(String type, UUID uuid) {
        Map<UUID, Long> map = cooldowns.get(type);
        if (map == null) return false;
        Long end = map.get(uuid);
        if (end == null) return false;
        if (System.currentTimeMillis() >= end) {
            map.remove(uuid);
            return false;
        }
        return true;
    }

    public int getRemainingSeconds(String type, UUID uuid) {
        Map<UUID, Long> map = cooldowns.get(type);
        if (map == null) return 0;
        Long end = map.get(uuid);
        if (end == null) return 0;
        long remaining = (end - System.currentTimeMillis()) / 1000;
        if (remaining <= 0) {
            map.remove(uuid);
            return 0;
        }
        return (int) remaining;
    }

    public void removeCooldown(String type, UUID uuid) {
        Map<UUID, Long> map = cooldowns.get(type);
        if (map != null) map.remove(uuid);
    }

    public boolean checkAndApply(Player player, String type, int seconds, String messagePath) {
        if (player.hasPermission("cxteleport.bypass.cooldown")) return false;
        if (hasCooldown(type, player.getUniqueId())) {
            int remaining = getRemainingSeconds(type, player.getUniqueId());
            plugin.getMessageUtil().send(player, messagePath,
                    MessageUtil.ph("cooldown", String.valueOf(remaining), "seconds", String.valueOf(remaining)));
            return true;
        }
        setCooldown(type, player.getUniqueId(), seconds);
        return false;
    }

    public void cleanup() {
        long now = System.currentTimeMillis();
        for (Map<UUID, Long> map : cooldowns.values()) {
            map.entrySet().removeIf(entry -> now >= entry.getValue());
        }
    }
}
