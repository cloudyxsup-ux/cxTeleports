package com.cxteleport.commands.warp;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.hook.VaultHook;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand extends BaseCommand {

    public SetWarpCommand(CXTeleport plugin) {
        super(plugin, "setwarp", "Cria uma warp", "/setwarp <nome>");
        this.setPermission("cxteleport.setwarp");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /setwarp <nome>"));
            return true;
        }

        String warpName = args[0].toLowerCase();

        if (plugin.getWarpManager().hasWarp(warpName)) {
            plugin.getMessageUtil().send(player, "warps.already-exists", ph("warp", args[0]));
            return true;
        }

        double cost = plugin.getConfigUtil().getWarpCreateCost();
        if (cost > 0 && plugin.getVaultHook() != null && plugin.getVaultHook().isEnabled()) {
            VaultHook vault = plugin.getVaultHook();
            if (!vault.has(player, cost)) {
                plugin.getMessageUtil().send(player, "general.no-money",
                        ph("cost", vault.format(cost), "balance", vault.format(vault.getBalance(player))));
                return true;
            }
            vault.withdraw(player, cost);
            plugin.getMessageUtil().send(player, "warps.cost-create", ph("cost", vault.format(cost)));
        }

        plugin.getWarpManager().createWarp(warpName, player.getLocation());
        plugin.getMessageUtil().send(player, "warps.created", ph("warp", args[0]));

        return true;
    }
}
