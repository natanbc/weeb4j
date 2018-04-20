package com.github.natanbc.weeb4j.internal;

import com.github.natanbc.reliqua.Reliqua;
import com.github.natanbc.reliqua.limiter.factory.RateLimiterFactory;
import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.reliqua.request.RequestException;
import com.github.natanbc.reliqua.util.StatusCodeValidator;
import com.github.natanbc.weeb4j.Environment;
import com.github.natanbc.weeb4j.TokenInfo;
import com.github.natanbc.weeb4j.TokenType;
import com.github.natanbc.weeb4j.Weeb4J;
import com.github.natanbc.weeb4j.image.FileType;
import com.github.natanbc.weeb4j.image.HiddenMode;
import com.github.natanbc.weeb4j.image.Image;
import com.github.natanbc.weeb4j.image.ImageProvider;
import com.github.natanbc.weeb4j.image.ImageTypes;
import com.github.natanbc.weeb4j.image.NsfwFilter;
import com.github.natanbc.weeb4j.image.PreviewMode;
import com.github.natanbc.weeb4j.imagegen.DiscordStatus;
import com.github.natanbc.weeb4j.imagegen.ImageGenerator;
import com.github.natanbc.weeb4j.imagegen.LicenseData;
import com.github.natanbc.weeb4j.reputation.ReputationManager;
import com.github.natanbc.weeb4j.reputation.ReputationTransferException;
import com.github.natanbc.weeb4j.reputation.Settings;
import com.github.natanbc.weeb4j.reputation.TransferResult;
import com.github.natanbc.weeb4j.reputation.User;
import com.github.natanbc.weeb4j.settings.Setting;
import com.github.natanbc.weeb4j.settings.SettingCache;
import com.github.natanbc.weeb4j.settings.SettingManager;
import com.github.natanbc.weeb4j.util.InputStreamFunction;
import com.github.natanbc.weeb4j.util.QueryStringBuilder;
import com.github.natanbc.weeb4j.util.RequestUtils;
import com.github.natanbc.weeb4j.util.Utils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Weeb4JImpl extends Reliqua implements Weeb4J {
    public static final Logger LOGGER = LoggerFactory.getLogger("Weeb4J");

    private final Environment environment;
    private final String apiBase;
    private final TokenType type;
    private final String token;
    private final String userAgent;
    private final ImageProviderImpl imageProvider;
    private final ImageGeneratorImpl imageGenerator;
    private final ReputationManagerImpl reputationManager;
    private final SettingManager settingManager;

    public Weeb4JImpl(OkHttpClient client, RateLimiterFactory factory, boolean trackCallSites, Environment environment, TokenType type, String token, String userAgent, Long botId, SettingCache settingCache) {
        super(client, factory, trackCallSites);
        this.environment = environment;
        this.apiBase = environment.getApiBase();
        this.type = type;
        this.token = token;
        this.userAgent = userAgent;
        this.imageProvider = new ImageProviderImpl(this);
        this.imageGenerator = new ImageGeneratorImpl(this);
        this.reputationManager = new ReputationManagerImpl(this, botId);
        this.settingManager = new SettingManagerImpl(this, settingCache);
    }

    @Override
    public void setTrackCallSites(boolean trackCallSites) {
        super.setTrackCallSites(trackCallSites);
        imageProvider.setTrackCallSites(trackCallSites);
    }

    @CheckReturnValue
    @Nonnull
    public String getApiBase() {
        return apiBase;
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
    public ImageProvider getImageProvider() {
        return imageProvider;
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public ImageGenerator getImageGenerator() {
        return imageGenerator;
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public ReputationManager getReputationManager() {
        return reputationManager;
    }

    @CheckReturnValue
    @Nonnull
    @Override
    public SettingManager getSettingManager() {
        return settingManager;
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
    public Request.Builder newRequestBuilder(@Nonnull String url) {
        return new Request.Builder()
                .header("Authorization", type.format(token))
                .header("User-Agent", userAgent)
                .header("Accept-Encoding", "gzip, deflate") //we can handle gzip data
                .url(url)
                .get();
    }

    public static abstract class AbstractManager extends Reliqua {
        public final Weeb4JImpl api;

        public AbstractManager(Weeb4JImpl api) {
            super(api.getClient(), api.getRateLimiterFactory(), api.isTrackingCallSites());
            this.api = api;
        }

        @CheckReturnValue
        @Nonnull
        public Weeb4J getApi() {
            return api;
        }
    }

    public static class ImageProviderImpl extends AbstractManager implements ImageProvider {
        public ImageProviderImpl(Weeb4JImpl api) {
            super(api);
        }

        @CheckReturnValue
        @Nonnull
        @Override
        public PendingRequest<List<String>> getImageTags(@Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw) {
            QueryStringBuilder qsb = new QueryStringBuilder()
                    .append(api.getApiBase() + "/images/tags");
            if(hidden != null) {
                hidden.appendTo(qsb);
            }
            if(nsfw != null) {
                nsfw.appendTo(qsb);
            }
            return createRequest(api.newRequestBuilder(qsb.build()))
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
                    .append(api.getApiBase() + "/images/types");
            if(hidden != null) {
                hidden.appendTo(qsb);
            }
            if(nsfw != null) {
                nsfw.appendTo(qsb);
            }
            if(preview != null) {
                preview.appendTo(qsb);
            }
            return createRequest(api.newRequestBuilder(qsb.build()))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .setRateLimiter(getRateLimiter("/images/types"))
                    .build(response->ImageTypes.fromJSON(api, RequestUtils.toJSONObject(response)), RequestUtils::handleError);
        }

        @CheckReturnValue
        @Nonnull
        @Override
        public PendingRequest<Image> getRandomImage(@Nullable String type, @Nullable List<String> tags, @Nullable HiddenMode hidden, @Nullable NsfwFilter nsfw, @Nullable FileType fileType) {
            if(type == null && (tags == null || tags.isEmpty())) {
                throw new IllegalArgumentException("Either type or tags must be present");
            }

            QueryStringBuilder qsb = new QueryStringBuilder()
                    .append(api.getApiBase() + "/images/random");
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
            return createRequest(api.newRequestBuilder(qsb.build()))
                    .setRateLimiter(getRateLimiter("/images/random"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->Image.fromJSON(api, RequestUtils.toJSONObject(response)), RequestUtils::handleError);
        }

        @CheckReturnValue
        @Nonnull
        @Override
        public PendingRequest<Image> getImageById(@Nonnull String id) {
            return createRequest(api.newRequestBuilder(api.getApiBase() + "/images/info/" + id))
                    .setRateLimiter(getRateLimiter("/images/info"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->Image.fromJSON(api, RequestUtils.toJSONObject(response)), RequestUtils::handleError);
        }
    }

    public static class ImageGeneratorImpl extends AbstractManager implements ImageGenerator {
        public ImageGeneratorImpl(Weeb4JImpl api) {
            super(api);
        }

        @CheckReturnValue
        @Nonnull
        @Override
        public <T> PendingRequest<T> generateAwoo(@Nullable Color faceColor, @Nullable Color hairColor, @Nonnull InputStreamFunction<T> mapper) {
            Objects.requireNonNull(mapper, "Mapper may not be null");
            QueryStringBuilder qsb = new QueryStringBuilder()
                    .append(api.getApiBase() + "/auto-image/generate")
                    .append("type", "awooo");
            if(faceColor != null) {
                qsb.append("face", String.format("%06X", faceColor.getRGB() & 0xFFFFFF));
            }
            if(hairColor != null) {
                qsb.append("hair", String.format("%06X", hairColor.getRGB() & 0xFFFFFF));
            }
            return createRequest(api.newRequestBuilder(qsb.build()))
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
                    .append(api.getApiBase() + "/auto-image/generate")
                    .append("type", "eyes");
            return createRequest(api.newRequestBuilder(qsb.build()))
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
                    .append(api.getApiBase() + "/auto-image/generate")
                    .append("type", "won");
            return createRequest(api.newRequestBuilder(qsb.build()))
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
                    .append(api.getApiBase() + "/auto-image/discord-status")
                    .append("avatar", avatarUrl);
            if(status != null) {
                status.appendTo(qsb);
            }
            return createRequest(api.newRequestBuilder(qsb.build()))
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

            return createRequest(api.newRequestBuilder(api.getApiBase() + "/auto-image/license").post(RequestUtils.toBody(body)))
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

            return createRequest(api.newRequestBuilder(api.getApiBase() + "/auto-image/waifu-insult").post(RequestUtils.toBody(body)))
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

            return createRequest(api.newRequestBuilder(api.getApiBase() + "/auto-image/love-ship").post(RequestUtils.toBody(body)))
                    .setRateLimiter(getRateLimiter("/auto-image/love-ship"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->mapper.accept(RequestUtils.getInputStream(response)), RequestUtils::handleError);
        }
    }

    public static class ReputationManagerImpl extends AbstractManager implements ReputationManager {
        private Long botId;

        public ReputationManagerImpl(Weeb4JImpl api, Long botId) {
            super(api);
            this.botId = botId;
        }

        private void checkHasId() {
            if(botId == null) {
                throw new IllegalStateException("No bot ID has been set!");
            }
        }

        @Override
        public void setBotId(long botId) {
            this.botId = botId;
        }

        @Override
        public long getBotId() {
            checkHasId();
            return botId;
        }

        @CheckReturnValue
        @Nonnull
        @Override
        public PendingRequest<User> getUser(long userId) {
            return createRequest(api.newRequestBuilder(api.getApiBase() + "/reputation/" + getBotId() + "/" + userId))
                    .setRateLimiter(getRateLimiter("/reputation"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        return User.fromJSON(json.getJSONObject("user"));
                    }, RequestUtils::handleError);
        }

        @CheckReturnValue
        @Nonnull
        @Override
        public PendingRequest<TransferResult> giveReputation(long targetId, long sourceId) {
            return createRequest(
                    api.newRequestBuilder(api.getApiBase() + "/reputation/" + getBotId() + "/" + targetId)
                    .post(RequestUtils.toBody(new JSONObject().put("source_user", String.valueOf(sourceId))))
            )
                    .setRateLimiter(getRateLimiter("/reputation"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        return TransferResult.fromJSON(json);
                    }, ctx->{
                        JSONObject object = null;
                        try {
                            object = RequestUtils.toJSONObject(ctx.getResponse());
                        } catch(Exception ignored) {}
                        if(object != null) {
                            if(object.has("status") && object.getInt("status") == 403 && object.has("code")) {
                                JSONObject user = object.optJSONObject("user");
                                ctx.getErrorConsumer().accept(new ReputationTransferException(
                                        object.optString("message", null),
                                        object.getInt("code"),
                                        user == null ? null : User.fromJSON(user)
                                ));
                                return;
                            }
                        }
                        RequestUtils.handleErrorCode(object, ctx);
                    });
        }

        @CheckReturnValue
        @Nonnull
        @Override
        public PendingRequest<User> resetReputation(long userId, boolean resetCooldown) {
            return createRequest(
                    api.newRequestBuilder(api.getApiBase() + "/reputation/" + getBotId() + "/" + userId + "/reset")
                            .post(RequestUtils.toBody(new JSONObject()))
            )
                    .setRateLimiter(getRateLimiter("/reputation/reset"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        return User.fromJSON(json.getJSONObject("user"));
                    }, RequestUtils::handleError);
        }

        @CheckReturnValue
        @Nonnull
        @Override
        public PendingRequest<User> increaseReputation(long userId, int amount) {
            return createRequest(
                    api.newRequestBuilder(api.getApiBase() + "/reputation/" + getBotId() + "/" + userId + "/increase")
                            .post(RequestUtils.toBody(new JSONObject().put("increase", amount)))
            )
                    .setRateLimiter(getRateLimiter("/reputation/increase"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        return User.fromJSON(json.getJSONObject("user"));
                    }, RequestUtils::handleError);
        }

        @CheckReturnValue
        @Nonnull
        @Override
        public PendingRequest<User> decreaseReputation(long userId, int amount) {
            return createRequest(
                    api.newRequestBuilder(api.getApiBase() + "/reputation/" + getBotId() + "/" + userId + "/decrease")
                            .post(RequestUtils.toBody(new JSONObject().put("decrease", amount)))
            )
                    .setRateLimiter(getRateLimiter("/reputation/decrease"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        return User.fromJSON(json.getJSONObject("user"));
                    }, RequestUtils::handleError);
        }

        @CheckReturnValue
        @Nonnull
        @Override
        public PendingRequest<Settings> getSettings() {
            return createRequest(api.newRequestBuilder(api.getApiBase() + "/reputation/settings"))
                    .setRateLimiter(getRateLimiter("/reputation/settings"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        return Settings.fromJSON(json.getJSONObject("settings"));
                    }, RequestUtils::handleError);
        }

        @CheckReturnValue
        @Nonnull
        @Override
        public PendingRequest<Settings> setSettings(@Nonnull Settings newValues) {
            Objects.requireNonNull(newValues, "New values may not be null");
            return createRequest(
                    api.newRequestBuilder(api.getApiBase() + "/reputation/settings")
                        .post(RequestUtils.toBody(new JSONObject()
                                .put("reputationPerDay", newValues.getReputationPerDay())
                                .put("maximumReputation", newValues.getMaximumReputation())
                                .put("maximumReputationReceivedDay", newValues.getMaximumReputationReceivedPerDay())
                                .put("reputationCooldown", newValues.getReputationCooldown())
                        ))
            )
                    .setRateLimiter(getRateLimiter("/reputation/settings"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        return Settings.fromJSON(json.getJSONObject("settings"));
                    }, RequestUtils::handleError);
        }
    }

    public static class SettingManagerImpl extends AbstractManager implements SettingManager {
        private SettingCache cache;

        public SettingManagerImpl(Weeb4JImpl api, SettingCache cache) {
            super(api);
            this.cache = cache;
        }

        @Override
        public void setSettingCache(@Nullable SettingCache cache) {
            this.cache = cache;
        }

        @Override
        public SettingCache getSettingCache() {
            return cache;
        }

        @Nonnull
        @Override
        public PendingRequest<Setting> getSetting(@Nonnull String type, @Nonnull String id) {
            Objects.requireNonNull(type, "Type may not be null");
            Objects.requireNonNull(id, "ID may not be null");
            Request.Builder r = api.newRequestBuilder(api.getApiBase() + "/settings/" + type + "/" + id);
            if(cache != null) {
                JSONObject cached = cache.getSetting(type, id);
                if(cached != null) {
                    return completedRequest(r, Setting.create(type, id, cached));
                }
            }
            return createRequest(r)
                    .setRateLimiter(getRateLimiter("/settings"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        Setting s = Setting.fromJSON(json.getJSONObject("setting"));
                        if(cache != null) {
                            cache.saveSetting(type, id, s.getData());
                        }
                        return s;
                    }, RequestUtils::handleError);
        }

        @Nonnull
        @Override
        public PendingRequest<Setting> saveSetting(@Nonnull String type, @Nonnull String id, @Nonnull JSONObject data) {
            Objects.requireNonNull(type, "Type may not be null");
            Objects.requireNonNull(id, "ID may not be null");
            Objects.requireNonNull(data, "Data may not be null");
            String s = data.toString();
            if(s.length() > 10 * 1024) {
                throw new IllegalArgumentException("Data may not be bigger than 10 KiB");
            }
            if(cache != null) cache.saveSetting(type, id, data);
            return createRequest(
                    api.newRequestBuilder(api.getApiBase() + "/settings/" + type + "/" + id)
                    .post(RequestBody.create(RequestUtils.MEDIA_TYPE_JSON, s))
            )
                    .setRateLimiter(getRateLimiter("/settings"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        return Setting.fromJSON(json.getJSONObject("setting"));
                    }, RequestUtils::handleError);
        }

        @Nonnull
        @Override
        public PendingRequest<Setting> deleteSetting(@Nonnull String type, @Nonnull String id) {
            Objects.requireNonNull(type, "Type may not be null");
            Objects.requireNonNull(id, "ID may not be null");
            if(cache != null) cache.invalidateSetting(type, id);
            return createRequest(api.newRequestBuilder(api.getApiBase() + "/settings/" + type + "/" + id).delete())
                    .setRateLimiter(getRateLimiter("/settings"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        return Setting.fromJSON(json.getJSONObject("setting"));
                    }, RequestUtils::handleError);
        }

        @Nonnull
        @Override
        public PendingRequest<Setting> getSubSetting(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id) {
            Objects.requireNonNull(parentType, "Parent type may not be null");
            Objects.requireNonNull(parentId, "Parent ID may not be null");
            Objects.requireNonNull(type, "Type may not be null");
            Objects.requireNonNull(id, "ID may not be null");
            Request.Builder r = api.newRequestBuilder(api.getApiBase() + "/settings/" + parentType + "/" + parentId + "/" + type + "/" + id);
            if(cache != null) {
                JSONObject cached = cache.getSubSetting(parentType, parentId, type, id);
                if(cached != null) {
                    return completedRequest(r, Setting.create(parentType, parentId, type, id, cached));
                }
            }
            return createRequest(r)
                    .setRateLimiter(getRateLimiter("/settings"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        Setting setting =  Setting.fromJSON(json.getJSONObject("subsetting"));
                        if(cache != null) {
                            cache.saveSubSetting(parentType, parentId, type, id, setting.getData());
                        }
                        return setting;
                    }, RequestUtils::handleError);
        }

        @Nonnull
        @Override
        public PendingRequest<Setting> saveSubSetting(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id, @Nonnull JSONObject data) {
            Objects.requireNonNull(parentType, "Parent type may not be null");
            Objects.requireNonNull(parentId, "Parent ID may not be null");
            Objects.requireNonNull(type, "Type may not be null");
            Objects.requireNonNull(id, "ID may not be null");
            Objects.requireNonNull(data, "Data may not be null");
            String s = data.toString();
            if(s.length() > 10 * 1024) {
                throw new IllegalArgumentException("Data may not be bigger than 10 KiB");
            }
            if(cache != null) cache.saveSubSetting(parentType, parentId, type, id, data);
            return createRequest(
                    api.newRequestBuilder(api.getApiBase() + "/settings/" + parentType + "/" + parentId + "/" + type + "/" + id)
                            .post(RequestBody.create(RequestUtils.MEDIA_TYPE_JSON, s))
            )
                    .setRateLimiter(getRateLimiter("/settings"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        return Setting.fromJSON(json.getJSONObject("subsetting"));
                    }, RequestUtils::handleError);
        }

        @Nonnull
        @Override
        public PendingRequest<Setting> deleteSubSetting(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type, @Nonnull String id) {
            Objects.requireNonNull(parentType, "Parent type may not be null");
            Objects.requireNonNull(parentId, "Parent ID may not be null");
            Objects.requireNonNull(type, "Type may not be null");
            Objects.requireNonNull(id, "ID may not be null");
            if(cache != null) cache.invalidateSubSetting(parentType, parentId, type, id);
            return createRequest(api.newRequestBuilder(api.getApiBase() + "/settings/" + parentType + "/" + parentId + "/" + type + "/" + id).delete())
                    .setRateLimiter(getRateLimiter("/settings"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        return Setting.fromJSON(json.getJSONObject("subsetting"));
                    }, RequestUtils::handleError);
        }

        @Nonnull
        @Override
        public PendingRequest<List<String>> listSubSettings(@Nonnull String parentType, @Nonnull String parentId, @Nonnull String type) {
            Objects.requireNonNull(parentType, "Parent type may not be null");
            Objects.requireNonNull(parentId, "Parent ID may not be null");
            Objects.requireNonNull(type, "Type may not be null");
            return createRequest(api.newRequestBuilder(api.getApiBase() + "/settings/" + parentType + "/" + parentId + "/" + type))
                    .setRateLimiter(getRateLimiter("/settings"))
                    .setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
                    .build(response->{
                        JSONObject json = RequestUtils.toJSONObject(response);
                        JSONArray array = json.getJSONArray("subsettings");
                        List<String> list = new ArrayList<>();
                        for(int i = 0, j = array.length(); i < j; i++) {
                            list.add(array.getJSONObject(i).getString("subId"));
                        }
                        return Collections.unmodifiableList(list);
                    }, RequestUtils::handleError);
        }

        private <T> PendingRequest<T> completedRequest(Request.Builder r, T data) {
            return new PendingRequest<T>(this, r) {
                @Nullable
                @Override
                protected T onSuccess(@Nonnull Response response) {
                    return data;
                }

                @Override
                public void async(@Nullable Consumer<T> onSuccess, @Nullable Consumer<RequestException> onError) {
                    if(onSuccess != null) {
                        onSuccess.accept(data);
                    }
                }

                @Nonnull
                @Override
                public Future<T> submit() {
                    return CompletableFuture.completedFuture(data);
                }

                @Override
                public T execute() {
                    return data;
                }
            };
        }
    }
}
