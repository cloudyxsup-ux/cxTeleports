package com.cxteleport.commands.rtp;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.manager.RTPManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTPCommand extends BaseCommand {

    public RTPCommand(CXTeleport plugin) {
        super(plugin, "rtp", "Teleporte aleatorio", "/rtp [mundo]");
        this.setPermission("cxteleport.rtp");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (plugin.getConfig().getBoolean("rtp.gui.enabled", true) && args.length == 0) {
            new com.cxteleport.gui.RTPGUI(plugin).open(player);
            return true;
        }

        if (plugin.getCooldownManager().checkAndApply(player, "rtp", plugin.getConfigUtil().getRTPCooldown(), "general.cooldown")) {
            return true;
        }

        if (!checkCombat(player)) return true;

        String worldName = args.length >= 1 ? args[0] : player.getWorld().getName();

        if (!plugin.getRtpManager().isWorldAllowed(worldName)) {
            plugin.getMessageUtil().send(player, "rtp.world-not-allowed");
            return true;
        }

        plugin.getMessageUtil().send(player, "rtp.searching");

        plugin.getScheduler().runAsync(() -> {
            RTPManager.RTPResult result = plugin.getRtpManager().performRTP(player, worldName);

            plugin.getScheduler().runTaskForEntity(player, () -> {
                switch (result) {
                    case SUCCESS -> {
                        org.bukkit.Location loc = player.getLocation();
                        plugin.getMessageUtil().send(player, "rtp.teleported",
                                ph("world", loc.getWorld().getName(),
                                        "x", String.valueOf(loc.getBlockX()),
                                        "y", String.valueOf(loc.getBlockY()),
                                        "z", String.valueOf(loc.getBlockZ())));
                    }
                    case WORLD_NOT_ALLOWED -> plugin.getMessageUtil().send(player, "rtp.world-not-allowed");
                    case NO_SAFE_LOCATION -> plugin.getMessageUtil().send(player, "rtp.not-found",
                            ph("count", String.valueOf(plugin.getConfigUtil().getRTPMaxAttempts())));
                }
            });
        });

        return true;
    }
}
