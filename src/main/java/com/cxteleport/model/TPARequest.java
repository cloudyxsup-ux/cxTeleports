package com.cxteleport.model;

import java.util.UUID;

public class TPARequest {

    public enum Type {
        TPA,
        TPA_HERE
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        DENIED,
        EXPIRED,
        CANCELLED
    }

    private final UUID requester;
    private final UUID target;
    private final Type type;
    private final long createdAt;
    private Status status;

    public TPARequest(UUID requester, UUID target, Type type) {
        this.requester = requester;
        this.target = target;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
        this.status = Status.PENDING;
    }

    public UUID getRequester() {
        return requester;
    }

    public UUID getTarget() {
        return target;
    }

    public Type getType() {
        return type;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isExpired(long timeoutSeconds) {
        return System.currentTimeMillis() - createdAt > timeoutSeconds * 1000L;
    }
}
