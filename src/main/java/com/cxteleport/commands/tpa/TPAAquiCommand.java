package com.cxteleport.commands.tpa;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.model.TPARequest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class TPAAquiCommand extends BaseCommand {

    public TPAAquiCommand(CXTeleport plugin) {
        super(plugin, "tpaaqui", "Solicita que um jogador venha ate voce", "/tpaaqui <jogador>");
        this.setAliases(java.util.List.of("tpahere"));
        this.setPermission("cxteleport.tpahere");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /tpaaqui <jogador>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessageUtil().send(player, "general.player-not-found");
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            plugin.getMessageUtil().send(player, "tpa.self-teleport");
            return true;
        }

        if (!plugin.getPlayerDataManager().isTpaHereEnabled(target.getUniqueId())) {
            plugin.getMessageUtil().send(player, "tpa.target-toggled-off-here", ph("target", target.getName()));
            return true;
        }

        if (plugin.getTpaManager().isBlocked(target.getUniqueId(), player.getUniqueId())) {
            plugin.getMessageUtil().send(player, "tpa.blocked-by-target", ph("target", target.getName()));
            return true;
        }

        if (plugin.getTpaManager().hasSentRequestTo(player.getUniqueId(), target.getUniqueId())) {
            plugin.getMessageUtil().send(player, "tpa.already-sent", ph("player", target.getName()));
            return true;
        }

        if (plugin.getCooldownManager().checkAndApply(player, "tpa", plugin.getConfigUtil().getTPACooldown(), "general.cooldown")) {
            return true;
        }

        if (!checkCombat(player)) return true;

        TPARequest request = plugin.getTpaManager().sendRequest(player, target, TPARequest.Type.TPA_HERE);

        plugin.getMessageUtil().send(player, "tpa.sent", ph("target", target.getName()));
        plugin.getMessageUtil().send(target, "tpa.received-here", ph("player", player.getName()));

        return true;
    }
}
