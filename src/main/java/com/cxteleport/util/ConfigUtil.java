package com.cxteleport.util;

import com.cxteleport.CXTeleport;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtil {

    private final CXTeleport plugin;

    public ConfigUtil(CXTeleport plugin) {
        this.plugin = plugin;
    }

    public boolean getBoolean(String path, boolean def) {
        return plugin.getConfig().getBoolean(path, def);
    }

    public int getInt(String path, int def) {
        return plugin.getConfig().getInt(path, def);
    }

    public double getDouble(String path, double def) {
        return plugin.getConfig().getDouble(path, def);
    }

    public String getString(String path, String def) {
        return plugin.getConfig().getString(path, def);
    }

    public int getWarmup() {
        return getInt("teleport.warmup", 5);
    }

    public boolean isCancelOnMove() {
        return getBoolean("teleport.cancel-on-move", true);
    }

    public boolean isCancelOnDamage() {
        return getBoolean("teleport.cancel-on-damage", true);
    }

    public int getInvulnerability() {
        return getInt("teleport.invulnerability", 5);
    }

    public boolean isCheckSafeLocation() {
        return getBoolean("teleport.check-safe-location", true);
    }

    public int getSafeLocationRange() {
        return getInt("teleport.safe-location-range", 10);
    }

    public int getTPATimeout() {
        return getInt("tpa.request-timeout", 60);
    }

    public int getTPACooldown() {
        return getInt("tpa.cooldown", 10);
    }

    public int getHomeCooldown() {
        return getInt("homes.cooldown", 10);
    }

    public int getDefaultHomeLimit() {
        return getInt("homes.default-limit", 3);
    }

    public boolean isHomePermissionLimits() {
        return getBoolean("homes.use-permission-limits", true);
    }

    public double getHomeSetCost() {
        return getDouble("homes.set-cost", 0.0);
    }

    public double getHomeTeleportCost() {
        return getDouble("homes.teleport-cost", 0.0);
    }

    public int getWarpCooldown() {
        return getInt("warps.cooldown", 5);
    }

    public double getWarpCreateCost() {
        return getDouble("warps.create-cost", 0.0);
    }

    public double getWarpUseCost() {
        return getDouble("warps.use-cost", 0.0);
    }

    public int getSpawnCooldown() {
        return getInt("spawn.cooldown", 5);
    }

    public double getSpawnCost() {
        return getDouble("spawn.cost", 0.0);
    }

    public boolean isSpawnFirstLogin() {
        return getBoolean("spawn.first-login", true);
    }

    public boolean isSpawnPerWorld() {
        return getBoolean("spawn.per-world", false);
    }

    public int getBackCooldown() {
        return getInt("back.cooldown", 30);
    }

    public double getBackCost() {
        return getDouble("back.cost", 0.0);
    }

    public int getRTPCooldown() {
        return getInt("rtp.cooldown", 300);
    }

    public int getRTPMinDistance() {
        return getInt("rtp.min-distance", 500);
    }

    public int getRTPMaxDistance() {
        return getInt("rtp.max-distance", 5000);
    }

    public int getRTPMaxAttempts() {
        return getInt("rtp.max-attempts", 50);
    }

    public int getPvPProtectionDuration() {
        return getInt("pvp-protection.duration", 15);
    }

    public int getCombatCooldown() {
        return getInt("pvp-protection.combat-cooldown", 15);
    }

    public boolean isPvPProtectionEnabled() {
        return getBoolean("pvp-protection.enabled", true);
    }

    public boolean isJailEnabled() {
        return getBoolean("jail.enabled", true);
    }

    public boolean isCrossServerEnabled() {
        return getBoolean("cross-server.enabled", false);
    }
}
