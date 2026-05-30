package com.cxteleport.commands.home;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.hook.VaultHook;
import com.cxteleport.model.HomeData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CasaCommand extends BaseCommand {

    public CasaCommand(CXTeleport plugin) {
        super(plugin, "casa", "Teleporta para uma casa", "/casa <nome>");
        this.setAliases(java.util.List.of("home"));
        this.setPermission("cxteleport.home");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (args.length < 1) {
            if (plugin.getConfig().getBoolean("homes.gui-enabled", true)) {
                new com.cxteleport.gui.HomesGUI(plugin).open(player);
                return true;
            }
            player.sendMessage(plugin.getMessageUtil().color("&cUso: /casa <nome>"));
            return true;
        }

        String homeName = args[0].toLowerCase();
        HomeData home = plugin.getHomeManager().getHome(player.getUniqueId(), homeName);

        if (home == null) {
            plugin.getMessageUtil().send(player, "homes.not-found", ph("home", args[0]));
            return true;
        }

        if (plugin.getCooldownManager().checkAndApply(player, "home", plugin.getConfigUtil().getHomeCooldown(), "general.cooldown")) {
            return true;
        }

        double cost = plugin.getConfigUtil().getHomeTeleportCost();
        if (cost > 0 && plugin.getVaultHook() != null && plugin.getVaultHook().isEnabled()) {
            VaultHook vault = plugin.getVaultHook();
            if (!vault.has(player, cost)) {
                plugin.getMessageUtil().send(player, "general.no-money",
                        ph("cost", vault.format(cost), "balance", vault.format(vault.getBalance(player))));
                return true;
            }
            vault.withdraw(player, cost);
            plugin.getMessageUtil().send(player, "homes.cost-teleport", ph("cost", vault.format(cost)));
        }

        if (!checkCombat(player)) return true;

        plugin.getTeleportManager().teleport(player, home.getLocation(), "home");
        plugin.getMessageUtil().send(player, "homes.teleported", ph("home", home.getName()));

        return true;
    }
}
