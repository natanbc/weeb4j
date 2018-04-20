package com.github.natanbc.weeb4j.settings;

import com.github.benmanes.caffeine.cache.Cache;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("unused")
public class CaffeineSettingCache implements SettingCache {
    private final Cache<CacheEntry, JSONObject> cache;

    public CaffeineSettingCache(Cache<CacheEntry, JSONObject> cache) {
        this.cache = cache;
    }

    @Nullable
    @Override
    public JSONObject getSetting(@Nonnull String type, @Nonnull String id) {
        return cache.getIfPresent(new CacheEntry(type, id));
    }

    @Override
    public void saveSetting(@Nonnull String type, @Nonnull String id, @Nonnull JSONObject setting) {
        cache.put(new CacheEntry(type, id), setting);
    }

    @Override
    public void invalidateSetting(@Nonnull String type, @Nonnull String id) {
        cache.invalidate(new CacheEntry(type, id));
    }

    @Nullable
    @Override
    public JSONObject getSubSetting(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id) {
        return cache.getIfPresent(new CacheEntry(parentType, parentId, type, id));
    }

    @Override
    public void saveSubSetting(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id, @Nonnull JSONObject setting) {
        cache.put(new CacheEntry(parentType, parentId, type, id), setting);
    }

    @Override
    public void invalidateSubSetting(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id) {
        cache.invalidate(new CacheEntry(parentType, parentId, type, id));
    }

    @Override
    public Set<CacheEntry> keySet() {
        return cache.asMap().keySet();
    }
}

