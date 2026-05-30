package com.cxteleport.commands.tpa;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.model.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAAutoCommand extends BaseCommand {

    public TPAAutoCommand(CXTeleport plugin) {
        super(plugin, "tpaauto", "Ativa ou desativa autoaceitacao", "/tpaauto");
        this.setPermission("cxteleport.tpaauto");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        PlayerData data = plugin.getPlayerDataManager().get(player);
        data.setAutoAccept(!data.isAutoAccept());

        if (data.isAutoAccept()) {
            plugin.getMessageUtil().send(player, "tpa.auto-on");
        } else {
            plugin.getMessageUtil().send(player, "tpa.auto-off");
        }

        return true;
    }
}
