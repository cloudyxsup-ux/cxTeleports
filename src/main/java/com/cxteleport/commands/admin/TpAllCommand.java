package com.cxteleport.commands.admin;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAllCommand extends BaseCommand {

    public TpAllCommand(CXTeleport plugin) {
        super(plugin, "tptodos", "Teleporta todos os jogadores", "/tptodos");
        this.setAliases(java.util.List.of("tpall"));
        this.setPermission("cxteleport.tpall");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        org.bukkit.Location loc = player.getLocation();
        int count = 0;

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getUniqueId().equals(player.getUniqueId())) continue;
            if (plugin.getJailManager().isJailed(online.getUniqueId())) continue;
            plugin.getTeleportManager().teleportInstant(online, loc, "admin-tpall");
            count++;
        }

        plugin.getMessageUtil().send(player, "admin.tpall-success");

        return true;
    }
}
