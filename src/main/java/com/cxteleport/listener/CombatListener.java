package com.cxteleport.listener;

import com.cxteleport.CXTeleport;
import com.cxteleport.model.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class CombatListener implements Listener {

    private final CXTeleport plugin;

    public CombatListener(CXTeleport plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (plugin.getTeleportManager().hasWarmup(player.getUniqueId())) {
            if (plugin.getConfigUtil().isCancelOnDamage()) {
                plugin.getTeleportManager().cancelWarmup(player.getUniqueId());
                plugin.getMessageUtil().send(player, "general.teleport-cancelled-damage");
            }
        }

        PlayerData data = plugin.getPlayerDataManager().get(player);
        data.setLastCombatTime(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        PlayerData attackerData = plugin.getPlayerDataManager().get(attacker);
        if (attackerData.hasPvpProtection()) {
            if (plugin.getConfig().getBoolean("pvp-protection.cancel-on-attack", true)) {
                attackerData.setPvpProtectionEnd(0);
                plugin.getMessageUtil().send(attacker, "pvp.protection-cancelled");
            }
        }

        PlayerData victimData = plugin.getPlayerDataManager().get(victim);
        victimData.setLastCombatTime(System.currentTimeMillis());
        attackerData.setLastCombatTime(System.currentTimeMillis());
    }
}
