package com.github.natanbc.weeb4j.settings;

import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

public interface SettingCache {
    @Nullable
    JSONObject getSetting(@Nonnull String type, @Nonnull String id);

    void saveSetting(@Nonnull String type, @Nonnull String id, @Nonnull JSONObject setting);

    void invalidateSetting(@Nonnull String type, @Nonnull String id);

    @Nullable
    JSONObject getSubSetting(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id);

    void saveSubSetting(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id, @Nonnull JSONObject setting);

    void invalidateSubSetting(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id);

    Set<CacheEntry> keySet();

    class CacheEntry {
        private final String parentType;
        private final String parentId;
        private final String type;
        private final String id;

        public CacheEntry(String parentType, String parentId, String type, String id) {
            this.parentType = parentType;
            this.parentId = parentId;
            this.type = type;
            this.id = id;
        }

        CacheEntry(String type, String id) {
            this(null, null, type, id);
        }

        public String getParentType() {
            return parentType;
        }

        public String getParentId() {
            return parentId;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(parentId, parentType, type, id);
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof CacheEntry)) {
                return false;
            }
            CacheEntry e = (CacheEntry)obj;
            return
                    e.type.equals(type) &&
                            e.id.equals(id) &&
                            Objects.equals(e.parentType, parentType) &&
                            Objects.equals(e.parentId, parentId);
        }
    }
}
