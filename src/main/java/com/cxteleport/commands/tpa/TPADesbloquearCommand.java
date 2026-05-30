package com.cxteleport.commands.tpa;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TPADesbloquearCommand extends BaseCommand {

    public TPADesbloquearCommand(CXTeleport plugin) {
        super(plugin, "tpdesbloquear", "Remove o bloqueio de um jogador", "/tpdesbloquear <jogador>");
        this.setAliases(java.util.List.of("tpunblock"));
        this.setPermission("cxteleport.tpblock");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /tpdesbloquear <jogador>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        UUID targetUuid;

        if (target != null) {
            targetUuid = target.getUniqueId();
        } else {
            org.bukkit.OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
            targetUuid = offline.getUniqueId();
        }

        if (!plugin.getTpaManager().isBlocked(player.getUniqueId(), targetUuid)) {
            plugin.getMessageUtil().send(player, "tpa.not-blocked", ph("player", args[0]));
            return true;
        }

        plugin.getTpaManager().unblockPlayer(player.getUniqueId(), targetUuid);
        plugin.getMessageUtil().send(player, "tpa.unblocked-player", ph("player", args[0]));

        return true;
    }
}
