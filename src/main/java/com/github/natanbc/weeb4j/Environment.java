package com.github.natanbc.weeb4j;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public enum Environment {
    PRODUCTION("https://api.weeb.sh"), STAGING("https://staging.weeb.sh");

    private final String apiBase;

    Environment(String apiBase) {
        this.apiBase = apiBase;
    }

    @CheckReturnValue
    @Nonnull
    public String getApiBase() {
        return apiBase;
    }
}
