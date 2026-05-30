package com.cxteleport.commands.spawn;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.hook.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseCommand {

    public SpawnCommand(CXTeleport plugin) {
        super(plugin, "spawn", "Teleporta para o spawn", "/spawn [jogador]");
        this.setPermission("cxteleport.spawn");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length >= 1 && sender.hasPermission("cxteleport.spawn.others")) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                plugin.getMessageUtil().send(sender, "general.player-not-found");
                return true;
            }

            Location spawn = plugin.getSpawnManager().getSpawn(target.getWorld().getName());
            if (spawn == null) {
                plugin.getMessageUtil().send(sender, "spawn.not-set");
                return true;
            }

            plugin.getTeleportManager().teleportInstant(target, spawn, "spawn");
            plugin.getMessageUtil().send(sender, "spawn.teleported-other", ph("player", target.getName()));
            return true;
        }

        Player player = requirePlayer(sender);
        if (player == null) return true;

        Location spawn = plugin.getSpawnManager().getSpawn(player.getWorld().getName());
        if (spawn == null) {
            plugin.getMessageUtil().send(player, "spawn.not-set");
            return true;
        }

        if (plugin.getCooldownManager().checkAndApply(player, "spawn", plugin.getConfigUtil().getSpawnCooldown(), "general.cooldown")) {
            return true;
        }

        double cost = plugin.getConfigUtil().getSpawnCost();
        if (cost > 0 && plugin.getVaultHook() != null && plugin.getVaultHook().isEnabled()) {
            VaultHook vault = plugin.getVaultHook();
            if (!vault.has(player, cost)) {
                plugin.getMessageUtil().send(player, "general.no-money",
                        ph("cost", vault.format(cost), "balance", vault.format(vault.getBalance(player))));
                return true;
            }
            vault.withdraw(player, cost);
        }

        if (!checkCombat(player)) return true;

        plugin.getTeleportManager().teleport(player, spawn, "spawn");
        plugin.getMessageUtil().send(player, "spawn.teleported");

        return true;
    }
}
