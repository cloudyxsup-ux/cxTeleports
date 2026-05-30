package com.cxteleport.commands.tpa;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TPABloquearCommand extends BaseCommand {

    public TPABloquearCommand(CXTeleport plugin) {
        super(plugin, "tpbloquear", "Bloqueia solicitacoes de um jogador", "/tpbloquear <jogador>");
        this.setAliases(java.util.List.of("tpblock"));
        this.setPermission("cxteleport.tpblock");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /tpbloquear <jogador>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        UUID targetUuid;

        if (target != null) {
            targetUuid = target.getUniqueId();
        } else {
            org.bukkit.OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
            if (!offline.hasPlayedBefore()) {
                plugin.getMessageUtil().send(player, "general.player-not-found");
                return true;
            }
            targetUuid = offline.getUniqueId();
        }

        if (plugin.getTpaManager().isBlocked(player.getUniqueId(), targetUuid)) {
            plugin.getMessageUtil().send(player, "tpa.already-blocked", ph("player", args[0]));
            return true;
        }

        plugin.getTpaManager().blockPlayer(player.getUniqueId(), targetUuid);
        plugin.getMessageUtil().send(player, "tpa.blocked-player", ph("player", args[0]));

        return true;
    }
}
