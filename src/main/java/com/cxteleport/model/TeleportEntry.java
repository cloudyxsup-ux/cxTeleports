package com.cxteleport.model;

import org.bukkit.Location;

public class TeleportEntry {

    private final UUID playerId;
    private final String playerName;
    private final String targetName;
    private final String type;
    private final Location from;
    private final Location to;
    private final long timestamp;

    public TeleportEntry(java.util.UUID playerId, String playerName, String targetName, String type, Location from, Location to) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.targetName = targetName;
        this.type = type;
        this.from = from;
        this.to = to;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getPlayerId() { return playerId; }
    public String getPlayerName() { return playerName; }
    public String getTargetName() { return targetName; }
    public String getType() { return type; }
    public Location getFrom() { return from; }
    public Location getTo() { return to; }
    public long getTimestamp() { return timestamp; }
}
