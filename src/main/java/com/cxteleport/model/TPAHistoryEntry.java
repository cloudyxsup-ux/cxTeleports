package com.cxteleport.model;

import java.util.UUID;

public class TPAHistoryEntry {

    private final UUID requester;
    private final UUID target;
    private final TPARequest.Type type;
    private final TPARequest.Status status;
    private final long timestamp;

    public TPAHistoryEntry(UUID requester, UUID target, TPARequest.Type type, TPARequest.Status status) {
        this.requester = requester;
        this.target = target;
        this.type = type;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getRequester() {
        return requester;
    }

    public UUID getTarget() {
        return target;
    }

    public TPARequest.Type getType() {
        return type;
    }

    public TPARequest.Status getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
