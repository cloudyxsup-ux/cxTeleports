package com.cxteleport.commands.back;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.hook.VaultHook;
import com.cxteleport.model.PlayerData;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoltarCommand extends BaseCommand {

    public VoltarCommand(CXTeleport plugin) {
        super(plugin, "voltar", "Retorna para a ultima localizacao", "/voltar");
        this.setAliases(java.util.List.of("back"));
        this.setPermission("cxteleport.back");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        PlayerData data = plugin.getPlayerDataManager().get(player);

        if (args.length >= 1 && args[0].equalsIgnoreCase("morte") && player.hasPermission("cxteleport.back.death")) {
            Location deathLoc = data.getDeathLocation();
            if (deathLoc == null) {
                plugin.getMessageUtil().send(player, "back.no-location");
                return true;
            }

            if (plugin.getCooldownManager().checkAndApply(player, "back", plugin.getConfigUtil().getBackCooldown(), "general.cooldown")) {
                return true;
            }

            applyCostAndTeleport(player, deathLoc, "back.death-back");
            data.setDeathLocation(null);
            return true;
        }

        Location lastLoc = data.getLastLocation();
        if (lastLoc == null) {
            plugin.getMessageUtil().send(player, "back.no-location");
            return true;
        }

        if (plugin.getCooldownManager().checkAndApply(player, "back", plugin.getConfigUtil().getBackCooldown(), "general.cooldown")) {
            return true;
        }

        if (!checkCombat(player)) return true;

        applyCostAndTeleport(player, lastLoc, "back.teleported");

        return true;
    }

    private void applyCostAndTeleport(Player player, Location loc, String messagePath) {
        double cost = plugin.getConfigUtil().getBackCost();
        if (cost > 0 && plugin.getVaultHook() != null && plugin.getVaultHook().isEnabled()) {
            VaultHook vault = plugin.getVaultHook();
            if (!vault.has(player, cost)) {
                plugin.getMessageUtil().send(player, "general.no-money",
                        ph("cost", vault.format(cost), "balance", vault.format(vault.getBalance(player))));
                return;
            }
            vault.withdraw(player, cost);
        }

        plugin.getTeleportManager().teleport(player, loc, "back");
        plugin.getMessageUtil().send(player, messagePath);
    }
}
