package com.github.natanbc.weeb4j.image;

import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.weeb4j.Weeb4J;
import com.github.natanbc.weeb4j.util.IOUtils;
import com.github.natanbc.weeb4j.util.InputStreamFunction;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class Image {
    private final Weeb4J api;
    private final String id;
    private final String type;
    private final String baseType;
    private final FileType fileType;
    private final String mimeType;
    private final String account;
    private final boolean hidden;
    private final boolean nsfw;
    private final List<Tag> tags;
    private final String sourceUrl;
    private final String url;

    private Image(Weeb4J api, String id, String type, String baseType, FileType fileType, String mimeType, String account, boolean hidden, boolean nsfw, List<Tag> tags, String sourceUrl, String url) {
        this.api = api;
        this.id = id;
        this.type = type;
        this.baseType = baseType;
        this.fileType = fileType;
        this.mimeType = mimeType;
        this.account = account;
        this.hidden = hidden;
        this.nsfw = nsfw;
        this.tags = tags;
        this.sourceUrl = sourceUrl;
        this.url = url;
    }

    /**
     * Returns the internal document ID of this image.
     *
     * @return this image's ID.
     */
    @Nonnull
    @CheckReturnValue
    public String getId() {
        return id;
    }

    /**
     * Returns the type of this image.
     *
     * @return this image's type.
     */
    @Nonnull
    @CheckReturnValue
    public String getType() {
        return type;
    }

    /**
     * Returns the category-like type of this image.
     *
     * @return this image's base type.
     */
    @Nonnull
    @CheckReturnValue
    public String getBaseType() {
        return baseType;
    }

    /**
     * Returns the file type of this image.
     *
     * @return this image's file type.
     */
    @Nonnull
    @CheckReturnValue
    public FileType getFileType() {
        return fileType;
    }

    /**
     * Returns the mime type of this image.
     *
     * @return this image's mime type.
     */
    @Nonnull
    @CheckReturnValue
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Returns the ID of the API account that uploaded this image.
     *
     * @return this image's uploader's ID.
     */
    @Nonnull
    @CheckReturnValue
    public String getAccount() {
        return account;
    }

    /**
     * Returns true if this image is hidden. Hidden images are private and only available to the owner.
     *
     * @return true if this image is hidden.
     */
    @CheckReturnValue
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Returns true if this image is NSFW (not safe for work).
     *
     * @return true if this image is NSFW.
     */
    @CheckReturnValue
    public boolean isNsfw() {
        return nsfw;
    }

    /**
     * Return this image's tags.
     *
     * @return this image's tags.
     */
    @Nonnull
    @CheckReturnValue
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Returns the source URL of this image.
     *
     * @return this image's source URL.
     */
    @Nullable
    @CheckReturnValue
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * Returns the URL of this image.
     *
     * @return this image's URL.
     */
    @Nonnull
    @CheckReturnValue
    public String getUrl() {
        return url;
    }

    /**
     * Downloads this image and returns it's bytes.
     *
     * @return A request for this image's bytes.
     */
    @Nonnull
    @CheckReturnValue
    public PendingRequest<byte[]> download() {
        return download(IOUtils.READ_FULLY);
    }

    /**
     * Applies a given function to an InputStream of this image's bytes, returning the result.
     *
     * @param function Mapper to convert the InputStream to another form of data. <strong>The input stream is closed after the mapper returns.</strong>
     *
     * @return A request for this image's bytes.
     */
    public <T> PendingRequest<T> download(InputStreamFunction<T> function) {
        return api.download(url, function);
    }

    @Nonnull
    @CheckReturnValue
    public static Image fromJSON(@Nonnull Weeb4J api, @Nonnull JSONObject object) {
        JSONArray tagsRaw = object.getJSONArray("tags");
        List<Tag> tags = new ArrayList<>(tagsRaw.length());
        for(int i = 0, j = tagsRaw.length(); i < j; i++) {
            tags.add(Tag.fromJSON(tagsRaw.getJSONObject(i)));
        }
        return new Image(
                api,
                object.getString("id"),
                object.getString("type"),
                object.getString("baseType"),
                FileType.fromString(object.getString("fileType")),
                object.getString("mimeType"),
                object.getString("account"),
                object.getBoolean("hidden"),
                object.getBoolean("nsfw"),
                Collections.unmodifiableList(tags),
                object.optString("source", null),
                object.getString("url")
        );
    }
}
