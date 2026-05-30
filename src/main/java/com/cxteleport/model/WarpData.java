package com.cxteleport.model;

import org.bukkit.Location;

public class WarpData {

    private final String name;
    private Location location;
    private String permission;
    private double cost;
    private String category;
    private long createdAt;

    public WarpData(String name, Location location) {
        this.name = name;
        this.location = location;
        this.cost = 0.0;
        this.category = "";
        this.createdAt = System.currentTimeMillis();
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

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
}
