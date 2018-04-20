package com.github.natanbc.weeb4j.reputation;

import org.json.JSONObject;

@SuppressWarnings("unused")
public final class TransferResult {
    private final User source;
    private final User target;

    private TransferResult(User source, User target) {
        this.source = source;
        this.target = target;
    }

    public User getSourceUser() {
        return source;
    }

    public User getTargetUser() {
        return target;
    }

    public static TransferResult fromJSON(JSONObject json) {
        return new TransferResult(
                User.fromJSON(json.getJSONObject("sourceUser")),
                User.fromJSON(json.getJSONObject("targetUser"))
        );
    }
}
