package com.github.natanbc.weeb4j.image;

import com.github.natanbc.weeb4j.internal.QueryParam;
import com.github.natanbc.weeb4j.util.QueryStringBuilder;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public enum NsfwFilter implements QueryParam {
    NO_NSFW("false"),
    ALLOW_NSFW("true"),
    ONLY_NSFW("only");

    private final String value;

    NsfwFilter(String value) {
        this.value = value;
    }

    @Override
    public void appendTo(@Nonnull QueryStringBuilder builder) {
        builder.append("nsfw", value);
    }
}
