package com.cxteleport.commands.tpa;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.model.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleTPAAquiCommand extends BaseCommand {

    public ToggleTPAAquiCommand(CXTeleport plugin) {
        super(plugin, "toggletpaaqui", "Ativa ou desativa solicitacoes de vir ate voce", "/toggletpaaqui");
        this.setPermission("cxteleport.toggle");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        PlayerData data = plugin.getPlayerDataManager().get(player);
        data.setTpaHereEnabled(!data.isTpaHereEnabled());

        if (data.isTpaHereEnabled()) {
            plugin.getMessageUtil().send(player, "tpa.toggle-here-on");
        } else {
            plugin.getMessageUtil().send(player, "tpa.toggle-here-off");
        }

        return true;
    }
}
