package com.cxteleport.commands.tpa;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.model.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleTPACommand extends BaseCommand {

    public ToggleTPACommand(CXTeleport plugin) {
        super(plugin, "toggletpa", "Ativa ou desativa solicitacoes de teleporte", "/toggletpa");
        this.setPermission("cxteleport.toggle");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        PlayerData data = plugin.getPlayerDataManager().get(player);
        data.setTpaEnabled(!data.isTpaEnabled());

        if (data.isTpaEnabled()) {
            plugin.getMessageUtil().send(player, "tpa.toggle-on");
        } else {
            plugin.getMessageUtil().send(player, "tpa.toggle-off");
        }

        return true;
    }
}
