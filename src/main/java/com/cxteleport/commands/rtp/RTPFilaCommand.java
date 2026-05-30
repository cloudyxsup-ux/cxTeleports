package com.cxteleport.commands.rtp;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTPFilaCommand extends BaseCommand {

    public RTPFilaCommand(CXTeleport plugin) {
        super(plugin, "rtpfila", "Entra ou sai da fila de RTP", "/rtpfila");
        this.setAliases(java.util.List.of("rtpqueue"));
        this.setPermission("cxteleport.rtp.queue");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        if (!plugin.getConfig().getBoolean("rtp.queue.enabled", true)) {
            player.sendMessage(plugin.getMessageUtil().color("&cFila de RTP desativada."));
            return true;
        }

        if (plugin.getRtpManager().isInQueue(player.getUniqueId())) {
            plugin.getRtpManager().removeFromQueue(player.getUniqueId());
            plugin.getMessageUtil().send(player, "rtp.queue.left");
        } else {
            plugin.getRtpManager().addToQueue(player);
            int pos = plugin.getRtpManager().getQueuePosition(player.getUniqueId());
            plugin.getMessageUtil().send(player, "rtp.queue.joined", ph("position", String.valueOf(pos)));
        }

        return true;
    }
}
