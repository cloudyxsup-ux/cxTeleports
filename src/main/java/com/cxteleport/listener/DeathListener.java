package com.cxteleport.listener;

import com.cxteleport.CXTeleport;
import com.cxteleport.model.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final CXTeleport plugin;

    public DeathListener(CXTeleport plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerData data = plugin.getPlayerDataManager().get(player);

        if (plugin.getConfig().getBoolean("back.death-back", true)) {
            data.setDeathLocation(player.getLocation());
            if (player.hasPermission("cxteleport.back.death")) {
                plugin.getMessageUtil().send(player, "back.death-location-saved");
            }
        }

        plugin.getPlayerDataManager().setLastLocation(player);
    }
}
