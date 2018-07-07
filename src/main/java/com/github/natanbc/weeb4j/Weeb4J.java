package com.github.natanbc.weeb4j;

import com.github.natanbc.reliqua.limiter.RateLimiter;
import com.github.natanbc.reliqua.limiter.factory.RateLimiterFactory;
import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.weeb4j.image.FileType;
import com.github.natanbc.weeb4j.image.HiddenMode;
import com.github.natanbc.weeb4j.image.Image;
import com.github.natanbc.weeb4j.image.ImageCache;
import com.github.natanbc.weeb4j.image.ImageProvider;
import com.github.natanbc.weeb4j.image.ImageTypes;
import com.github.natanbc.weeb4j.image.NsfwFilter;
import com.github.natanbc.weeb4j.image.PreviewMode;
import com.github.natanbc.weeb4j.imagegen.DiscordStatus;
import com.github.natanbc.weeb4j.imagegen.ImageGenerator;
import com.github.natanbc.weeb4j.imagegen.LicenseData;
import com.github.natanbc.weeb4j.internal.Weeb4JImpl;
import com.github.natanbc.weeb4j.reputation.ReputationManager;
import com.github.natanbc.weeb4j.settings.SettingCache;
import com.github.natanbc.weeb4j.settings.SettingManager;
import com.github.natanbc.weeb4j.util.IOUtils;
import com.github.natanbc.weeb4j.util.InputStreamFunction;
import com.github.natanbc.weeb4j.util.Utils;
import okhttp3.OkHttpClient;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public interface Weeb4J {
    /**
     * Returns the environment provided during creation.
     *
     * @return The provided environment.
     */
    @CheckReturnValue
    @Nonnull
    Environment getEnvironment();

    /**
     * Returns the API token provided during creation.
     *
     * @return The provided API token.
     */
    @CheckReturnValue
    @Nonnull
    String getToken();

    /**
     * Returns the token type provided during creation.
     *
     * @return The provided token type.
     */
    @CheckReturnValue
    @Nonnull
    TokenType getTokenType();

    /**
     * Returns metadata of the provided token.
     *
     * @param token Token to be analyzed.
     *
     * @return The metadata for the given token.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<TokenInfo> getTokenInfo(@Nonnull String token);

    /**
     * Returns metadata of the token provided during creation.
     *
     * @return The metadata for the given token.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<TokenInfo> getTokenInfo() {
        return getTokenInfo(getToken());
    }

    /**
     * Returns an image provider, used to request images from the api.
     *
     * @return An image provider.
     */
    @CheckReturnValue
    @Nonnull
    ImageProvider getImageProvider();

    /**
     * Returns an image generator, used to generate images.
     *
     * @return An image generator.
     */
    @CheckReturnValue
    @Nonnull
    ImageGenerator getImageGenerator();

    /**
     * Returns a reputation manager, used to manage users' reputation.
     *
     * @return A reputation manager.
     */
    @CheckReturnValue
    @Nonnull
    ReputationManager getReputationManager();

    /**
     * Returns a setting manager, used to manage bot settings.
     *
     * @return A setting manager.
     */
    @CheckReturnValue
    @Nonnull
    SettingManager getSettingManager();

    /**
     * Downloads a given url.
     *
     * @param url URL to download.
     * @param mapper Maps the download input stream
     * @param <T> Type returned by the mapper
     *
     * @return The downloaded data.
     */
    @CheckReturnValue
    @Nonnull
    <T> PendingRequest<T> download(String url, InputStreamFunction<T> mapper);

    class Builder {
        private OkHttpClient client;
        private RateLimiterFactory rateLimiterFactory;
        private TokenType tokenType;
        private String token;
        private String botName;
        private String botVersion;
        private String botEnvironment;
        private boolean trackCallSites;
        private Environment environment;
        private Long botId;
        private SettingCache settingCache;
        private ImageCache imageCache;

        @CheckReturnValue
        @Nonnull
        @Deprecated
        public Builder setRateLimiter(RateLimiter limiter) {
            return setRateLimiterFactory(new RateLimiterFactory() {
                @Override
                protected RateLimiter createRateLimiter(String key) {
                    return limiter;
                }
            });
        }

        @CheckReturnValue
        @Nonnull
        public Builder setRateLimiterFactory(@Nullable RateLimiterFactory factory) {
            this.rateLimiterFactory = factory;
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Builder setHttpClient(@Nullable OkHttpClient client) {
            this.client = client;
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Builder setToken(@Nonnull TokenType type, @Nonnull String token) {
            this.tokenType = Objects.requireNonNull(type, "Type may not be null");
            this.token = Objects.requireNonNull(token, "Token may not be null");
            return this;
        }

        @CheckReturnValue
        @Nonnull
        @Deprecated
        public Builder setUserAgent(@Nullable String userAgent) {
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Builder setBotInfo(@Nonnull String botName, @Nonnull String botVersion, @Nullable String botEnvironment) {
            this.botName = Objects.requireNonNull(botName);
            this.botVersion = Objects.requireNonNull(botVersion);
            this.botEnvironment = botEnvironment;
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Builder setBotInfo(@Nonnull String botName, @Nonnull String botVersion) {
            return setBotInfo(botName, botVersion, null);
        }

        @CheckReturnValue
        @Nonnull
        public Builder setCallSiteTrackingEnabled(boolean enabled) {
            this.trackCallSites = enabled;
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Builder setEnvironment(@Nullable Environment environment) {
            this.environment = environment;
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Builder setBotId(long botId) {
            this.botId = botId;
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Builder setSettingCache(@Nullable SettingCache cache) {
            this.settingCache = cache;
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Builder setImageCache(@Nullable ImageCache imageCache) {
            this.imageCache = imageCache;
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Weeb4J build() {
            if(token == null) throw new IllegalStateException("Token not set");
            if(botName == null || botVersion == null) {
                Weeb4JImpl.LOGGER.warn("No bot name or version specified. These values will be required in a future release. Set them with Weeb4J.Builder#setBotInfo");
            }
            if(botEnvironment == null) {
                Weeb4JImpl.LOGGER.info("No bot environment specified, defaulting to 'production'");
            }
            String name = botName == null ? Utils.tryFindMainClass() : botName;
            return new Weeb4JImpl(
                    client == null ? new OkHttpClient() : client,
                    rateLimiterFactory,
                    trackCallSites,
                    environment == null ? Environment.PRODUCTION : environment,
                    tokenType,
                    token,
                                 //<bot name>/<bot version>/<bot environment>; (Weeb4J/<weeb4j version>/<weeb.sh environment>/<weeb4j commit>)
                    String.format("%s/%s/%s; (Weeb4J/%s/%s/%s)",
                            name == null ? "Unknown bot name" : name,
                            botVersion == null ? "Unknown bot version" : botVersion,
                            botEnvironment == null ? "production" : botEnvironment,
                            WeebInfo.VERSION,
                            environment == null ? "production" : environment.name().toLowerCase(),
                            WeebInfo.COMMIT
                    ),
                    botId,
                    settingCache,
                    imageCache
            );
        }
    }

    //deprecated methods

    /**
     * Retrieve tags matching the given filters.
     *
     * @param hidden Filter for hidden tags.
     * @param nsfw Filter for NSFW (not safe for work) tags.
     *
     * @return List of tags matching the specified filters.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<List<String>> getImageTags(@Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw) {
        return getImageProvider().getImageTags(hidden, nsfw);
    }

    /**
     * Retrieve tags matching the given filters.
     *
     * @param hidden Filter for hidden tags.
     *
     * @return List of tags matching the specified filters.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<List<String>> getImageTags(@Nullable HiddenMode hidden) {
        return getImageTags(hidden, null);
    }

    /**
     * Retrieve tags matching the given filters.
     *
     * @param nsfw Filter for NSFW (not safe for work) tags.
     *
     * @return List of tags matching the specified filters.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<List<String>> getImageTags(@Nullable NsfwFilter nsfw) {
        return getImageTags(null, nsfw);
    }

    /**
     * Retrieve tags from the API.
     *
     * @return List of tags available.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
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
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<ImageTypes> getImageTypes(@Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw, @Nullable PreviewMode preview) {
        return getImageProvider().getImageTypes(hidden, nsfw, preview);
    }

    /**
     * List image types and optionally previews for each of them.
     *
     * @param hidden Filter for hidden types.
     * @param nsfw Filter for NSFW (not safe for work) types.
     *
     * @return Image types matching the filters and optional previews for them.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
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
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
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
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<ImageTypes> getImageTypes(@Nullable NsfwFilter nsfw, PreviewMode preview) {
        return getImageTypes(null, nsfw, preview);
    }

    /**
     * List image types and optionally previews for each of them.
     *
     * @param hidden Filter for hidden types.
     *
     * @return Image types matching the filters and optional previews for them.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<ImageTypes> getImageTypes(@Nullable HiddenMode hidden) {
        return getImageTypes(hidden, null, null);
    }

    /**
     * List image types and optionally previews for each of them.
     *
     * @param nsfw Filter for NSFW (not safe for work) types.
     *
     * @return Image types matching the filters and optional previews for them.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<ImageTypes> getImageTypes(@Nullable NsfwFilter nsfw) {
        return getImageTypes(null, nsfw, null);
    }

    /**
     * List image types and optionally previews for each of them.
     *
     * @param preview Whether or not previews should be included.
     *
     * @return Image types matching the filters and optional previews for them.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<ImageTypes> getImageTypes(@Nullable PreviewMode preview) {
        return getImageTypes(null, null, preview);
    }

    /**
     * List image types in the API.
     *
     * @return Image types available.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
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
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<Image> getRandomImage(@Nullable String type, @Nullable List<String> tags, @Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw, @Nullable FileType fileType) {
        return getImageProvider().getRandomImage(type, tags, hidden, nsfw, fileType);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param type Image type.
     * @param tags Image tags.
     * @param hidden Filter for hidden images.
     * @param nsfw Filter for NSFW (not safe for work) images.
     *
     * @return A random image matching the filters.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
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
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
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
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
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
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
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
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
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
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
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
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
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
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
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
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<Image> getRandomImage(@Nullable String type, @Nullable List<String> tags) {
        return getRandomImage(type, tags, null, null, null);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param type Image type.
     *
     * @return A random image matching the filters.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<Image> getRandomImage(@Nullable String type) {
        return getRandomImage(type, null, null, null, null);
    }

    /**
     * Retrieve a random image matching the specified filters.
     *
     * @param tags Image tags.
     *
     * @return A random image matching the filters.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<Image> getRandomImage(@Nullable List<String> tags) {
        return getRandomImage(null, tags, null, null, null);
    }

    /**
     * Get an image's info from the ID.
     *
     * @param id The image id.
     *
     * @return The image info. Returns null if the image doesn't exist.
     *
     * @deprecated Use {@link #getImageProvider()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<Image> getImageById(@Nonnull String id) {
        return getImageProvider().getImageById(id);
    }

    /**
     * Generates an awoo image. Only RGB bits are used, alpha is ignored.
     *
     * @param faceColor Color used for the face. Defaults to 0xFFF0D3.
     * @param hairColor Color used for the hair. Defaults to 0xCC817C.
     * @param mapper Maps the image's input stream.
     * @param <T> Type returned by the mapper.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default <T> PendingRequest<T> generateAwoo(@Nullable Color faceColor, @Nullable Color hairColor, @Nonnull InputStreamFunction<T> mapper) {
        return getImageGenerator().generateAwoo(faceColor, hairColor, mapper);
    }

    /**
     * Generates an awoo image. Only RGB bits are used, alpha is ignored.
     *
     * @param faceColor Color used for the face.
     * @param hairColor Color used for the hair.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<byte[]> generateAwoo(@Nullable Color faceColor, @Nullable Color hairColor) {
        return generateAwoo(faceColor, hairColor, IOUtils.READ_FULLY);
    }

    /**
     * Generates an awoo image. Only RGB bits are used, alpha is ignored.
     *
     * @param mapper Maps the image's input stream.
     * @param <T> Type returned by the mapper.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default <T> PendingRequest<T> generateAwoo(@Nonnull InputStreamFunction<T> mapper) {
        return generateAwoo(null, null, mapper);
    }

    /**
     * Generates an awoo image. Only RGB bits are used, alpha is ignored.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<byte[]> generateAwoo() {
        return generateAwoo(IOUtils.READ_FULLY);
    }

    /**
     * Generates a pair of eyes looking in random directions.
     *
     * @param mapper Maps the image's input stream
     * @param <T> Type returned by the mapper.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default <T> PendingRequest<T> generateEyes(@Nonnull InputStreamFunction<T> mapper) {
        return getImageGenerator().generateEyes(mapper);
    }

    /**
     * Generates a pair of eyes looking in random directions.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<byte[]> generateEyes() {
        return generateEyes(IOUtils.READ_FULLY);
    }

    /**
     * Generates a won image.
     *
     * @param mapper Maps the image's input stream.
     * @param <T> Type returned by the mapper.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default <T> PendingRequest<T> generateWon(@Nonnull InputStreamFunction<T> mapper) {
        return getImageGenerator().generateWon(mapper);
    }

    /**
     * Generates a won image.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<byte[]> generateWon() {
        return generateWon(IOUtils.READ_FULLY);
    }

    /**
     * Generates a discord status for a given avatar.
     *
     * @param status Status to display. Defaults to online.
     * @param avatarUrl Avatar url.
     * @param mapper Maps the image's input stream.
     * @param <T> Type returned by the mapper.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default <T> PendingRequest<T> generateStatus(@Nullable DiscordStatus status, @Nonnull String avatarUrl, @Nonnull InputStreamFunction<T> mapper) {
        return getImageGenerator().generateStatus(status, avatarUrl, mapper);
    }

    /**
     * Generates a discord status for a given avatar.
     *
     * @param avatarUrl Avatar url.
     * @param mapper Maps the image's input stream.
     * @param <T> Type returned by the mapper.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default <T> PendingRequest<T> generateStatus(@Nonnull String avatarUrl, @Nonnull InputStreamFunction<T> mapper) {
        return generateStatus(null, avatarUrl, mapper);
    }

    /**
     * Generates a discord status for a given avatar.
     *
     * @param status Status to display. Defaults to online.
     * @param avatarUrl Avatar url.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<byte[]> generateStatus(@Nullable DiscordStatus status, @Nonnull String avatarUrl) {
        return generateStatus(status, avatarUrl, IOUtils.READ_FULLY);
    }

    /**
     * Generates a discord status for a given avatar.
     *
     * @param avatarUrl Avatar url.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<byte[]> generateStatus(@Nonnull String avatarUrl) {
        return generateStatus(avatarUrl, IOUtils.READ_FULLY);
    }

    /**
     * Generates a license card.
     *
     * @param data Data to show on the card.
     * @param mapper Maps the image's input stream.
     * @param <T> Type returned by the mapper.
     *
     * @return The generated image.
     *
     * @see com.github.natanbc.weeb4j.imagegen.LicenseBuilder
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default <T> PendingRequest<T> generateLicense(@Nonnull LicenseData data, @Nonnull InputStreamFunction<T> mapper) {
        return getImageGenerator().generateLicense(data, mapper);
    }

    /**
     * Generates a license card.
     *
     * @param data Data to show on the card.
     *
     * @return The generated image.
     *
     * @see com.github.natanbc.weeb4j.imagegen.LicenseBuilder
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<byte[]> generateLicense(@Nonnull LicenseData data) {
        return generateLicense(data, IOUtils.READ_FULLY);
    }

    /**
     * Generates a waifu insult.
     *
     * @param avatarUrl Avatar of the waifu to be insulted.
     * @param mapper Maps the image's input stream.
     * @param <T> Type returned by the mapper.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default <T> PendingRequest<T> generateWaifuInsult(@Nonnull String avatarUrl, @Nonnull InputStreamFunction<T> mapper) {
        return getImageGenerator().generateWaifuInsult(avatarUrl, mapper);
    }

    /**
     * Generates a waifu insult.
     *
     * @param avatarUrl Avatar of the waifu to be insulted.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<byte[]> generateWaifuInsult(@Nonnull String avatarUrl) {
        return generateWaifuInsult(avatarUrl, IOUtils.READ_FULLY);
    }

    /**
     * Generates a ship image.
     *
     * @param firstAvatarUrl First avatar of the ship.
     * @param secondAvatarUrl Second avatar of the ship.
     * @param mapper Maps the image's input stream.
     * @param <T> Type returned by the mapper.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default <T> PendingRequest<T> generateLoveship(@Nonnull String firstAvatarUrl, @Nonnull String secondAvatarUrl, @Nonnull InputStreamFunction<T> mapper) {
        return getImageGenerator().generateLoveship(firstAvatarUrl, secondAvatarUrl, mapper);
    }

    /**
     * Generates a ship image.
     *
     * @param firstAvatarUrl First avatar of the ship.
     * @param secondAvatarUrl Second avatar of the ship.
     *
     * @return The generated image.
     *
     * @deprecated Use {@link #getImageGenerator()}.
     */
    @CheckReturnValue
    @Nonnull
    @Deprecated
    default PendingRequest<byte[]> generateLoveship(@Nonnull String firstAvatarUrl, @Nonnull String secondAvatarUrl) {
        return generateLoveship(firstAvatarUrl, secondAvatarUrl, IOUtils.READ_FULLY);
    }
}
