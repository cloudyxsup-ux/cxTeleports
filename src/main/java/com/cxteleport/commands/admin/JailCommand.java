package com.cxteleport.commands.admin;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JailCommand extends BaseCommand {

    public JailCommand(CXTeleport plugin) {
        super(plugin, "prender", "Prende um jogador", "/prender <jogador>");
        this.setAliases(java.util.List.of("jail"));
        this.setPermission("cxteleport.jail");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!plugin.getConfigUtil().isJailEnabled()) {
            sender.sendMessage(plugin.getMessageUtil().color("&cSistema de prisao desativado."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getMessageUtil().color("&cUso: /prender <jogador>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessageUtil().send(sender, "general.player-not-found");
            return true;
        }

        if (!plugin.getJailManager().hasJailLocation()) {
            plugin.getMessageUtil().send(sender, "jail.no-location");
            return true;
        }

        if (plugin.getJailManager().isJailed(target.getUniqueId())) {
            plugin.getMessageUtil().send(sender, "jail.already-jailed", ph("player", target.getName()));
            return true;
        }

        String senderName = sender instanceof Player ? sender.getName() : "Console";
        plugin.getJailManager().jail(target.getUniqueId());
        plugin.getMessageUtil().send(sender, "jail.jailed-player", ph("player", target.getName()));
        plugin.getMessageUtil().send(target, "jail.jailed", ph("player", senderName));

        return true;
    }
}
