package com.github.natanbc.weeb4j;

import com.github.natanbc.reliqua.Reliqua;
import com.github.natanbc.reliqua.limiter.RateLimiter;
import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.reliqua.request.RequestContext;
import com.github.natanbc.reliqua.request.RequestException;
import com.github.natanbc.weeb4j.image.FileType;
import com.github.natanbc.weeb4j.image.HiddenMode;
import com.github.natanbc.weeb4j.image.Image;
import com.github.natanbc.weeb4j.image.NsfwFilter;
import com.github.natanbc.weeb4j.image.PreviewImage;
import com.github.natanbc.weeb4j.image.PreviewMode;
import com.github.natanbc.weeb4j.util.QueryStringBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Weeb4J extends Reliqua {
    public static final String VERSION_MAJOR = "@VERSION_MAJOR@";
    public static final String VERSION_MINOR = "@VERSION_MINOR@";
    public static final String VERSION_REVISION = "@VERSION_REVISION@";
    @SuppressWarnings("ConstantConditions")
    public static final String VERSION = VERSION_MAJOR.startsWith("@") ? "dev" : String.format("%s.%s.%s", VERSION_MAJOR, VERSION_MINOR, VERSION_REVISION);

    private final String apiBase;
    private final String token;
    private final String userAgent;

    Weeb4J(RateLimiter limiter, OkHttpClient client, boolean trackCallSites, Environment environment, String token, String userAgent) {
        super(limiter, client, trackCallSites);
        this.apiBase = environment.getApiBase();
        this.token = token;
        this.userAgent = userAgent;
    }

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
    public PendingRequest<List<String>> getImageTags(@Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw) {
        QueryStringBuilder qsb = new QueryStringBuilder()
                .append(apiBase + "/images/tags");
        if(hidden != null) {
            hidden.appendTo(qsb);
        }
        if(nsfw != null) {
            nsfw.appendTo(qsb);
        }
        return createRequest("/images/tags", createRequest(qsb.build()), 200, response->{
            if(response == null) throw new RuntimeException("Response should never be null");
            JSONObject json = new JSONObject(response.string());
            JSONArray types = json.getJSONArray("tags");
            List<String> list = new ArrayList<>(types.length());
            for(int i = 0, j = types.length(); i < j; i++) {
                list.add(types.getString(i));
            }
            return Collections.unmodifiableList(list);
        }, Weeb4J::handleError);
    }

    /**
     * Retrieve tags matching the given filters.
     *
     * @param hidden Filter for hidden tags.
     *
     * @return List of tags matching the specified filters.
     */
    @CheckReturnValue
    @Nonnull
    public PendingRequest<List<String>> getImageTags(@Nullable HiddenMode hidden) {
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
    public PendingRequest<List<String>> getImageTags(@Nullable NsfwFilter nsfw) {
        return getImageTags(null, nsfw);
    }

    /**
     * Retrieve tags from the API.
     *
     * @return List of tags available.
     */
    @CheckReturnValue
    @Nonnull
    public PendingRequest<List<String>> getImageTags() {
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
    public PendingRequest<PreviewImage> getImageTypes(@Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw, @Nullable PreviewMode preview) {
        QueryStringBuilder qsb = new QueryStringBuilder()
                .append(apiBase + "/images/types");
        if(hidden != null) {
            hidden.appendTo(qsb);
        }
        if(nsfw != null) {
            nsfw.appendTo(qsb);
        }
        if(preview != null) {
            preview.appendTo(qsb);
        }
        return createRequest("/images/types", createRequest(qsb.build()), 200, response->{
            if(response == null) throw new RuntimeException("Response should never be null");
            JSONObject json = new JSONObject(response.string());
            return PreviewImage.fromJSON(Weeb4J.this, json);
        }, Weeb4J::handleError);
    }

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
    public PendingRequest<PreviewImage> getImageTypes(@Nullable HiddenMode hidden, NsfwFilter nsfw) {
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
    public PendingRequest<PreviewImage> getImageTypes(@Nullable HiddenMode hidden, PreviewMode preview) {
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
    public PendingRequest<PreviewImage> getImageTypes(@Nullable NsfwFilter nsfw, PreviewMode preview) {
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
    public PendingRequest<PreviewImage> getImageTypes(@Nullable HiddenMode hidden) {
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
    public PendingRequest<PreviewImage> getImageTypes(@Nullable NsfwFilter nsfw) {
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
    public PendingRequest<PreviewImage> getImageTypes(@Nullable PreviewMode preview) {
        return getImageTypes(null, null, preview);
    }

    /**
     * List image types in the API.
     *
     * @return Image types available.
     */
    @CheckReturnValue
    @Nonnull
    public PendingRequest<PreviewImage> getImageTypes() {
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
    public PendingRequest<Image> getRandomImage(@Nullable String type, @Nullable List<String> tags, @Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw, @Nullable FileType fileType) {
        if(type == null && (tags == null || tags.isEmpty())) {
            throw new IllegalArgumentException("Either type or tags must be present");
        }

        QueryStringBuilder qsb = new QueryStringBuilder()
                .append(apiBase + "/images/random");
        if(type != null) {
            qsb.append("type", type);
        }
        if(tags != null && tags.size() > 0) {
            qsb.append("tags", tags);
        }
        if(hidden != null) {
            hidden.appendTo(qsb);
        }
        if(nsfw != null) {
            nsfw.appendTo(qsb);
        }
        if(fileType != null) {
            if(fileType == FileType.UNKNOWN) {
                throw new IllegalArgumentException("UNKNOWN file type may not be used in requests");
            }
            fileType.appendTo(qsb);
        }
        return createRequest("/images/random", createRequest(qsb.build()), 200, response->{
            if(response == null) throw new RuntimeException("Response should never be null");
            JSONObject json = new JSONObject(response.string());
            return Image.fromJSON(Weeb4J.this, json);
        }, Weeb4J::handleError);
    }

    /**
     * Get an image's info from the ID.
     *
     * @param id The image id.
     *
     * @return The image info. Returns null if the image doesn't exist.
     */
    public PendingRequest<Image> getImageById(String id) {
        return createRequest("/images/" + id, createRequest(apiBase + "/images/" + id), 200, response->{
            if(response == null) throw new RuntimeException("Response should never be null");
            JSONObject json = new JSONObject(response.string());
            return Image.fromJSON(Weeb4J.this, json);
        }, Weeb4J::handleError);
    }

    private static <T> void handleError(RequestContext<T> context) throws IOException {
        Response response = context.getResponse();
        ResponseBody body = response.body();
        if(body == null) {
            context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code() + " (No body)", context.getCallStack()));
            return;
        }
        switch(context.getResponse().code()) {
            case 403:
                context.getErrorConsumer().accept(new MissingScopeException(new JSONObject(
                        body.string()
                ).getString("message"), context.getCallStack()));
                break;
            case 404:
                context.getSuccessConsumer().accept(null);
                break;
            default:
                JSONObject json = null;
                try {
                    json = new JSONObject(body.string());
                } catch(JSONException ignored) {}
                if(json != null) {
                    context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code() + ": " + json.getString("message"), context.getCallStack()));
                } else {
                    context.getErrorConsumer().accept(new RequestException("Unexpected status code " + response.code(), context.getCallStack()));
                }
        }
    }

    @CheckReturnValue
    @Nonnull
    private Request.Builder createRequest(@Nonnull String url) {
        return new Request.Builder()
                .header("Authorization", token)
                .header("User-Agent", userAgent)
                .url(url)
                .get();
    }

    public static class Builder {
        private RateLimiter limiter;
        private OkHttpClient client;
        private String token;
        private String userAgent;
        private boolean trackCallSites;
        private Environment environment;

        @CheckReturnValue
        @Nonnull
        public Builder setRateLimiter(@Nullable RateLimiter limiter) {
            this.limiter = limiter;
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
            this.token = type.format(token);
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Builder setUserAgent(@Nullable String userAgent) {
            this.userAgent = userAgent;
            return this;
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
        public Weeb4J build() {
            if(token == null) throw new IllegalStateException("Token not set");
            return new Weeb4J(
                    limiter,
                    client == null ? new OkHttpClient() : client,
                    trackCallSites,
                    environment == null ? Environment.PRODUCTION : environment,
                    token,
                    userAgent == null ? "Weeb4J/" + VERSION + "/" + (environment == null ? "production" : environment.name().toLowerCase()) : userAgent
            );
        }
    }
}
