package com.github.natanbc.weeb4j.image;

import com.github.natanbc.weeb4j.util.QueryStringBuilder;

import javax.annotation.Nonnull;

public interface QueryParam {
    void appendTo(@Nonnull QueryStringBuilder builder);
}
