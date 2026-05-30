package com.cxteleport.commands.tpa;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.model.TPARequest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TPARecusarCommand extends BaseCommand {

    public TPARecusarCommand(CXTeleport plugin) {
        super(plugin, "tprecusar", "Recusa uma solicitacao de teleporte", "/tprecusar [jogador]");
        this.setAliases(java.util.List.of("tpdeny"));
        this.setPermission("cxteleport.tpdeny");
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
            request = plugin.getTpaManager().denyRequest(player, requester.getUniqueId());
        } else {
            request = plugin.getTpaManager().denyRequest(player, null);
        }

        if (request == null) {
            plugin.getMessageUtil().send(player, "tpa.no-pending");
            return true;
        }

        Player requester = Bukkit.getPlayer(request.getRequester());
        plugin.getMessageUtil().send(player, "tpa.denied");
        if (requester != null) {
            plugin.getMessageUtil().send(requester, "tpa.denied-target", ph("player", player.getName()));
        }

        return true;
    }
}
