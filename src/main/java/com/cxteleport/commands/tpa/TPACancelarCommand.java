package com.cxteleport.commands.tpa;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPACancelarCommand extends BaseCommand {

    public TPACancelarCommand(CXTeleport plugin) {
        super(plugin, "tpcancelar", "Cancela solicitacoes pendentes", "/tpcancelar");
        this.setAliases(java.util.List.of("tpcancel"));
        this.setPermission("cxteleport.tpcancel");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        plugin.getTpaManager().cancelAllRequests(player.getUniqueId());
        plugin.getMessageUtil().send(player, "tpa.cancelled");

        return true;
    }
}
