package com.github.natanbc.weeb4j.settings;

import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.weeb4j.Weeb4J;
import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused")
public interface SettingManager {
    /**
     * Returns the Weeb4J instance associated with this object.
     *
     * @return The Weeb4J instance associated with this object.
     */
    @CheckReturnValue
    @Nonnull
    Weeb4J getApi();

    /**
     * Sets this manager's setting cache.
     *
     * @param cache Cache to set.
     */
    void setSettingCache(@Nullable SettingCache cache);

    /**
     * Returns this manager's setting cache.
     *
     * @return This manager's setting cache.
     */
    SettingCache getSettingCache();

    /**
     * Gets a setting from the API.
     *
     * @param type The setting type.
     * @param id The setting ID.
     *
     * @return The setting, or null if it doesn't exist.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<Setting> getSetting(@Nonnull String type, @Nonnull String id);

    /**
     * Saves a setting to the API.
     *
     * @param type The setting type.
     * @param id The setting ID.
     * @param data Data to save.
     *
     * @return The setting after saving.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<Setting> saveSetting(@Nonnull String type, @Nonnull String id, @Nonnull JSONObject data);

    /**
     * Deletes a setting from the API.
     *
     * @param type The setting type.
     * @param id The setting ID.
     *
     * @return The old setting, or null if it didn't exist.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<Setting> deleteSetting(@Nonnull String type, @Nonnull String id);

    /**
     * Gets a sub setting from the API.
     *
     * @param parentType The parent type.
     * @param parentId The parent ID.
     * @param type The setting type.
     * @param id The setting id.
     *
     * @return The setting, or null if it doesn't exist.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<Setting> getSubSetting(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id);

    /**
     * Saves a sub setting to the API.
     *
     * @param parentType The parent type.
     * @param parentId The parent ID.
     * @param type The setting type.
     * @param id The setting id.
     * @param data Data to save.
     *
     * @return The setting after saving.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<Setting> saveSubSetting(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id, @Nonnull JSONObject data);

    /**
     * Deletes a sub setting from the API.
     *
     * @param parentType The parent type.
     * @param parentId The parent ID.
     * @param type The setting type.
     * @param id The setting id.
     *
     * @return The old setting, or null if it didn't exist.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<Setting> deleteSubSetting(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id);

    /**
     * Lists sub setting of a given parent by type.
     *
     * @param parentType The parent type.
     * @param parentId The parent ID.
     * @param type The setting type.
     *
     * @return The ids of the sub settings.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<List<String>> listSubSettings(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type);
}
