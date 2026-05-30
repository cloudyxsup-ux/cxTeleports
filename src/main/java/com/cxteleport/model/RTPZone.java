package com.cxteleport.model;

import org.bukkit.Location;

public class RTPZone {

    private final String name;
    private final String world;
    private final int minX;
    private final int maxX;
    private final int minZ;
    private final int maxZ;
    private final int countdown;
    private final String message;

    public RTPZone(String name, String world, int minX, int maxX, int minZ, int maxZ, int countdown, String message) {
        this.name = name;
        this.world = world;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.countdown = countdown;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getCountdown() {
        return countdown;
    }

    public String getMessage() {
        return message;
    }

    public boolean contains(Location loc) {
        if (!loc.getWorld().getName().equalsIgnoreCase(world)) return false;
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }
}
