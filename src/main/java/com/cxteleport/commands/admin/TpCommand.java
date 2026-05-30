package com.cxteleport.commands.admin;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand extends BaseCommand {

    public TpCommand(CXTeleport plugin) {
        super(plugin, "tp", "Teleporta ate um jogador", "/tp <jogador>");
        this.setPermission("cxteleport.tp");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /tp <jogador>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessageUtil().send(player, "general.player-not-found");
            return true;
        }

        plugin.getPlayerDataManager().setLastLocation(player);
        plugin.getTeleportManager().teleportInstant(player, target.getLocation(), "admin-tp");
        plugin.getMessageUtil().send(player, "admin.tp-success", ph("player", target.getName()));

        return true;
    }
}
