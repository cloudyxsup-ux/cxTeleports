package com.cxteleport.gui;

import com.cxteleport.CXTeleport;
import com.cxteleport.model.WarpData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WarpsGUI {

    private final CXTeleport plugin;

    public WarpsGUI(CXTeleport plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Collection<WarpData> warps = plugin.getWarpManager().getWarps();
        int size = plugin.getConfig().getInt("warps.gui-size", 27);
        String title = plugin.getMessageUtil().color(plugin.getConfig().getString("warps.gui-title", "&8Warps"));

        Inventory inv = Bukkit.createInventory(null, size, title);

        for (WarpData warp : warps) {
            if (!plugin.getWarpManager().canUse(player, warp)) continue;

            Material material;
            try {
                material = Material.valueOf(plugin.getConfig().getString("warps.gui-item", "ENDER_PEARL"));
            } catch (IllegalArgumentException e) {
                material = Material.ENDER_PEARL;
            }

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(plugin.getMessageUtil().color("&e" + warp.getName()));
                List<String> lore = new ArrayList<>();
                lore.add(plugin.getMessageUtil().color("&7Mundo: &f" + warp.getWorldName()));
                if (warp.getCategory() != null && !warp.getCategory().isEmpty()) {
                    lore.add(plugin.getMessageUtil().color("&7Categoria: &f" + warp.getCategory()));
                }
                if (warp.getCost() > 0) {
                    lore.add(plugin.getMessageUtil().color("&7Custo: &e$" + warp.getCost()));
                }
                lore.add("");
                lore.add(plugin.getMessageUtil().color("&aClique para teleportar"));
                meta.lore(lore);
                item.setItemMeta(meta);
            }
            inv.addItem(item);
        }

        fillEmpty(inv, size);
        player.openInventory(inv);
    }

    private void fillEmpty(Inventory inv, int size) {
        Material filler;
        try {
            filler = Material.valueOf(plugin.getConfig().getString("gui.filler-item", "GRAY_STAINED_GLASS_PANE"));
        } catch (IllegalArgumentException e) {
            filler = Material.GRAY_STAINED_GLASS_PANE;
        }

        ItemStack fillItem = new ItemStack(filler);
        ItemMeta fillMeta = fillItem.getItemMeta();
        if (fillMeta != null) {
            fillMeta.displayName(plugin.getMessageUtil().color(" "));
            fillItem.setItemMeta(fillMeta);
        }

        for (int i = 0; i < size; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, fillItem);
            }
        }
    }
}
