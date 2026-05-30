package com.cxteleport.commands.admin;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SoltarCommand extends BaseCommand {

    public SoltarCommand(CXTeleport plugin) {
        super(plugin, "soltar", "Liberta um jogador", "/soltar <jogador>");
        this.setAliases(java.util.List.of("unjail"));
        this.setPermission("cxteleport.unjail");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (!plugin.getConfigUtil().isJailEnabled()) {
            sender.sendMessage(plugin.getMessageUtil().color("&cSistema de prisao desativado."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(plugin.getMessageUtil().color("&cUso: /soltar <jogador>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        java.util.UUID targetUuid;

        if (target != null) {
            targetUuid = target.getUniqueId();
        } else {
            org.bukkit.OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
            targetUuid = offline.getUniqueId();
        }

        if (!plugin.getJailManager().isJailed(targetUuid)) {
            plugin.getMessageUtil().send(sender, "jail.not-jailed", ph("player", args[0]));
            return true;
        }

        String senderName = sender instanceof Player ? sender.getName() : "Console";
        plugin.getJailManager().unjail(targetUuid);
        plugin.getMessageUtil().send(sender, "jail.unjailed-player", ph("player", args[0]));
        if (target != null) {
            plugin.getMessageUtil().send(target, "jail.unjailed", ph("player", senderName));
        }

        return true;
    }
}
