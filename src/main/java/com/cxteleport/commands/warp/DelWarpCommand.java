package com.cxteleport.commands.warp;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelWarpCommand extends BaseCommand {

    public DelWarpCommand(CXTeleport plugin) {
        super(plugin, "delwarp", "Remove uma warp", "/delwarp <nome>");
        this.setPermission("cxteleport.delwarp");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /delwarp <nome>"));
            return true;
        }

        String warpName = args[0].toLowerCase();

        if (!plugin.getWarpManager().hasWarp(warpName)) {
            plugin.getMessageUtil().send(player, "warps.not-found", ph("warp", args[0]));
            return true;
        }

        plugin.getWarpManager().deleteWarp(warpName);
        plugin.getMessageUtil().send(player, "warps.deleted", ph("warp", args[0]));

        return true;
    }
}
