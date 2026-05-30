package com.cxteleport.commands.home;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelCasaCommand extends BaseCommand {

    public DelCasaCommand(CXTeleport plugin) {
        super(plugin, "delcasa", "Remove uma casa", "/delcasa <nome>");
        this.setAliases(java.util.List.of("delhome"));
        this.setPermission("cxteleport.delhome");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /delcasa <nome>"));
            return true;
        }

        String homeName = args[0].toLowerCase();

        if (!plugin.getHomeManager().hasHome(player.getUniqueId(), homeName)) {
            plugin.getMessageUtil().send(player, "homes.not-found", ph("home", args[0]));
            return true;
        }

        plugin.getHomeManager().deleteHome(player.getUniqueId(), homeName);
        plugin.getMessageUtil().send(player, "homes.deleted", ph("home", args[0]));

        return true;
    }
}
