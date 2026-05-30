package com.cxteleport.listener;

import com.cxteleport.CXTeleport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    private final CXTeleport plugin;

    public MoveListener(CXTeleport plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getX() == event.getTo().getX()
                && event.getFrom().getY() == event.getTo().getY()
                && event.getFrom().getZ() == event.getTo().getZ()) {
            return;
        }

        Player player = event.getPlayer();
        if (plugin.getTeleportManager().hasWarmup(player.getUniqueId())) {
            if (plugin.getConfigUtil().isCancelOnMove()) {
                plugin.getTeleportManager().cancelWarmup(player.getUniqueId());
                plugin.getMessageUtil().send(player, "general.teleport-cancelled-move");
            }
        }
    }
}
