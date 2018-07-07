package com.github.natanbc.weeb4j.image;

import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.weeb4j.Weeb4J;
import com.github.natanbc.weeb4j.util.IOUtils;
import com.github.natanbc.weeb4j.util.InputStreamFunction;
import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class PreviewImage {
    private final Weeb4J api;
    private final String url;
    private final String id;
    private final FileType fileType;
    private final String baseType;
    private final String type;

    private PreviewImage(Weeb4J api, String url, String id, FileType fileType, String baseType, String type) {
        this.api = api;
        this.url = url;
        this.id = id;
        this.fileType = fileType;
        this.baseType = baseType;
        this.type = type;
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
     * @param <T> Type returned by the mapper.
     *
     * @return A request for this image's bytes.
     */
    public <T> PendingRequest<T> download(InputStreamFunction<T> function) {
        return api.download(url, function);
    }

    @Nonnull
    @CheckReturnValue
    public static PreviewImage fromJSON(@Nonnull Weeb4J api, @Nonnull JSONObject object) {
        return new PreviewImage(
                api,
                object.getString("url"),
                object.getString("id"),
                FileType.fromString(object.getString("fileType")),
                object.getString("baseType"),
                object.getString("type")
        );
    }
}
