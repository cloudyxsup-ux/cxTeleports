package com.cxteleport.commands.home;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.model.HomeData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CasasCommand extends BaseCommand {

    public CasasCommand(CXTeleport plugin) {
        super(plugin, "casas", "Lista todas as casas", "/casas");
        this.setAliases(java.util.List.of("homes"));
        this.setPermission("cxteleport.homes");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (plugin.getConfig().getBoolean("homes.gui-enabled", true)) {
            new com.cxteleport.gui.HomesGUI(plugin).open(player);
            return true;
        }

        Collection<HomeData> homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        int limit = plugin.getHomeManager().getHomeLimit(player);
        String limitStr = limit >= 999 ? "\u221e" : String.valueOf(limit);

        if (homes.isEmpty()) {
            plugin.getMessageUtil().send(player, "homes.list-empty");
            return true;
        }

        plugin.getMessageUtil().sendNoPrefix(player, "homes.list-title",
                ph("count", String.valueOf(homes.size()), "limit", limitStr));

        for (HomeData home : homes) {
            plugin.getMessageUtil().sendNoPrefix(player, "homes.list-entry",
                    ph("home", home.getName(),
                            "world", home.getWorldName(),
                            "x", String.valueOf(home.getX()),
                            "y", String.valueOf(home.getY()),
                            "z", String.valueOf(home.getZ())));
        }

        return true;
    }
}
