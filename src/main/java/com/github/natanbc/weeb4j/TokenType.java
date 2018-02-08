package com.github.natanbc.weeb4j;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Objects;

public enum TokenType {
    BEARER("Bearer"), WOLKE("Wolke");

    private final String prefix;

    TokenType(String prefix) {
        this.prefix = prefix;
    }

    @Nonnull
    @CheckReturnValue
    public String format(@Nonnull String token) {
        Objects.requireNonNull(token, "Token may not be null");
        return prefix + " " + token;
    }
}
