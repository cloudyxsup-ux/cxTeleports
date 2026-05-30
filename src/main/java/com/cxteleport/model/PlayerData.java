package com.cxteleport.model;

import org.bukkit.Location;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private boolean tpaEnabled = true;
    private boolean tpaHereEnabled = true;
    private boolean autoAccept = false;
    private Location lastLocation;
    private Location deathLocation;
    private long lastCombatTime;
    private long pvpProtectionEnd;
    private boolean jailed;
    private String jailReason;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isTpaEnabled() {
        return tpaEnabled;
    }

    public void setTpaEnabled(boolean tpaEnabled) {
        this.tpaEnabled = tpaEnabled;
    }

    public boolean isTpaHereEnabled() {
        return tpaHereEnabled;
    }

    public void setTpaHereEnabled(boolean tpaHereEnabled) {
        this.tpaHereEnabled = tpaHereEnabled;
    }

    public boolean isAutoAccept() {
        return autoAccept;
    }

    public void setAutoAccept(boolean autoAccept) {
        this.autoAccept = autoAccept;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Location getDeathLocation() {
        return deathLocation;
    }

    public void setDeathLocation(Location deathLocation) {
        this.deathLocation = deathLocation;
    }

    public long getLastCombatTime() {
        return lastCombatTime;
    }

    public void setLastCombatTime(long lastCombatTime) {
        this.lastCombatTime = lastCombatTime;
    }

    public boolean isInCombat(int cooldownSeconds) {
        return System.currentTimeMillis() - lastCombatTime < cooldownSeconds * 1000L;
    }

    public long getPvpProtectionEnd() {
        return pvpProtectionEnd;
    }

    public void setPvpProtectionEnd(long pvpProtectionEnd) {
        this.pvpProtectionEnd = pvpProtectionEnd;
    }

    public boolean hasPvpProtection() {
        return System.currentTimeMillis() < pvpProtectionEnd;
    }

    public int getPvpProtectionRemaining() {
        if (!hasPvpProtection()) return 0;
        return (int) ((pvpProtectionEnd - System.currentTimeMillis()) / 1000);
    }

    public boolean isJailed() {
        return jailed;
    }

    public void setJailed(boolean jailed) {
        this.jailed = jailed;
    }

    public String getJailReason() {
        return jailReason;
    }

    public void setJailReason(String jailReason) {
        this.jailReason = jailReason;
    }
}
