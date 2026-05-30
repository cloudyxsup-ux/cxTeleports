package com.cxteleport.gui;

import com.cxteleport.CXTeleport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RTPGUI {

    private final CXTeleport plugin;

    public RTPGUI(CXTeleport plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        int size = plugin.getConfig().getInt("rtp.gui.size", 27);
        String title = plugin.getMessageUtil().color(plugin.getConfig().getString("rtp.gui.title", "&8Random Teleport"));

        Inventory inv = Bukkit.createInventory(null, size, title);

        if (plugin.getConfig().contains("rtp.worlds")) {
            for (String worldName : plugin.getConfig().getConfigurationSection("rtp.worlds").getKeys(false)) {
                if (!plugin.getConfig().getBoolean("rtp.worlds." + worldName + ".enabled", false)) continue;

                World world = Bukkit.getWorld(worldName);
                if (world == null) continue;

                Material material = getWorldMaterial(worldName);
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.displayName(plugin.getMessageUtil().color("&e" + worldName));
                    List<String> lore = new ArrayList<>();
                    int min = plugin.getConfig().getInt("rtp.worlds." + worldName + ".min-distance",
                            plugin.getConfigUtil().getRTPMinDistance());
                    int max = plugin.getConfig().getInt("rtp.worlds." + worldName + ".max-distance",
                            plugin.getConfigUtil().getRTPMaxDistance());
                    lore.add(plugin.getMessageUtil().color("&7Distancia: &f" + min + " - " + max));
                    lore.add(plugin.getMessageUtil().color("&7Jogadores: &f" + world.getPlayers().size()));
                    lore.add("");
                    lore.add(plugin.getMessageUtil().color("&aClique para RTP"));
                    meta.lore(lore);
                    item.setItemMeta(meta);
                }
                inv.addItem(item);
            }
        }

        fillEmpty(inv, size);
        player.openInventory(inv);
    }

    private Material getWorldMaterial(String worldName) {
        if (worldName.toLowerCase().contains("nether")) return Material.NETHERRACK;
        if (worldName.toLowerCase().contains("end")) return Material.END_STONE;
        return Material.GRASS_BLOCK;
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
