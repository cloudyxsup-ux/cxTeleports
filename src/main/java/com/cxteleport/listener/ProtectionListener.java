package com.cxteleport.listener;

import com.cxteleport.CXTeleport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ProtectionListener implements Listener {

    private final CXTeleport plugin;

    public ProtectionListener(CXTeleport plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getJailManager().isJailed(player.getUniqueId())) return;

        String command = event.getMessage().substring(1).split(" ")[0];
        if (plugin.getJailManager().isCommandBlocked(command)) {
            event.setCancelled(true);
            String msg = plugin.getConfig().getString("jail.blocked-message", "&cVoce esta preso e nao pode usar este comando!");
            player.sendMessage(com.cxteleport.util.MessageUtil.color(msg));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (plugin.getJailManager().isJailed(player.getUniqueId())
                && plugin.getConfig().getBoolean("jail.block-teleport", true)) {
            if (event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN
                    && event.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND) {
                return;
            }
            event.setCancelled(true);
        }
    }
}
