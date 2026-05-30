package com.cxteleport.commands.spawn;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends BaseCommand {

    public SetSpawnCommand(CXTeleport plugin) {
        super(plugin, "setspawn", "Define o spawn", "/setspawn");
        this.setPermission("cxteleport.setspawn");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        plugin.getSpawnManager().setGlobalSpawn(player.getLocation());
        plugin.getMessageUtil().send(player, "spawn.set");

        return true;
    }
}
