package com.github.natanbc.weeb4j.image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.function.Supplier;

enum NoopImageCache implements ImageCache {
    INSTANCE;

    @Override
    @Nullable
    public InputStream retrieve(@Nonnull String name) {
        return null;
    }

    @Override
    public void save(@Nonnull String name, @Nonnull InputStream in) {
        //noop
    }

    @Override
    public void purge(@Nonnull String name) {
        //noop
    }

    @Override
    @Nonnull
    public InputStream retrieveOrCache(@Nonnull String name, @Nonnull Supplier<InputStream> request) {
        return request.get();
    }
}
