package com.github.natanbc.weeb4j.reputation;

import com.github.natanbc.reliqua.request.RequestException;

@SuppressWarnings("unused")
public class ReputationTransferException extends RequestException {
    private final int code;
    private final Error error;
    private final User user;

    public ReputationTransferException(String message, int code, User user) {
        super(message);
        this.code = code;
        this.error = code >= 1 && code <= 3 ? Error.values()[code - 1] : Error.UNKNOWN;
        this.user = user;
    }

    public int getCode() {
        return code;
    }

    public Error getError() {
        return error;
    }

    public User getUser() {
        return user;
    }

    public enum Error {
        COOLDOWN_HIT, RECEIVED_MAX, MAX_REPUTATION, UNKNOWN
    }
}
