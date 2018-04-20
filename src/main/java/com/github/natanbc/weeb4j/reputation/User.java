package com.github.natanbc.weeb4j.reputation;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public final class User {
    private final int reputation;
    private final List<OffsetDateTime> cooldown;
    private final List<OffsetDateTime> givenReputation;
    private final long id;
    private final long botId;
    private final String accountId;
    private final int availableReputations;
    private final List<Integer> nextAvailableReputations;

    private User(int reputation, List<OffsetDateTime> cooldown, List<OffsetDateTime> givenReputation, long id, long botId, String accountId, int availableReputations, List<Integer> nextAvailableReputations) {
        this.reputation = reputation;
        this.cooldown = cooldown;
        this.givenReputation = givenReputation;
        this.id = id;
        this.botId = botId;
        this.accountId = accountId;
        this.availableReputations = availableReputations;
        this.nextAvailableReputations = nextAvailableReputations;
    }

    /**
     * Returns the user's current reputation.
     *
     * @return The user's current reputation.
     */
    @CheckReturnValue
    public int getReputation() {
        return reputation;
    }

    /**
     * Returns a list of timestamps referring to the last time(s) this user has given reputation to another user.
     *
     * @return A list of timestamps referring to the last time(s) this user has given reputation to another user.
     */
    @CheckReturnValue
    @Nonnull
    public List<OffsetDateTime> getCooldown() {
        return cooldown;
    }

    /**
     * Returns a list of timestamps referring to the last time(s) this user has received reputation from another user.
     *
     * @return A list of timestamps referring to the last time(s) this user has received reputation from another user.
     */
    @CheckReturnValue
    @Nonnull
    public List<OffsetDateTime> getGivenReputation() {
        return givenReputation;
    }

    /**
     * Returns the id of the user that was passed in the first call to take or give reputation to the user.
     *
     * @return The id of the user that was passed in the first call to take or give reputation to the user.
     */
    @CheckReturnValue
    public long getId() {
        return id;
    }

    /**
     * Returns the id of the bot that was passed in the first call to take or give reputation to the user.
     *
     * @return The id of the bot that was passed in the first call to take or give reputation to the user.
     */
    @CheckReturnValue
    public long getBotId() {
        return botId;
    }

    /**
     * Returns the internal id associated with the token calling the API.
     *
     * @return The internal id associated with the token calling the API.
     */
    @CheckReturnValue
    @Nonnull
    public String getAccountId() {
        return accountId;
    }

    /**
     * Returns how many reputations the user may give out.
     *
     * @return How many reputations the user may give out.
     */
    @CheckReturnValue
    public int getAvailableReputations() {
        return availableReputations;
    }

    /**
     * Returns a list of timestamps referring to the remaining cooldown time until the user can give out reputation from now.
     *
     * @return A list of timestamps referring to the remaining cooldown time until the user can give out reputation from now.
     */
    @CheckReturnValue
    @Nonnull
    public List<Integer> getNextAvailableReputations() {
        return nextAvailableReputations;
    }

    @CheckReturnValue
    @Nonnull
    public static User fromJSON(JSONObject json) {
        List<OffsetDateTime> cooldown = new ArrayList<>();
        List<OffsetDateTime> givenReputation = new ArrayList<>();
        List<Integer> nextAvailableReputations = new ArrayList<>();
        JSONArray cooldownRaw = json.getJSONArray("cooldown");
        for(int i = 0, j = cooldownRaw.length(); i < j; i++) {
            cooldown.add(OffsetDateTime.parse(cooldownRaw.getString(i)));
        }
        JSONArray givenReputationRaw = json.getJSONArray("givenReputation");
        for(int i = 0, j = givenReputationRaw.length(); i < j; i++) {
            givenReputation.add(OffsetDateTime.parse(givenReputationRaw.getString(i)));
        }
        JSONArray nextAvailableReputationsRaw = json.optJSONArray("nextAvailableReputations");
        if(nextAvailableReputationsRaw != null) {
            for(int i = 0, j = nextAvailableReputationsRaw.length(); i < j; i++) {
                nextAvailableReputations.add(nextAvailableReputationsRaw.getInt(i));
            }
        }
        return new User(
                json.getInt("reputation"),
                Collections.unmodifiableList(cooldown),
                Collections.unmodifiableList(givenReputation),
                json.getLong("userId"),
                json.getLong("botId"),
                json.getString("accountId"),
                json.optInt("availableReputations", -1),
                Collections.unmodifiableList(nextAvailableReputations)
        );
    }
}
