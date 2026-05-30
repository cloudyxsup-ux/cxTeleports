package com.cxteleport.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;

public class SafeLocationUtil {

    public static Location findSafeLocation(Location origin, int range) {
        if (isSafe(origin)) return origin;

        for (int y = origin.getBlockY(); y < origin.getWorld().getMaxHeight(); y++) {
            Location loc = new Location(origin.getWorld(), origin.getBlockX(), y, origin.getBlockZ());
            if (isSafe(loc)) return loc;
        }

        for (int y = origin.getBlockY(); y >= origin.getWorld().getMinHeight(); y--) {
            Location loc = new Location(origin.getWorld(), origin.getBlockX(), y, origin.getBlockZ());
            if (isSafe(loc)) return loc;
        }

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = origin.getWorld().getMaxHeight(); y >= origin.getWorld().getMinHeight(); y--) {
                    Location loc = new Location(origin.getWorld(), origin.getBlockX() + x, y, origin.getBlockZ() + z);
                    if (isSafe(loc)) return loc.add(0.5, 0, 0.5);
                }
            }
        }

        return null;
    }

    public static boolean isSafe(Location location) {
        if (location == null || location.getWorld() == null) return false;
        World world = location.getWorld();

        Block feet = world.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Block head = world.getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
        Block below = world.getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());

        if (isDangerous(feet.getType()) || isDangerous(head.getType())) return false;
        if (below.getType() == Material.AIR || below.getType() == Material.VOID_AIR) return false;
        if (isDangerous(below.getType())) return false;

        return !feet.getType().isSolid() && !head.getType().isSolid();
    }

    public static boolean isSafe(Location location, List<String> blacklistedBlocks, List<String> blacklistedBiomes) {
        if (!isSafe(location)) return false;

        if (blacklistedBiomes != null) {
            String biomeName = location.getBlock().getBiome().name();
            if (blacklistedBiomes.contains(biomeName)) return false;
        }

        if (blacklistedBlocks != null) {
            Block below = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
            String blockName = below.getType().name();
            if (blacklistedBlocks.contains(blockName)) return false;
        }

        return true;
    }

    private static boolean isDangerous(Material material) {
        return material == Material.LAVA
                || material == Material.FIRE
                || material == Material.SOUL_FIRE
                || material == Material.CACTUS
                || material == Material.MAGMA_BLOCK
                || material == Material.SWEET_BERRY_BUSH
                || material == Material.POWDER_SNOW
                || material == Material.WITHER_ROSE
                || material == Material.TRIPWIRE
                || material == Material.TRIPWIRE_HOOK
                || material == Material.TNT
                || material == Material.END_PORTAL
                || material == Material.END_GATEWAY
                || material == Material.NETHER_PORTAL;
    }

    public static Location getRoundedLocation(Location location) {
        return new Location(
                location.getWorld(),
                Math.floor(location.getX()) + 0.5,
                location.getY(),
                Math.floor(location.getZ()) + 0.5,
                location.getYaw(),
                location.getPitch()
        );
    }
}
