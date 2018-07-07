package com.github.natanbc.weeb4j.image;

import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.weeb4j.Weeb4J;
import com.github.natanbc.weeb4j.util.InputStreamFunction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused")
public interface ImageProvider {
    /**
     * Returns the Weeb4J instance associated with this object.
     *
     * @return The Weeb4J instance associated with this object.
     */
    @CheckReturnValue
    @Nonnull
    Weeb4J getApi();

    /**
     * Returns the currently used image cache for this provider.
     * If no implementation was specified, a {@link ImageCache#noop() noop}
     * instance is returned.
     *
     * @return The currently used image cache.
     */
    @CheckReturnValue
    @Nonnull
    ImageCache getImageCache();

    /**
     * Sets the image cache for this provider. If null, a {@link ImageCache#noop() noop}
     * instance is used.
     *
     * @param cache Cache to use.
     */
    void setImageCache(@Nullable ImageCache cache);

    /**
     * Retrieve tags matching the given filters.
     *
     * @param hidden Filter for hidden tags.
     * @param nsfw Filter for NSFW (not safe for work) tags.
     *
     * @return List of tags matching the specified filters.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<List<String>> getImageTags(@Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw);

    /**
     * Retrieve tags matching the given filters.
     *
     * @param hidden Filter for hidden tags.
     *
     * @return List of tags matching the specified filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<List<String>> getImageTags(@Nullable HiddenMode hidden) {
        return getImageTags(hidden, null);
    }

    /**
     * Retrieve tags matching the given filters.
     *
     * @param nsfw Filter for NSFW (not safe for work) tags.
     *
     * @return List of tags matching the specified filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<List<String>> getImageTags(@Nullable NsfwFilter nsfw) {
        return getImageTags(null, nsfw);
    }

    /**
     * Retrieve tags from the API.
     *
     * @return List of tags available.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<List<String>> getImageTags() {
        return getImageTags(null, null);
    }

    /**
     * List image types and optionally previews for each of them.
     *
     * @param hidden Filter for hidden types.
     * @param nsfw Filter for NSFW (not safe for work) types.
     * @param preview Whether or not previews should be included.
     *
     * @return Image types matching the filters and optional previews for them.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<ImageTypes> getImageTypes(@Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw, @Nullable PreviewMode preview);

    /**
     * List image types and optionally previews for each of them.
     *
     * @param hidden Filter for hidden types.
     * @param nsfw Filter for NSFW (not safe for work) types.
     *
     * @return Image types matching the filters and optional previews for them.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<ImageTypes> getImageTypes(@Nullable HiddenMode hidden, NsfwFilter nsfw) {
        return getImageTypes(hidden, nsfw, null);
    }

    /**
     * List image types and optionally previews for each of them.
     *
     * @param hidden Filter for hidden types.
     * @param preview Whether or not previews should be included.
     *
     * @return Image types matching the filters and optional previews for them.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<ImageTypes> getImageTypes(@Nullable HiddenMode hidden, PreviewMode preview) {
        return getImageTypes(hidden, null, preview);
    }

    /**
     * List image types and optionally previews for each of them.
     *
     * @param nsfw Filter for NSFW (not safe for work) types.
     * @param preview Whether or not previews should be included.
     *
     * @return Image types matching the filters and optional previews for them.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<ImageTypes> getImageTypes(@Nullable NsfwFilter nsfw, PreviewMode preview) {
        return getImageTypes(null, nsfw, preview);
    }

    /**
     * List image types and optionally previews for each of them.
     *
     * @param hidden Filter for hidden types.
     *
     * @return Image types matching the filters and optional previews for them.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<ImageTypes> getImageTypes(@Nullable HiddenMode hidden) {
        return getImageTypes(hidden, null, null);
    }

    /**
     * List image types and optionally previews for each of them.
     *
     * @param nsfw Filter for NSFW (not safe for work) types.
     *
     * @return Image types matching the filters and optional previews for them.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<ImageTypes> getImageTypes(@Nullable NsfwFilter nsfw) {
        return getImageTypes(null, nsfw, null);
    }

    /**
     * List image types and optionally previews for each of them.
     *
     * @param preview Whether or not previews should be included.
     *
     * @return Image types matching the filters and optional previews for them.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<ImageTypes> getImageTypes(@Nullable PreviewMode preview) {
        return getImageTypes(null, null, preview);
    }

    /**
     * List image types in the API.
     *
     * @return Image types available.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<ImageTypes> getImageTypes() {
        return getImageTypes(null, null, null);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param type Image type.
     * @param tags Image tags.
     * @param hidden Filter for hidden images.
     * @param nsfw Filter for NSFW (not safe for work) images.
     * @param fileType Filter for type of file (JPG, PNG, GIF).
     *
     * @return A random image matching the filters.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<Image> getRandomImage(@Nullable String type, @Nullable List<String> tags, @Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw, @Nullable FileType fileType);

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param type Image type.
     * @param tags Image tags.
     * @param hidden Filter for hidden images.
     * @param nsfw Filter for NSFW (not safe for work) images.
     *
     * @return A random image matching the filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<Image> getRandomImage(@Nullable String type, @Nullable List<String> tags, @Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw) {
        return getRandomImage(type, tags, hidden, nsfw, null);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param type Image type.
     * @param tags Image tags.
     * @param hidden Filter for hidden images.
     *
     * @return A random image matching the filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<Image> getRandomImage(@Nullable String type, @Nullable List<String> tags, @Nullable HiddenMode hidden) {
        return getRandomImage(type, tags, hidden, null, null);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param type Image type.
     * @param hidden Filter for hidden images.
     * @param nsfw Filter for NSFW (not safe for work) images.
     * @param fileType Filter for type of file (JPG, PNG, GIF).
     *
     * @return A random image matching the filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<Image> getRandomImage(@Nullable String type, @Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw, @Nullable FileType fileType) {
        return getRandomImage(type, null, hidden, nsfw, fileType);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param type Image type.
     * @param hidden Filter for hidden images.
     * @param nsfw Filter for NSFW (not safe for work) images.
     *
     * @return A random image matching the filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<Image> getRandomImage(@Nullable String type, @Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw) {
        return getRandomImage(type, null, hidden, nsfw, null);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param type Image type.
     * @param hidden Filter for hidden images.
     *
     * @return A random image matching the filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<Image> getRandomImage(@Nullable String type,  @Nullable HiddenMode hidden) {
        return getRandomImage(type, null, hidden, null, null);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param tags Image tags.
     * @param hidden Filter for hidden images.
     * @param nsfw Filter for NSFW (not safe for work) images.
     * @param fileType Filter for type of file (JPG, PNG, GIF).
     *
     * @return A random image matching the filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<Image> getRandomImage(@Nullable List<String> tags, @Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw, @Nullable FileType fileType) {
        return getRandomImage(null, tags, hidden, nsfw, fileType);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param tags Image tags.
     * @param hidden Filter for hidden images.
     * @param nsfw Filter for NSFW (not safe for work) images.
     *
     * @return A random image matching the filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<Image> getRandomImage(@Nullable List<String> tags, @Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw) {
        return getRandomImage(null, tags, hidden, nsfw, null);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param tags Image tags.
     * @param hidden Filter for hidden images.
     *
     * @return A random image matching the filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<Image> getRandomImage(@Nullable List<String> tags, @Nullable HiddenMode hidden) {
        return getRandomImage(null, tags, hidden, null, null);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param type Image type.
     * @param tags Image tags.
     *
     * @return A random image matching the filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<Image> getRandomImage(@Nullable String type, @Nullable List<String> tags) {
        return getRandomImage(type, tags, null, null, null);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param type Image type.
     *
     * @return A random image matching the filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<Image> getRandomImage(@Nullable String type) {
        return getRandomImage(type, null, null, null, null);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param tags Image tags.
     *
     * @return A random image matching the filters.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<Image> getRandomImage(@Nullable List<String> tags) {
        return getRandomImage(null, tags, null, null, null);
    }

    /**
     * Get an image's info from the ID.
     *
     * @param id The image id.
     *
     * @return The image info. Returns null if the image doesn't exist.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<Image> getImageById(@Nonnull String id);

    /**
     * Downloads a given image.
     *
     * @param image Image to download.
     * @param mapper Maps the download input stream
     * @param <T> Type returned by the mapper
     *
     * @return The downloaded data.
     */
    @CheckReturnValue
    @Nonnull
    <T> PendingRequest<T> download(Image image, InputStreamFunction<T> mapper);
}
