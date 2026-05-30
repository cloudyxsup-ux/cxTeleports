package com.cxteleport.commands.admin;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpHereCommand extends BaseCommand {

    public TpHereCommand(CXTeleport plugin) {
        super(plugin, "tphere", "Puxa um jogador ate voce", "/tphere <jogador>");
        this.setPermission("cxteleport.tphere");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /tphere <jogador>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessageUtil().send(player, "general.player-not-found");
            return true;
        }

        plugin.getPlayerDataManager().setLastLocation(target);
        plugin.getTeleportManager().teleportInstant(target, player.getLocation(), "admin-tphere");
        plugin.getMessageUtil().send(player, "admin.tphere-success", ph("player", target.getName()));

        return true;
    }
}
