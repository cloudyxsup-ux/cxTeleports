package com.cxteleport.listener;

import com.cxteleport.CXTeleport;
import com.cxteleport.manager.SpawnManager;
import com.cxteleport.model.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final CXTeleport plugin;

    public JoinListener(CXTeleport plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerData data = plugin.getPlayerDataManager().get(player);

        if (plugin.getConfigUtil().isSpawnFirstLogin() && !player.hasPlayedBefore()) {
            plugin.getScheduler().runTaskLaterForEntity(player, () -> {
                org.bukkit.Location spawn = plugin.getSpawnManager().getSpawn(player.getWorld().getName());
                if (spawn != null) {
                    plugin.getScheduler().teleport(player, spawn);
                    plugin.getMessageUtil().send(player, "spawn.first-login");
                }
            }, 10L);
        }
    }
}
