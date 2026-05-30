package com.cxteleport.commands.admin;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpPosCommand extends BaseCommand {

    public TpPosCommand(CXTeleport plugin) {
        super(plugin, "tppos", "Teleporta para coordenadas", "/tppos <x> <y> <z> [mundo]");
        this.setPermission("cxteleport.tppos");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 3) {
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /tppos <x> <y> <z> [mundo]"));
            return true;
        }

        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            World world = args.length >= 4 ? Bukkit.getWorld(args[3]) : player.getWorld();

            if (world == null) {
                player.sendMessage(plugin.getMessageUtil().color("&cMundo nao encontrado."));
                return true;
            }

            org.bukkit.Location loc = new org.bukkit.Location(world, x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
            plugin.getPlayerDataManager().setLastLocation(player);
            plugin.getTeleportManager().teleportInstant(player, loc, "admin-tppos");
            plugin.getMessageUtil().send(player, "admin.tppos-success",
                    ph("x", String.valueOf(x), "y", String.valueOf(y), "z", String.valueOf(z)));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessageUtil().color("&cCoordenadas invalidas."));
            return true;
        }
    }
}
