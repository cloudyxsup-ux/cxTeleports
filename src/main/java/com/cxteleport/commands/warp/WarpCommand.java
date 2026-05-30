package com.cxteleport.commands.warp;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.hook.VaultHook;
import com.cxteleport.model.WarpData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand extends BaseCommand {

    public WarpCommand(CXTeleport plugin) {
        super(plugin, "warp", "Teleporta para uma warp", "/warp <nome>");
        this.setPermission("cxteleport.warp");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            if (plugin.getConfig().getBoolean("warps.gui-enabled", true)) {
                new com.cxteleport.gui.WarpsGUI(plugin).open(player);
                return true;
            }
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /warp <nome>"));
            return true;
        }

        String warpName = args[0].toLowerCase();
        WarpData warp = plugin.getWarpManager().getWarp(warpName);

        if (warp == null) {
            plugin.getMessageUtil().send(player, "warps.not-found", ph("warp", args[0]));
            return true;
        }

        if (!plugin.getWarpManager().canUse(player, warp)) {
            plugin.getMessageUtil().send(player, "warps.no-permission-warp");
            return true;
        }

        if (plugin.getCooldownManager().checkAndApply(player, "warp", plugin.getConfigUtil().getWarpCooldown(), "general.cooldown")) {
            return true;
        }

        double cost = warp.getCost() > 0 ? warp.getCost() : plugin.getConfigUtil().getWarpUseCost();
        if (cost > 0 && plugin.getVaultHook() != null && plugin.getVaultHook().isEnabled()) {
            VaultHook vault = plugin.getVaultHook();
            if (!vault.has(player, cost)) {
                plugin.getMessageUtil().send(player, "general.no-money",
                        ph("cost", vault.format(cost), "balance", vault.format(vault.getBalance(player))));
                return true;
            }
            vault.withdraw(player, cost);
            plugin.getMessageUtil().send(player, "warps.cost-use", ph("cost", vault.format(cost)));
        }

        if (!checkCombat(player)) return true;

        plugin.getTeleportManager().teleport(player, warp.getLocation(), "warp");
        plugin.getMessageUtil().send(player, "warps.teleported", ph("warp", warp.getName()));

        return true;
    }
}
