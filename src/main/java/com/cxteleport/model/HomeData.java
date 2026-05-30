package com.cxteleport.model;

import org.bukkit.Location;

import java.util.UUID;

public class HomeData {

    private final UUID owner;
    private final String name;
    private Location location;
    private long createdAt;

    public HomeData(UUID owner, String name, Location location) {
        this.owner = owner;
        this.name = name;
        this.location = location;
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getWorldName() {
        return location.getWorld() != null ? location.getWorld().getName() : "unknown";
    }

    public int getX() {
        return location.getBlockX();
    }

    public int getY() {
        return location.getBlockY();
    }

    public int getZ() {
        return location.getBlockZ();
    }
}
