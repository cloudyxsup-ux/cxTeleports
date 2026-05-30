package com.cxteleport.commands.admin;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.model.TeleportEntry;
import com.cxteleport.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TpLogCommand extends BaseCommand {

    public TpLogCommand(CXTeleport plugin) {
        super(plugin, "tplog", "Historico de teletransportes", "/tplog [jogador] [pagina]");
        this.setPermission("cxteleport.tplog");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        List<TeleportEntry> log = plugin.getTeleportManager().getLog();
        int page = 1;

        if (args.length >= 2) {
            try { page = Integer.parseInt(args[1]); } catch (NumberFormatException e) { page = 1; }
        } else if (args.length == 1) {
            try { page = Integer.parseInt(args[0]); } catch (NumberFormatException e) { page = 1; }
        }

        if (log.isEmpty()) {
            sender.sendMessage(plugin.getMessageUtil().color("&cNenhum registro encontrado."));
            return true;
        }

        int perPage = 10;
        int pages = (int) Math.ceil((double) log.size() / perPage);
        page = Math.max(1, Math.min(page, pages));

        plugin.getMessageUtil().sendNoPrefix(sender, "admin.tplog-title",
                ph("page", String.valueOf(page), "pages", String.valueOf(pages)));

        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, log.size());

        for (int i = start; i < end; i++) {
            TeleportEntry entry = log.get(log.size() - 1 - i);
            String time = MessageUtil.formatTimeCompact(System.currentTimeMillis() - entry.getTimestamp());
            sender.sendMessage(plugin.getMessageUtil().color(
                    "&7[" + time + " atr\u00e1s] &e" + entry.getPlayerName()
                            + " &7-> &e" + entry.getTargetName()
                            + " &8(" + entry.getType() + ")"));
        }

        return true;
    }
}
