package com.cxteleport.commands.admin;

import com.cxteleport.CXTeleport;
import com.cxteleport.commands.BaseCommand;
import com.cxteleport.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CXTeleportCommand extends BaseCommand {

    public CXTeleportCommand(CXTeleport plugin) {
        super(plugin, "cxteleport", "Comando principal do plugin", "/cxteleport <reload|help|stats>");
        this.setPermission("cxteleport.admin");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("cxteleport.reload")) {
                    plugin.getMessageUtil().send(sender, "general.no-permission");
                    return true;
                }
                plugin.reloadPlugin();
                plugin.getMessageUtil().send(sender, "general.reloaded");
            }
            case "stats" -> {
                if (!sender.hasPermission("cxteleport.admin")) {
                    plugin.getMessageUtil().send(sender, "general.no-permission");
                    return true;
                }
                sendStats(sender);
            }
            case "help" -> sendHelp(sender);
            case "setjail" -> {
                if (!(sender instanceof Player player)) {
                    plugin.getMessageUtil().send(sender, "general.player-only");
                    return true;
                }
                if (!player.hasPermission("cxteleport.admin")) {
                    plugin.getMessageUtil().send(player, "general.no-permission");
                    return true;
                }
                plugin.getJailManager().setJailLocation(player.getLocation());
                player.sendMessage(plugin.getMessageUtil().color("&aLocal da pris\u00e3o definido com sucesso!"));
            }
            default -> sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getMessageUtil().color("&8======= &bCXTeleport Ajuda &8======="));
        sender.sendMessage(plugin.getMessageUtil().color("&e/teleportar <jogador> &7- Solicita teleporte"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/tpaaqui <jogador> &7- Solicita que o jogador venha"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/tpaceitar &7- Aceita solicitacao"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/tprecusar &7- Recusa solicitacao"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/tpcancelar &7- Cancela solicitacoes"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/toggletpa &7- Ativa/desativa TPA"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/tpaauto &7- Autoaceitacao"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/casa <nome> &7- Teleporta para casa"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/setcasa <nome> &7- Define casa"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/delcasa <nome> &7- Remove casa"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/casas &7- Lista casas"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/warp <nome> &7- Teleporta para warp"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/setwarp <nome> &7- Cria warp"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/delwarp <nome> &7- Remove warp"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/warps &7- Lista warps"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/spawn &7- Vai ao spawn"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/setspawn &7- Define spawn"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/rtp &7- Teleporte aleatorio"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/voltar &7- Retorna ao local anterior"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/tp <jogador> &7- Teleporta ate jogador"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/tphere <jogador> &7- Puxa jogador"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/tppos <x> <y> <z> &7- Teleporta para coordenadas"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/tpsubir <blocos> &7- Teleporta para cima"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/tptodos &7- Teleporta todos"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/prender <jogador> &7- Prende jogador"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/soltar <jogador> &7- Liberta jogador"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/cxteleport reload &7- Recarrega config"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/cxteleport stats &7- Estatisticas"));
        sender.sendMessage(plugin.getMessageUtil().color("&e/cxteleport setjail &7- Define local da prisao"));
    }

    private void sendStats(CommandSender sender) {
        long uptime = (System.currentTimeMillis() - plugin.getStartTime()) / 1000;
        sender.sendMessage(plugin.getMessageUtil().color("&8======= &bCXTeleport Stats &8======="));
        sender.sendMessage(plugin.getMessageUtil().color("&7Total de teleportes: &e" + plugin.getTeleportManager().getTotalTeleports()));
        sender.sendMessage(plugin.getMessageUtil().color("&7Total de casas: &e" + plugin.getHomeManager().getTotalHomeCount()));
        sender.sendMessage(plugin.getMessageUtil().color("&7Total de warps: &e" + plugin.getWarpManager().getWarpCount()));
        sender.sendMessage(plugin.getMessageUtil().color("&7Total de RTPs: &e" + plugin.getRtpManager().getTotalRTPs()));
        sender.sendMessage(plugin.getMessageUtil().color("&7Jogadores presos: &e" + plugin.getJailManager().getJailedCount()));
        sender.sendMessage(plugin.getMessageUtil().color("&7Fila RTP: &e" + plugin.getRtpManager().getQueueSize()));
        sender.sendMessage(plugin.getMessageUtil().color("&7Tempo online: &e" + MessageUtil.formatTime(uptime)));
        sender.sendMessage(plugin.getMessageUtil().color("&7Folia: &e" + plugin.isFolia()));
    }
}
