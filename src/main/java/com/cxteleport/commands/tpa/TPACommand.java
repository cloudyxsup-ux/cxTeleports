package com.cxteleport.commands.tpa;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.model.TPARequest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class TPACommand extends BaseCommand {

    public TPACommand(CXTeleport plugin) {
        super(plugin, "teleportar", "Solicita teleporte para um jogador", "/teleportar <jogador>");
        this.setAliases(java.util.List.of("tpa"));
        this.setPermission("cxteleport.tpa");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /teleportar <jogador>"));
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

        if (!plugin.getPlayerDataManager().isTpaEnabled(target.getUniqueId())) {
            plugin.getMessageUtil().send(player, "tpa.target-toggled-off", ph("target", target.getName()));
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

        TPARequest request = plugin.getTpaManager().sendRequest(player, target, TPARequest.Type.TPA);

        plugin.getMessageUtil().send(player, "tpa.sent", ph("target", target.getName()));
        plugin.getMessageUtil().send(target, "tpa.received", ph("player", player.getName()));

        return true;
    }
}
