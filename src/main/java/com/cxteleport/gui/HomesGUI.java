package com.cxteleport.gui;

import com.cxteleport.CXTeleport;
import com.cxteleport.model.HomeData;
import com.cxteleport.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HomesGUI {

    private final CXTeleport plugin;

    public HomesGUI(CXTeleport plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Collection<HomeData> homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        int size = plugin.getConfig().getInt("homes.gui-size", 27);
        String title = plugin.getMessageUtil().color(plugin.getConfig().getString("homes.gui-title", "&8Suas Casas"));

        Inventory inv = Bukkit.createInventory(null, size, title);

        for (HomeData home : homes) {
            Material material;
            try {
                material = Material.valueOf(plugin.getConfig().getString("homes.gui-item", "RED_BED"));
            } catch (IllegalArgumentException e) {
                material = Material.RED_BED;
            }

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(plugin.getMessageUtil().color("&e" + home.getName()));
                List<String> lore = new ArrayList<>();
                lore.add(plugin.getMessageUtil().color("&7Mundo: &f" + home.getWorldName()));
                lore.add(plugin.getMessageUtil().color("&7X: &f" + home.getX() + " &7Y: &f" + home.getY() + " &7Z: &f" + home.getZ()));
                lore.add("");
                lore.add(plugin.getMessageUtil().color("&aClique para teleportar"));
                lore.add(plugin.getMessageUtil().color("&cClique direito para deletar"));
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
