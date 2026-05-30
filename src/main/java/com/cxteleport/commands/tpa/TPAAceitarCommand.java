package com.cxteleport.commands.tpa;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.model.TPARequest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TPAAceitarCommand extends BaseCommand {

    public TPAAceitarCommand(CXTeleport plugin) {
        super(plugin, "tpaceitar", "Aceita uma solicitacao de teleporte", "/tpaceitar [jogador]");
        this.setAliases(java.util.List.of("tpaccept"));
        this.setPermission("cxteleport.tpaccept");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        List<TPARequest> pending = plugin.getTpaManager().getIncomingRequests(player.getUniqueId());
        if (pending.isEmpty()) {
            plugin.getMessageUtil().send(player, "tpa.no-pending");
            return true;
        }

        TPARequest request;

        if (args.length >= 1) {
            Player requester = Bukkit.getPlayer(args[0]);
            if (requester == null) {
                plugin.getMessageUtil().send(player, "general.player-not-found");
                return true;
            }
            if (!plugin.getTpaManager().hasPendingRequestFrom(player.getUniqueId(), requester.getUniqueId())) {
                plugin.getMessageUtil().send(player, "tpa.no-pending-from", ph("player", args[0]));
                return true;
            }
            request = plugin.getTpaManager().acceptRequest(player, requester.getUniqueId());
        } else {
            request = plugin.getTpaManager().acceptRequest(player, null);
        }

        if (request == null) {
            plugin.getMessageUtil().send(player, "tpa.no-pending");
            return true;
        }

        Player requester = Bukkit.getPlayer(request.getRequester());
        plugin.getMessageUtil().send(player, "tpa.accepted");
        if (requester != null) {
            plugin.getMessageUtil().send(requester, "tpa.accepted-target", ph("player", player.getName()));
        }

        return true;
    }
}
