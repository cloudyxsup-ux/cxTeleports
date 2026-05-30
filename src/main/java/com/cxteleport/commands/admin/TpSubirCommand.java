package com.cxteleport.commands.admin;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpSubirCommand extends BaseCommand {

    public TpSubirCommand(CXTeleport plugin) {
        super(plugin, "tpsubir", "Teleporta para cima", "/tpsubir <blocos>");
        this.setAliases(java.util.List.of("tpup"));
        this.setPermission("cxteleport.tpup");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /tpsubir <blocos>"));
            return true;
        }

        try {
            int blocks = Integer.parseInt(args[0]);
            if (blocks <= 0) {
                player.sendMessage(plugin.getMessageUtil().color("&cO valor deve ser positivo."));
                return true;
            }

            org.bukkit.Location current = player.getLocation();
            org.bukkit.Location target = current.clone().add(0, blocks, 0);

            plugin.getPlayerDataManager().setLastLocation(player);
            plugin.getTeleportManager().teleportInstant(player, target, "admin-tpup");
            plugin.getMessageUtil().send(player, "admin.tpup-success", ph("blocks", String.valueOf(blocks)));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessageUtil().color("&cValor invalido."));
            return true;
        }
    }
}
