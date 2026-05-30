package com.cxteleport.commands.home;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.hook.VaultHook;
import com.cxteleport.manager.HomeManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCasaCommand extends BaseCommand {

    public SetCasaCommand(CXTeleport plugin) {
        super(plugin, "setcasa", "Define uma nova casa", "/setcasa <nome>");
        this.setAliases(java.util.List.of("sethome"));
        this.setPermission("cxteleport.sethome");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /setcasa <nome>"));
            return true;
        }

        String homeName = args[0].toLowerCase();

        if (plugin.getHomeManager().hasHome(player.getUniqueId(), homeName)) {
            plugin.getMessageUtil().send(player, "homes.already-exists", ph("home", homeName));
            return true;
        }

        int limit = plugin.getHomeManager().getHomeLimit(player);
        int current = plugin.getHomeManager().getHomeCount(player.getUniqueId());
        if (current >= limit && !player.hasPermission("cxteleport.home.unlimited")) {
            plugin.getMessageUtil().send(player, "homes.limit-reached",
                    ph("limit", String.valueOf(limit)));
            return true;
        }

        double cost = plugin.getConfigUtil().getHomeSetCost();
        if (cost > 0 && plugin.getVaultHook() != null && plugin.getVaultHook().isEnabled()) {
            VaultHook vault = plugin.getVaultHook();
            if (!vault.has(player, cost)) {
                plugin.getMessageUtil().send(player, "general.no-money",
                        ph("cost", vault.format(cost), "balance", vault.format(vault.getBalance(player))));
                return true;
            }
            vault.withdraw(player, cost);
            plugin.getMessageUtil().send(player, "homes.cost-set", ph("cost", vault.format(cost)));
        }

        plugin.getHomeManager().createHome(player, homeName);
        plugin.getMessageUtil().send(player, "homes.set", ph("home", homeName));

        return true;
    }
}
