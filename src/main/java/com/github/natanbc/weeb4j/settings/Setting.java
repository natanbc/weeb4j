package com.github.natanbc.weeb4j.settings;

import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Objects;

@SuppressWarnings("unused")
public final class Setting {
    private final String parentType;
    private final String parentId;
    private final String type;
    private final String id;
    private final JSONObject data;

    private Setting(String parentType, String parentId, String type, String id, JSONObject data) {
        this.parentType = parentType;
        this.parentId = parentId;
        this.type = type;
        this.id = id;
        this.data = data;
    }

    /**
     * Returns whether or not this setting is a sub setting.
     *
     * @return Whether or not this setting is a sub setting.
     */
    public boolean isSubSetting() {
        return parentType != null;
    }

    /**
     * Returns this setting's parent type. Returns null if this isn't a sub setting.
     *
     * @return This setting's parent type.
     */
    public String getParentType() {
        return parentType;
    }

    /**
     * Returns this setting's parent id. Returns null if this isn't a sub setting.
     *
     * @return This setting's parent id.
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Returns this setting's type.
     *
     * @return This setting's type.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns this setting's id.
     *
     * @return This setting's id.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns this setting's data.
     *
     * @return This setting's data.
     */
    public JSONObject getData() {
        //copy data
        JSONObject obj = new JSONObject();
        for(String key : data.keySet()) {
            obj.put(key, data.get(key));
        }
        return obj;
    }

    @CheckReturnValue
    @Nonnull
    public static Setting fromJSON(@Nonnull JSONObject json) {
        boolean subsetting = json.has("subId");
        return new Setting(
                subsetting ? json.getString("type") : null,
                subsetting ? json.getString("id") : null,
                json.getString(subsetting ? "subType" : "type"),
                json.getString(subsetting ? "subId" : "id"),
                json.getJSONObject("data")
        );
    }

    @CheckReturnValue
    @Nonnull
    public static Setting create(@Nonnull String type, @Nonnull String id, @Nonnull JSONObject data) {
        Objects.requireNonNull(type, "Type may not be null");
        Objects.requireNonNull(id, "ID may not be null");
        Objects.requireNonNull(data, "Data may not be null");
        return new Setting(
                null,
                null,
                type,
                id,
                data
        );
    }

    @CheckReturnValue
    @Nonnull
    public static Setting create(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id, @Nonnull JSONObject data) {
        Objects.requireNonNull(parentType, "Parent type may not be null");
        Objects.requireNonNull(parentId, "Parent ID may not be null");
        Objects.requireNonNull(type, "Type may not be null");
        Objects.requireNonNull(id, "ID may not be null");
        Objects.requireNonNull(data, "Data may not be null");
        return new Setting(
                parentType,
                parentId,
                type,
                id,
                data
        );
    }
}
