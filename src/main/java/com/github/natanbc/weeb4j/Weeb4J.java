package com.github.natanbc.weeb4j;

import com.github.natanbc.reliqua.Reliqua;
import com.github.natanbc.reliqua.limiter.RateLimiter;
import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.weeb4j.image.FileType;
import com.github.natanbc.weeb4j.image.HiddenMode;
import com.github.natanbc.weeb4j.image.Image;
import com.github.natanbc.weeb4j.image.NsfwFilter;
import com.github.natanbc.weeb4j.util.QueryStringBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @CheckReturnValue
    @Nonnull
    public PendingRequest<List<String>> getImageTags(@Nullable HiddenMode hidden) {
        QueryStringBuilder qsb = new QueryStringBuilder()
                .append(apiBase + "/images/tags");
        if(hidden != null) {
            hidden.appendTo(qsb);
        }
        return createRequest("/images/tags", createRequest(qsb.build()), response->{
            JSONObject json = new JSONObject(response.string());
            if(json.getInt("status") == 403) {
                throw new MissingScopeException(json.getString("message"));
            }
            JSONArray types = json.getJSONArray("types");
            List<String> list = new ArrayList<>(types.length());
            for(int i = 0, j = types.length(); i < j; i++) {
                list.add(types.getString(i));
            }
            return Collections.unmodifiableList(list);
        });
    }

    @CheckReturnValue
    @Nonnull
    public PendingRequest<List<String>> getImageTags() {
        return getImageTags(null);
    }

    @CheckReturnValue
    @Nonnull
    public PendingRequest<List<String>> getImageTypes(@Nullable HiddenMode hidden) {
        QueryStringBuilder qsb = new QueryStringBuilder()
                .append(apiBase + "/images/types");
        if(hidden != null) {
            hidden.appendTo(qsb);
        }
        return createRequest("/images/types", createRequest(qsb.build()), response->{
            JSONObject json = new JSONObject(response.string());
            if(json.getInt("status") == 403) {
                throw new MissingScopeException(json.getString("message"));
            }
            JSONArray types = json.getJSONArray("types");
            List<String> list = new ArrayList<>(types.length());
            for(int i = 0, j = types.length(); i < j; i++) {
                list.add(types.getString(i));
            }
            return Collections.unmodifiableList(list);
        });
    }

    @CheckReturnValue
    @Nonnull
    public PendingRequest<List<String>> getImageTypes() {
        return getImageTypes(null);
    }

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
        return createRequest("/images/random", createRequest(qsb.build()), response->{
            JSONObject json = new JSONObject(response.string());
            if(json.getInt("status") == 403) {
                throw new MissingScopeException(json.getString("message"));
            }
            return Image.fromJSON(Weeb4J.this, json);
        });
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
