package com.cxteleport.commands;

import com.cxteleport.CXTeleport;
import com.cxteleport.manager.TeleportManager;
import com.cxteleport.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseCommand extends Command {

    protected final CXTeleport plugin;

    public BaseCommand(CXTeleport plugin, String name, String description, String usage) {
        super(name, description, usage);
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender)) return true;
        return onCommand(sender, args);
    }

    public abstract boolean onCommand(CommandSender sender, String[] args);

    protected boolean isPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            plugin.getMessageUtil().send(sender, "general.player-only");
            return false;
        }
        return true;
    }

    protected Player requirePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            plugin.getMessageUtil().send(sender, "general.player-only");
            return null;
        }
        return (Player) sender;
    }

    protected Map<String, String> ph(String key, String value) {
        return MessageUtil.ph(key, value);
    }

    protected Map<String, String> ph(String k1, String v1, String k2, String v2) {
        return MessageUtil.ph(k1, v1, k2, v2);
    }

    protected Map<String, String> ph(String k1, String v1, String k2, String v2, String k3, String v3) {
        return MessageUtil.ph(k1, v1, k2, v2, k3, v3);
    }

    protected boolean checkCombat(Player player) {
        TeleportManager.TeleportResult result = plugin.getTeleportManager().canTeleport(player);
        if (result != TeleportManager.TeleportResult.SUCCESS) {
            plugin.getTeleportManager().sendCannotTeleportMessage(player, result);
            return false;
        }
        return true;
    }
}
