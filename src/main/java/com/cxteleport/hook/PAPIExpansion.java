package com.cxteleport.hook;

import com.cxteleport.CXTeleport;
import com.cxteleport.manager.CooldownManager;
import com.cxteleport.manager.JailManager;
import com.cxteleport.manager.WarpManager;
import com.cxteleport.model.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIExpansion extends PlaceholderExpansion {

    private final CXTeleport plugin;

    public PAPIExpansion(CXTeleport plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cxteleport";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (params.startsWith("world_players_") && !params.equals("world_players_current")) {
            String worldName = params.substring("world_players_".length());
            return String.valueOf(getPlayersInWorld(worldName));
        }

        if (offlinePlayer == null || !offlinePlayer.isOnline()) return "";
        Player player = offlinePlayer.getPlayer();
        if (player == null) return "";

        PlayerData data = plugin.getPlayerDataManager().get(player);

        switch (params) {
            case "tpa_cooldown": {
                int remaining = plugin.getCooldownManager().getRemainingSeconds("tpa", player.getUniqueId());
                return remaining > 0 ? com.cxteleport.util.MessageUtil.formatTime(remaining) : "";
            }
            case "tpa_cooldown_seconds": {
                int remaining = plugin.getCooldownManager().getRemainingSeconds("tpa", player.getUniqueId());
                return remaining > 0 ? String.valueOf(remaining) : "0";
            }
            case "back_cooldown": {
                int remaining = plugin.getCooldownManager().getRemainingSeconds("back", player.getUniqueId());
                return remaining > 0 ? com.cxteleport.util.MessageUtil.formatTime(remaining) : "";
            }
            case "back_cooldown_seconds": {
                int remaining = plugin.getCooldownManager().getRemainingSeconds("back", player.getUniqueId());
                return remaining > 0 ? String.valueOf(remaining) : "0";
            }
            case "warp_count":
                return String.valueOf(plugin.getWarpManager().getWarpCount());
            case "pvp_protection": {
                int remaining = data.getPvpProtectionRemaining();
                return remaining > 0 ? com.cxteleport.util.MessageUtil.formatTime(remaining) : "";
            }
            case "pvp_protection_seconds": {
                int remaining = data.getPvpProtectionRemaining();
                return remaining > 0 ? String.valueOf(remaining) : "0";
            }
            case "jailed":
                return String.valueOf(data.isJailed());
            case "world_players_current":
                return String.valueOf(getPlayersInWorld(player.getWorld().getName()));
            default:
                return null;
        }
    }

    private int getPlayersInWorld(String worldName) {
        org.bukkit.World world = Bukkit.getWorld(worldName);
        if (world == null) return 0;
        return world.getPlayers().size();
    }
}
