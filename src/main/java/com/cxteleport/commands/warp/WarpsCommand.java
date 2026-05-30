package com.cxteleport.commands.warp;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.model.WarpData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class WarpsCommand extends BaseCommand {

    public WarpsCommand(CXTeleport plugin) {
        super(plugin, "warps", "Lista todas as warps", "/warps");
        this.setPermission("cxteleport.warps");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (plugin.getConfig().getBoolean("warps.gui-enabled", true)) {
            new com.cxteleport.gui.WarpsGUI(plugin).open(player);
            return true;
        }

        Collection<WarpData> warps = plugin.getWarpManager().getWarps();

        if (warps.isEmpty()) {
            plugin.getMessageUtil().send(player, "warps.list-empty");
            return true;
        }

        plugin.getMessageUtil().sendNoPrefix(player, "warps.list-title",
                ph("count", String.valueOf(warps.size())));

        for (WarpData warp : warps) {
            plugin.getMessageUtil().sendNoPrefix(player, "warps.list-entry",
                    ph("warp", warp.getName(),
                            "world", warp.getWorldName()));
        }

        return true;
    }
}
