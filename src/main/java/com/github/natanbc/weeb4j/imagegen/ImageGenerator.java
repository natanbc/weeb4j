package com.github.natanbc.weeb4j.imagegen;

import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.weeb4j.Weeb4J;
import com.github.natanbc.weeb4j.util.IOUtils;
import com.github.natanbc.weeb4j.util.InputStreamFunction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;

@SuppressWarnings("unused")
public interface ImageGenerator {
    /**
     * Returns the Weeb4J instance associated with this object.
     *
     * @return The Weeb4J instance associated with this object.
     */
    @CheckReturnValue
    @Nonnull
    Weeb4J getApi();

    /**
     * Generates an awoo image. Only RGB bits are used, alpha is ignored.
     *
     * @param faceColor Color used for the face. Defaults to 0xFFF0D3.
     * @param hairColor Color used for the hair. Defaults to 0xCC817C.
     * @param mapper Maps the image's input stream.
     * @param <T> Type returned by the mapper.
     *
     * @return The generated image.
     */
    @CheckReturnValue
    @Nonnull
    <T> PendingRequest<T> generateAwoo(@Nullable Color faceColor, @Nullable Color hairColor, @Nonnull InputStreamFunction<T> mapper);

    /**
     * Generates an awoo image. Only RGB bits are used, alpha is ignored.
     *
     * @param faceColor Color used for the face.
     * @param hairColor Color used for the hair.
     *
     * @return The generated image.
     */
    @CheckReturnValue
    @Nonnull
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
     */
    @CheckReturnValue
    @Nonnull
    default <T> PendingRequest<T> generateAwoo(@Nonnull InputStreamFunction<T> mapper) {
        return generateAwoo(null, null, mapper);
    }

    /**
     * Generates an awoo image. Only RGB bits are used, alpha is ignored.
     *
     * @return The generated image.
     */
    @CheckReturnValue
    @Nonnull
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
     */
    @CheckReturnValue
    @Nonnull
    <T> PendingRequest<T> generateEyes(@Nonnull InputStreamFunction<T> mapper);

    /**
     * Generates a pair of eyes looking in random directions.
     *
     * @return The generated image.
     */
    @CheckReturnValue
    @Nonnull
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
     */
    @CheckReturnValue
    @Nonnull
    <T> PendingRequest<T> generateWon(@Nonnull InputStreamFunction<T> mapper);

    /**
     * Generates a won image.
     *
     * @return The generated image.
     */
    @CheckReturnValue
    @Nonnull
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
     */
    @CheckReturnValue
    @Nonnull
    <T> PendingRequest<T> generateStatus(@Nullable DiscordStatus status, @Nonnull String avatarUrl, @Nonnull InputStreamFunction<T> mapper);

    /**
     * Generates a discord status for a given avatar.
     *
     * @param avatarUrl Avatar url.
     * @param mapper Maps the image's input stream.
     * @param <T> Type returned by the mapper.
     *
     * @return The generated image.
     */
    @CheckReturnValue
    @Nonnull
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
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<byte[]> generateStatus(@Nullable DiscordStatus status, @Nonnull String avatarUrl) {
        return generateStatus(status, avatarUrl, IOUtils.READ_FULLY);
    }

    /**
     * Generates a discord status for a given avatar.
     *
     * @param avatarUrl Avatar url.
     *
     * @return The generated image.
     */
    @CheckReturnValue
    @Nonnull
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
     */
    @CheckReturnValue
    @Nonnull
    <T> PendingRequest<T> generateLicense(@Nonnull LicenseData data, @Nonnull InputStreamFunction<T> mapper);

    /**
     * Generates a license card.
     *
     * @param data Data to show on the card.
     *
     * @return The generated image.
     *
     * @see com.github.natanbc.weeb4j.imagegen.LicenseBuilder
     */
    @CheckReturnValue
    @Nonnull
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
     */
    @CheckReturnValue
    @Nonnull
    <T> PendingRequest<T> generateWaifuInsult(@Nonnull String avatarUrl, @Nonnull InputStreamFunction<T> mapper);

    /**
     * Generates a waifu insult.
     *
     * @param avatarUrl Avatar of the waifu to be insulted.
     *
     * @return The generated image.
     */
    @CheckReturnValue
    @Nonnull
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
     */
    @CheckReturnValue
    @Nonnull
    <T> PendingRequest<T> generateLoveship(@Nonnull String firstAvatarUrl, @Nonnull String secondAvatarUrl, @Nonnull InputStreamFunction<T> mapper);

    /**
     * Generates a ship image.
     *
     * @param firstAvatarUrl First avatar of the ship.
     * @param secondAvatarUrl Second avatar of the ship.
     *
     * @return The generated image.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<byte[]> generateLoveship(@Nonnull String firstAvatarUrl, @Nonnull String secondAvatarUrl) {
        return generateLoveship(firstAvatarUrl, secondAvatarUrl, IOUtils.READ_FULLY);
    }
}
