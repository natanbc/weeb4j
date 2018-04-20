package com.github.natanbc.weeb4j.reputation;

import com.github.natanbc.reliqua.request.PendingRequest;
import com.github.natanbc.weeb4j.Weeb4J;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public interface ReputationManager {
    /**
     * Returns the Weeb4J instance associated with this object.
     *
     * @return The Weeb4J instance associated with this object.
     */
    @CheckReturnValue
    @Nonnull
    Weeb4J getApi();

    /**
     * Sets the bot id used for requests.
     *
     * @param botId Bot id to use.
     */
    void setBotId(long botId);

    /**
     * Returns the bot id used for requests. Throws if unset.
     *
     * @return The bot id used for requests.
     *
     * @throws IllegalStateException If no bot id has been set.
     */
    @CheckReturnValue
    long getBotId();

    /**
     * Retrieves an user's data.
     *
     * @param userId User's id.
     *
     * @return The user's data.
     *
     * @throws IllegalStateException If no bot id has been set.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<User> getUser(long userId);

    /**
     * Makes one user add reputation to another.
     *
     * @param targetId User receiving reputation.
     * @param sourceId User giving reputation.
     *
     * @return The data of both users after the transfer.
     *
     * @throws IllegalStateException If no bot id has been set.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<TransferResult> giveReputation(long targetId, long sourceId);

    /**
     * Resets an user's reputation.
     *
     * @param userId User to reset.
     * @param resetCooldown Whether or not to reset the cooldown field as well.
     *
     * @return The user after resetting.
     *
     * @throws IllegalStateException If no bot id has been set.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<User> resetReputation(long userId, boolean resetCooldown);

    /**
     * Resets an user's reputation.
     *
     * @param userId User to reset.
     *
     * @return The user after resetting.
     *
     * @throws IllegalStateException If no bot id has been set.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<User> resetReputation(long userId) {
        return resetReputation(userId, false);
    }

    /**
     * Increases an user's reputation.
     *
     * @param userId User to increase.
     * @param amount Amount to increase.
     *
     * @return The user after increasing.
     *
     * @throws IllegalStateException If no bot id has been set.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<User> increaseReputation(long userId, int amount);

    /**
     * Increases an user's reputation.
     *
     * @param userId User to increase.
     *
     * @return The user after increasing.
     *
     * @throws IllegalStateException If no bot id has been set.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<User> increaseReputation(long userId) {
        return increaseReputation(userId, 1);
    }

    /**
     * Decreases an user's reputation.
     *
     * @param userId User to decrease.
     * @param amount Amount to decrease.
     *
     * @return The user after decreasing.
     *
     * @throws IllegalStateException If no bot id has been set.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<User> decreaseReputation(long userId, int amount);

    /**
     * Decreases an user's reputation.
     *
     * @param userId User to decrease.
     *
     * @return The user after decreasing.
     *
     * @throws IllegalStateException If no bot id has been set.
     */
    @CheckReturnValue
    @Nonnull
    default PendingRequest<User> decreaseReputation(long userId) {
        return decreaseReputation(userId, 1);
    }

    /**
     * Returns the currently active settings for the current token.
     *
     * @return The currently active settings for the current token.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<Settings> getSettings();

    /**
     * Updates the currently active settings for the current token.
     *
     * @param newValues New settings to set.
     *
     * @return The newly set values.
     */
    @CheckReturnValue
    @Nonnull
    PendingRequest<Settings> setSettings(@Nonnull Settings newValues);
}
