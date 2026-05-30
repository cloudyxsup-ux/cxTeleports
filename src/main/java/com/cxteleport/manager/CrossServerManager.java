package com.cxteleport.manager;

import com.cxteleport.CXTeleport;
import org.bukkit.Bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CrossServerManager {

    private final CXTeleport plugin;
    private boolean enabled;
    private String proxyType;

    public CrossServerManager(CXTeleport plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getConfigUtil().isCrossServerEnabled();
        this.proxyType = plugin.getConfig().getString("cross-server.proxy-type", "VELOCITY");

        if (enabled) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
            Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord",
                    (channel, player, message) -> handlePluginMessage(channel, player, message));
        }
    }

    public void connectToServer(org.bukkit.entity.Player player, String serverName) {
        if (!enabled) return;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream data = new DataOutputStream(out);
            data.writeUTF("Connect");
            data.writeUTF(serverName);
            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        } catch (IOException e) {
            plugin.getLogger().warning("Falha ao conectar ao servidor: " + serverName);
        }
    }

    public void sendTPARequest(org.bukkit.entity.Player requester, String targetName, String targetServer) {
        if (!enabled) return;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream data = new DataOutputStream(out);
            data.writeUTF("CXTeleport");
            data.writeUTF("TPA_REQUEST");
            data.writeUTF(requester.getName());
            data.writeUTF(targetName);
            data.writeUTF(targetServer);
            requester.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        } catch (IOException e) {
            plugin.getLogger().warning("Falha ao enviar solicitacao TPA cross-server");
        }
    }

    private void handlePluginMessage(String channel, org.bukkit.entity.Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) return;
        // Process incoming cross-server messages
        // Future implementation for cross-server TPA handling
    }

    public void shutdown() {
        if (enabled) {
            Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin, "BungeeCord");
            Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin, "BungeeCord");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}
