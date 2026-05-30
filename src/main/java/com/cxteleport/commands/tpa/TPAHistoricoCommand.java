package com.cxteleport.commands.tpa;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.model.TPAHistoryEntry;
import com.cxteleport.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TPAHistoricoCommand extends BaseCommand {

    public TPAHistoricoCommand(CXTeleport plugin) {
        super(plugin, "tpahistorico", "Exibe historico de solicitacoes", "/tpahistorico [pagina]");
        this.setAliases(java.util.List.of("tpahistory"));
        this.setPermission("cxteleport.tpahistory");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = requirePlayer(sender);
        if (player == null) return true;

        int page = 1;
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        List<TPAHistoryEntry> history = plugin.getTpaManager().getHistory(player.getUniqueId());
        if (history.isEmpty()) {
            plugin.getMessageUtil().send(player, "tpa.history-empty");
            return true;
        }

        int perPage = 10;
        int pages = (int) Math.ceil((double) history.size() / perPage);
        page = Math.max(1, Math.min(page, pages));

        plugin.getMessageUtil().sendNoPrefix(player, "tpa.history-title",
                ph("page", String.valueOf(page), "pages", String.valueOf(pages)));

        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, history.size());

        for (int i = start; i < end; i++) {
            TPAHistoryEntry entry = history.get(i);
            String requesterName = Bukkit.getOfflinePlayer(entry.getRequester()).getName();
            String targetName = Bukkit.getOfflinePlayer(entry.getTarget()).getName();
            String time = MessageUtil.formatTimeCompact(
                    System.currentTimeMillis() - entry.getTimestamp());

            player.sendMessage(plugin.getMessageUtil().color(
                    "&7[" + time + " atr\u00e1s] &e" + requesterName
                            + " &7-> &e" + targetName
                            + " &8(" + entry.getStatus().name() + ")"));
        }

        return true;
    }
}
