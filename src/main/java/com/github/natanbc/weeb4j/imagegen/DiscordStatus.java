package com.github.natanbc.weeb4j.imagegen;

import com.github.natanbc.weeb4j.internal.QueryParam;
import com.github.natanbc.weeb4j.util.QueryStringBuilder;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public enum  DiscordStatus implements QueryParam {
    ONLINE("online"), IDLE("idle"), DO_NOT_DISTURB("dnd"), STREAMING("streaming"), OFFLINE("offline");

    private final String key;

    DiscordStatus(String key) {
        this.key = key;
    }

    @Override
    public void appendTo(@Nonnull QueryStringBuilder builder) {
        builder.append("status", key);
    }
}
