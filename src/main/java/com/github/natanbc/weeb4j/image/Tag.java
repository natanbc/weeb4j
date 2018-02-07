package com.github.natanbc.weeb4j.image;

import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class Tag {
    private final String name;
    private final String user;
    private final boolean hidden;

    private Tag(String name, String user, boolean hidden) {
        this.name = name;
        this.user = user;
        this.hidden = hidden;
    }

    /**
     * Returns this tag's name.
     *
     * @return this tag's name.
     */
    @Nonnull
    @CheckReturnValue
    public String getName() {
        return name;
    }

    /**
     * Returns the ID of the API account that added the tag.
     *
     * @return ID of the tag's creator
     */
    @Nonnull
    @CheckReturnValue
    public String getUser() {
        return user;
    }

    /**
     * True if this tag is hidden. Hidden tags are only available to their creator.
     *
     * @return true if this tag is hidden.
     */
    @CheckReturnValue
    public boolean isHidden() {
        return hidden;
    }

    @Nonnull
    @CheckReturnValue
    static Tag fromJSON(JSONObject object) {
        return new Tag(
                object.getString("name"),
                object.getString("user"),
                object.getBoolean("hidden")
        );
    }
}
