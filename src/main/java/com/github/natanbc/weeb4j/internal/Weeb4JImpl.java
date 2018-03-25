package com.github.natanbc.weeb4j.internal;

import com.github.natanbc.reliqua.Reliqua;
import com.github.natanbc.reliqua.limiter.factory.RateLimiterFactory;
import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.reliqua.util.StatusCodeValidator;
import com.github.natanbc.weeb4j.Environment;
import com.github.natanbc.weeb4j.TokenInfo;
import com.github.natanbc.weeb4j.TokenType;
import com.github.natanbc.weeb4j.Weeb4J;
import com.github.natanbc.weeb4j.image.FileType;
import com.github.natanbc.weeb4j.image.HiddenMode;
import com.github.natanbc.weeb4j.image.Image;
import com.github.natanbc.weeb4j.image.ImageTypes;
import com.github.natanbc.weeb4j.image.NsfwFilter;
import com.github.natanbc.weeb4j.image.PreviewMode;
import com.github.natanbc.weeb4j.imagegen.DiscordStatus;
import com.github.natanbc.weeb4j.imagegen.LicenseData;
import com.github.natanbc.weeb4j.util.InputStreamFunction;
import com.github.natanbc.weeb4j.util.QueryStringBuilder;
import com.github.natanbc.weeb4j.util.RequestUtils;
import com.github.natanbc.weeb4j.util.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Weeb4JImpl extends Reliqua implements Weeb4J {
    private final Environment environment;
    private final String apiBase;
    private final TokenType type;
    private final String token;
    private final String userAgent;

    public Weeb4JImpl(OkHttpClient client, RateLimiterFactory factory, boolean trackCallSites, Environment environment, TokenType type, String token, String userAgent) {
        super(client, factory, trackCallSites);
        this.environment = environment;
        this.apiBase = environment.getApiBase();
        this.type = type;
        this.token = token;
        this.userAgent = userAgent;
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public String getToken() {
        return token;
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public TokenType getTokenType() {
        return type;
    }

    @Nonnull
    @Override
    public PendingRequest<TokenInfo> getTokenInfo(@Nonnull String token) {
        Objects.requireNonNull(token, "Token may not be null");
        return createRequest(newRequestBuilder(apiBase + "/accounts/validate/" + token + "?wolkeToken=1"))
                .setRateLimiter(getRateLimiter("/accounts/validate"))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .build(response->TokenInfo.fromJSON(RequestUtils.toJSONObject(response), token), RequestUtils::handleError);
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public PendingRequest<List<String>> getImageTags(@Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw) {
        QueryStringBuilder qsb = new QueryStringBuilder()
                .append(apiBase + "/images/tags");
        if(hidden != null) {
            hidden.appendTo(qsb);
        }
        if(nsfw != null) {
            nsfw.appendTo(qsb);
        }
        return createRequest(newRequestBuilder(qsb.build()))
                .setRateLimiter(getRateLimiter("/images/tags"))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .build(response->{
                    JSONObject json = RequestUtils.toJSONObject(response);
                    JSONArray types = json.getJSONArray("tags");
                    List<String> list = new ArrayList<>(types.length());
                    for(int i = 0, j = types.length(); i < j; i++) {
                        list.add(types.getString(i));
                    }
                    return Collections.unmodifiableList(list);
                }, RequestUtils::handleError);
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public PendingRequest<ImageTypes> getImageTypes(@Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw, @Nullable PreviewMode preview) {
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
        return createRequest(newRequestBuilder(qsb.build()))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .setRateLimiter(getRateLimiter("/images/types"))
                .build(response->ImageTypes.fromJSON(this, RequestUtils.toJSONObject(response)), RequestUtils::handleError);
    }

    @CheckReturnValue
    @Nonnull
    @Override
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
        return createRequest(newRequestBuilder(qsb.build()))
                .setRateLimiter(getRateLimiter("/images/random"))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .build(response->Image.fromJSON(this, RequestUtils.toJSONObject(response)), RequestUtils::handleError);
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public PendingRequest<Image> getImageById(@Nonnull String id) {
        return createRequest(newRequestBuilder(apiBase + "/images/info/" + id))
                .setRateLimiter(getRateLimiter("/images/info"))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .build(response->Image.fromJSON(this, RequestUtils.toJSONObject(response)), RequestUtils::handleError);
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public <T> PendingRequest<T> generateAwoo(@Nullable Color faceColor, @Nullable Color hairColor, @Nonnull InputStreamFunction<T> mapper) {
        Objects.requireNonNull(mapper, "Mapper may not be null");
        QueryStringBuilder qsb = new QueryStringBuilder()
                .append(apiBase + "/auto-image/generate")
                .append("type", "awooo");
        if(faceColor != null) {
            qsb.append("face", String.format("%06X", faceColor.getRGB() & 0xFFFFFF));
        }
        if(hairColor != null) {
            qsb.append("hair", String.format("%06X", hairColor.getRGB() & 0xFFFFFF));
        }
        return createRequest(newRequestBuilder(qsb.build()))
                .setRateLimiter(getRateLimiter("/auto-image/generate"))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .build(response->mapper.accept(RequestUtils.getInputStream(response)), RequestUtils::handleError);
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public <T> PendingRequest<T> generateEyes(@Nonnull InputStreamFunction<T> mapper) {
        Objects.requireNonNull(mapper, "Mapper may not be null");
        QueryStringBuilder qsb = new QueryStringBuilder()
                .append(apiBase + "/auto-image/generate")
                .append("type", "eyes");
        return createRequest(newRequestBuilder(qsb.build()))
                .setRateLimiter(getRateLimiter("/auto-image/generate"))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .build(response->mapper.accept(RequestUtils.getInputStream(response)), RequestUtils::handleError);
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public <T> PendingRequest<T> generateWon(@Nonnull InputStreamFunction<T> mapper) {
        Objects.requireNonNull(mapper, "Mapper may not be null");
        QueryStringBuilder qsb = new QueryStringBuilder()
                .append(apiBase + "/auto-image/generate")
                .append("type", "won");
        return createRequest(newRequestBuilder(qsb.build()))
                .setRateLimiter(getRateLimiter("/auto-image/generate"))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .build(response->mapper.accept(RequestUtils.getInputStream(response)), RequestUtils::handleError);
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public <T> PendingRequest<T> generateStatus(@Nullable DiscordStatus status, @Nonnull String avatarUrl, @Nonnull InputStreamFunction<T> mapper) {
        Objects.requireNonNull(avatarUrl, "Avatar url may not be null");
        Objects.requireNonNull(mapper, "Mapper may not be null");
        Utils.validUrl(avatarUrl);
        QueryStringBuilder qsb = new QueryStringBuilder()
                .append(apiBase + "/auto-image/discord-status")
                .append("avatar", avatarUrl);
        if(status != null) {
            status.appendTo(qsb);
        }
        return createRequest(newRequestBuilder(qsb.build()))
                .setRateLimiter(getRateLimiter("/auto-image/discord-status"))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .build(response->mapper.accept(RequestUtils.getInputStream(response)), RequestUtils::handleError);
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public <T> PendingRequest<T> generateLicense(@Nonnull LicenseData data, @Nonnull InputStreamFunction<T> mapper) {
        Objects.requireNonNull(data, "Data may not be null");
        Objects.requireNonNull(mapper, "Mapper may not be null");

        JSONObject body = new JSONObject()
                .put("title", data.getTitle())
                .put("avatar", data.getAvatar());
        List<String> badges = data.getBadges();
        if(badges != null && !badges.isEmpty()) {
            JSONArray array = new JSONArray();
            for(String s : badges) {
                array.put(s);
            }
            body.put("badges", badges);
        }
        List<String> widgets = data.getWidgets();
        if(widgets != null && !widgets.isEmpty()) {
            JSONArray array = new JSONArray();
            for(String s : widgets) {
                array.put(s);
            }
            body.put("widgets", widgets);
        }

        return createRequest(newRequestBuilder(apiBase + "/auto-image/license").post(RequestUtils.toBody(body)))
                .setRateLimiter(getRateLimiter("/auto-image/license"))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .build(response->mapper.accept(RequestUtils.getInputStream(response)), RequestUtils::handleError);
    }

    @Nonnull
    @Override
    public <T> PendingRequest<T> generateWaifuInsult(@Nonnull String avatarUrl, @Nonnull InputStreamFunction<T> mapper) {
        Objects.requireNonNull(avatarUrl, "Avatar url may not be null");
        Objects.requireNonNull(mapper, "Mapper may not be null");
        Utils.validUrl(avatarUrl);

        JSONObject body = new JSONObject().put("avatar", avatarUrl);

        return createRequest(newRequestBuilder(apiBase + "/auto-image/waifu-insult").post(RequestUtils.toBody(body)))
                .setRateLimiter(getRateLimiter("/auto-image/waifu-insult"))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .build(response->mapper.accept(RequestUtils.getInputStream(response)), RequestUtils::handleError);
    }

    @Nonnull
    @Override
    public <T> PendingRequest<T> generateLoveship(@Nonnull String firstAvatarUrl, @Nonnull String secondAvatarUrl, @Nonnull InputStreamFunction<T> mapper) {
        Objects.requireNonNull(firstAvatarUrl, "First avatar url may not be null");
        Objects.requireNonNull(secondAvatarUrl, "Second avatar url may not be null");
        Objects.requireNonNull(mapper, "Mapper avatar url may not be null");
        Utils.validUrl(firstAvatarUrl);
        Utils.validUrl(secondAvatarUrl);

        JSONObject body = new JSONObject().put("targetOne", firstAvatarUrl).put("targetTwo", secondAvatarUrl);

        return createRequest(newRequestBuilder(apiBase + "/auto-image/love-ship").post(RequestUtils.toBody(body)))
                .setRateLimiter(getRateLimiter("/auto-image/love-ship"))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .build(response->mapper.accept(RequestUtils.getInputStream(response)), RequestUtils::handleError);
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public <T> PendingRequest<T> download(String url, InputStreamFunction<T> function) {
        Objects.requireNonNull(url, "URL may not be null");
        Objects.requireNonNull(function, "Function may not be null");
        return createRequest(newRequestBuilder(url))
                .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                .build(response->function.accept(RequestUtils.getInputStream(response)), RequestUtils::handleError);
    }

    @CheckReturnValue
    @Nonnull
    private Request.Builder newRequestBuilder(@Nonnull String url) {
        return new Request.Builder()
                .header("Authorization", type.format(token))
                .header("User-Agent", userAgent)
                .header("Accept-Encoding", "gzip, deflate") //we can handle gzip data
                .url(url)
                .get();
    }
}
