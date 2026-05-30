package com.cxteleport.manager;

import com.cxteleport.CXTeleport;
import com.cxteleport.model.TPAHistoryEntry;
import com.cxteleport.model.TPARequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TPAManager {

    private final CXTeleport plugin;
    private final Map<UUID, List<TPARequest>> incomingRequests = new ConcurrentHashMap<>();
    private final Map<UUID, List<TPARequest>> outgoingRequests = new ConcurrentHashMap<>();
    private final Map<UUID, List<TPAHistoryEntry>> history = new ConcurrentHashMap<>();
    private final Map<UUID, List<UUID>> blockedPlayers = new ConcurrentHashMap<>();

    public TPAManager(CXTeleport plugin) {
        this.plugin = plugin;
    }

    public TPARequest sendRequest(Player requester, Player target, TPARequest.Type type) {
        TPARequest request = new TPARequest(requester.getUniqueId(), target.getUniqueId(), type);

        incomingRequests.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>()).add(request);
        outgoingRequests.computeIfAbsent(requester.getUniqueId(), k -> new ArrayList<>()).add(request);

        if (plugin.getPlayerDataManager().isAutoAccept(target.getUniqueId())) {
            acceptRequest(target, requester.getUniqueId());
            return request;
        }

        int timeout = plugin.getConfigUtil().getTPATimeout();
        plugin.getScheduler().runTaskLaterForEntity(target, () -> {
            if (request.getStatus() == TPARequest.Status.PENDING) {
                request.setStatus(TPARequest.Status.EXPIRED);
                removeRequest(request);
                addHistory(requester.getUniqueId(), target.getUniqueId(), type, TPARequest.Status.EXPIRED);

                Player reqPlayer = Bukkit.getPlayer(requester.getUniqueId());
                Player tgtPlayer = Bukkit.getPlayer(target.getUniqueId());

                if (reqPlayer != null) {
                    plugin.getMessageUtil().send(reqPlayer, "tpa.expired-target",
                            com.cxteleport.util.MessageUtil.ph("player", target.getName()));
                }
                if (tgtPlayer != null && plugin.getConfig().getBoolean("tpa.notify-expire", true)) {
                    plugin.getMessageUtil().send(tgtPlayer, "tpa.expired");
                }
            }
        }, timeout * 20L);

        return request;
    }

    public TPARequest acceptRequest(Player target, UUID requesterUuid) {
        List<TPARequest> requests = incomingRequests.get(target.getUniqueId());
        if (requests == null || requests.isEmpty()) return null;

        TPARequest request = null;
        for (TPARequest req : requests) {
            if (req.getRequester().equals(requesterUuid) && req.getStatus() == TPARequest.Status.PENDING) {
                request = req;
                break;
            }
        }

        if (request == null) {
            for (TPARequest req : new ArrayList<>(requests)) {
                if (req.getStatus() == TPARequest.Status.PENDING) {
                    request = req;
                    break;
                }
            }
        }

        if (request == null) return null;

        request.setStatus(TPARequest.Status.ACCEPTED);
        removeRequest(request);
        addHistory(request.getRequester(), request.getTarget(), request.getType(), TPARequest.Status.ACCEPTED);

        Player requester = Bukkit.getPlayer(request.getRequester());
        Player targetPlayer = Bukkit.getPlayer(request.getTarget());

        if (requester != null && targetPlayer != null) {
            if (request.getType() == TPARequest.Type.TPA) {
                plugin.getTeleportManager().teleport(requester, targetPlayer.getLocation(), "tpa");
            } else {
                plugin.getTeleportManager().teleport(targetPlayer, requester.getLocation(), "tpa");
            }
        }

        return request;
    }

    public TPARequest denyRequest(Player target, UUID requesterUuid) {
        List<TPARequest> requests = incomingRequests.get(target.getUniqueId());
        if (requests == null || requests.isEmpty()) return null;

        TPARequest request = null;
        for (TPARequest req : requests) {
            if (req.getRequester().equals(requesterUuid) && req.getStatus() == TPARequest.Status.PENDING) {
                request = req;
                break;
            }
        }

        if (request == null) {
            for (TPARequest req : new ArrayList<>(requests)) {
                if (req.getStatus() == TPARequest.Status.PENDING) {
                    request = req;
                    break;
                }
            }
        }

        if (request == null) return null;

        request.setStatus(TPARequest.Status.DENIED);
        removeRequest(request);
        addHistory(request.getRequester(), request.getTarget(), request.getType(), TPARequest.Status.DENIED);

        return request;
    }

    public void cancelAllRequests(UUID requester) {
        List<TPARequest> requests = outgoingRequests.remove(requester);
        if (requests != null) {
            for (TPARequest req : requests) {
                if (req.getStatus() == TPARequest.Status.PENDING) {
                    req.setStatus(TPARequest.Status.CANCELLED);
                    List<TPARequest> incoming = incomingRequests.get(req.getTarget());
                    if (incoming != null) incoming.remove(req);
                }
            }
        }
    }

    public List<TPARequest> getIncomingRequests(UUID target) {
        List<TPARequest> requests = incomingRequests.getOrDefault(target, Collections.emptyList());
        List<TPARequest> pending = new ArrayList<>();
        for (TPARequest req : requests) {
            if (req.getStatus() == TPARequest.Status.PENDING) pending.add(req);
        }
        return pending;
    }

    public List<TPARequest> getOutgoingRequests(UUID requester) {
        List<TPARequest> requests = outgoingRequests.getOrDefault(requester, Collections.emptyList());
        List<TPARequest> pending = new ArrayList<>();
        for (TPARequest req : requests) {
            if (req.getStatus() == TPARequest.Status.PENDING) pending.add(req);
        }
        return pending;
    }

    public boolean hasPendingRequest(UUID target) {
        List<TPARequest> requests = incomingRequests.get(target);
        if (requests == null) return false;
        return requests.stream().anyMatch(r -> r.getStatus() == TPARequest.Status.PENDING);
    }

    public boolean hasPendingRequestFrom(UUID target, UUID requester) {
        List<TPARequest> requests = incomingRequests.get(target);
        if (requests == null) return false;
        return requests.stream().anyMatch(r -> r.getRequester().equals(requester) && r.getStatus() == TPARequest.Status.PENDING);
    }

    public boolean hasSentRequestTo(UUID requester, UUID target) {
        List<TPARequest> requests = outgoingRequests.get(requester);
        if (requests == null) return false;
        return requests.stream().anyMatch(r -> r.getTarget().equals(target) && r.getStatus() == TPARequest.Status.PENDING);
    }

    public void blockPlayer(UUID blocker, UUID blocked) {
        blockedPlayers.computeIfAbsent(blocker, k -> new ArrayList<>()).add(blocked);
    }

    public void unblockPlayer(UUID blocker, UUID blocked) {
        List<UUID> list = blockedPlayers.get(blocker);
        if (list != null) list.remove(blocked);
    }

    public boolean isBlocked(UUID blocker, UUID blocked) {
        List<UUID> list = blockedPlayers.get(blocker);
        return list != null && list.contains(blocked);
    }

    public List<TPAHistoryEntry> getHistory(UUID uuid) {
        return history.getOrDefault(uuid, Collections.emptyList());
    }

    public void addHistory(UUID requester, UUID target, TPARequest.Type type, TPARequest.Status status) {
        if (!plugin.getConfig().getBoolean("tpa.history-enabled", true)) return;
        TPAHistoryEntry entry = new TPAHistoryEntry(requester, target, type, status);
        history.computeIfAbsent(requester, k -> new LinkedList<>()).add(0, entry);
        int max = plugin.getConfig().getInt("tpa.max-history", 50);
        List<TPAHistoryEntry> list = history.get(requester);
        while (list.size() > max) list.remove(list.size() - 1);
    }

    private void removeRequest(TPARequest request) {
        List<TPARequest> incoming = incomingRequests.get(request.getTarget());
        if (incoming != null) incoming.remove(request);
        List<TPARequest> outgoing = outgoingRequests.get(request.getRequester());
        if (outgoing != null) outgoing.remove(request);
    }
}
