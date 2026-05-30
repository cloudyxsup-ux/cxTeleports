package com.cxteleport.util;

import com.cxteleport.CXTeleport;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {

    private final CXTeleport plugin;
    private final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public MessageUtil(CXTeleport plugin) {
        this.plugin = plugin;
    }

    public String color(String message) {
        if (message == null) return "";
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.COLOR_CHAR + "x"
                    + ChatColor.COLOR_CHAR + group.charAt(0)
                    + ChatColor.COLOR_CHAR + group.charAt(1)
                    + ChatColor.COLOR_CHAR + group.charAt(2)
                    + ChatColor.COLOR_CHAR + group.charAt(3)
                    + ChatColor.COLOR_CHAR + group.charAt(4)
                    + ChatColor.COLOR_CHAR + group.charAt(5));
        }
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public String getPrefix() {
        String prefix = plugin.getMessages().getString("prefix", "&8[&bCXTeleport&8] &r");
        return color(prefix);
    }

    public String get(String path) {
        String msg = plugin.getMessages().getString(path, "");
        if (msg.isEmpty()) return color("&cMensagem nao encontrada: " + path);
        return color(msg);
    }

    public String get(String path, Map<String, String> placeholders) {
        String msg = get(path);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return msg;
    }

    public void send(CommandSender sender, String path) {
        String msg = get(path);
        if (msg.isEmpty()) return;
        sender.sendMessage(getPrefix() + msg);
    }

    public void send(CommandSender sender, String path, Map<String, String> placeholders) {
        String msg = get(path, placeholders);
        if (msg.isEmpty()) return;
        if (sender instanceof Player && plugin.isPapiEnabled()) {
            msg = PlaceholderAPI.setPlaceholders((Player) sender, msg);
        }
        sender.sendMessage(getPrefix() + msg);
    }

    public void sendNoPrefix(CommandSender sender, String path, Map<String, String> placeholders) {
        String msg = get(path, placeholders);
        if (msg.isEmpty()) return;
        if (sender instanceof Player && plugin.isPapiEnabled()) {
            msg = PlaceholderAPI.setPlaceholders((Player) sender, msg);
        }
        sender.sendMessage(msg);
    }

    public void sendRaw(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    public static Map<String, String> ph(String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static Map<String, String> ph(String k1, String v1, String k2, String v2) {
        Map<String, String> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static Map<String, String> ph(String k1, String v1, String k2, String v2, String k3, String v3) {
        Map<String, String> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static String formatTime(long seconds) {
        if (seconds <= 0) return "0s";
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (secs > 0 || sb.length() == 0) sb.append(secs).append("s");
        return sb.toString().trim();
    }

    public static String formatTimeCompact(long millis) {
        long seconds = millis / 1000;
        return formatTime(seconds);
    }
}
