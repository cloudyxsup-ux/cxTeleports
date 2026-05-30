package com.cxteleport.listener;

import com.cxteleport.CXTeleport;
import com.cxteleport.model.HomeData;
import com.cxteleport.model.WarpData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

    private final CXTeleport plugin;

    public GUIListener(CXTeleport plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) return;

        String title = event.getView().getTitle();
        String homesTitle = plugin.getMessageUtil().color(plugin.getConfig().getString("homes.gui-title", "&8Suas Casas"));
        String warpsTitle = plugin.getMessageUtil().color(plugin.getConfig().getString("warps.gui-title", "&8Warps"));
        String rtpTitle = plugin.getMessageUtil().color(plugin.getConfig().getString("rtp.gui.title", "&8Random Teleport"));

        if (title.equals(homesTitle)) {
            event.setCancelled(true);
            handleHomesClick(player, event);
        } else if (title.equals(warpsTitle)) {
            event.setCancelled(true);
            handleWarpsClick(player, event);
        } else if (title.equals(rtpTitle)) {
            event.setCancelled(true);
            handleRTPClick(player, event);
        }
    }

    private void handleHomesClick(Player player, InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
        String name = event.getCurrentItem().getItemMeta().getDisplayName();
        String homeName = org.bukkit.ChatColor.stripColor(name);

        HomeData home = plugin.getHomeManager().getHome(player.getUniqueId(), homeName);
        if (home == null) return;

        if (event.isRightClick()) {
            plugin.getHomeManager().deleteHome(player.getUniqueId(), homeName);
            plugin.getMessageUtil().send(player, "homes.deleted", com.cxteleport.util.MessageUtil.ph("home", homeName));
            player.closeInventory();
            return;
        }

        player.closeInventory();
        plugin.getTeleportManager().teleport(player, home.getLocation(), "home");
        plugin.getMessageUtil().send(player, "homes.teleported", com.cxteleport.util.MessageUtil.ph("home", homeName));
    }

    private void handleWarpsClick(Player player, InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
        String name = event.getCurrentItem().getItemMeta().getDisplayName();
        String warpName = org.bukkit.ChatColor.stripColor(name);

        WarpData warp = plugin.getWarpManager().getWarp(warpName);
        if (warp == null) return;

        player.closeInventory();
        plugin.getTeleportManager().teleport(player, warp.getLocation(), "warp");
        plugin.getMessageUtil().send(player, "warps.teleported", com.cxteleport.util.MessageUtil.ph("warp", warpName));
    }

    private void handleRTPClick(Player player, InventoryClickEvent event) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
        String name = event.getCurrentItem().getItemMeta().getDisplayName();
        String worldName = org.bukkit.ChatColor.stripColor(name);

        player.closeInventory();
        player.performCommand("rtp " + worldName);
    }
}
