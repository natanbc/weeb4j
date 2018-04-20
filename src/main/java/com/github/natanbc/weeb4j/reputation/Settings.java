package com.github.natanbc.weeb4j.reputation;

import org.json.JSONObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class Settings {
    private final int reputationPerDay;
    private final int maximumReputation;
    private final int maximumReputationReceivedDay;
    private final int reputationCooldown;

    private Settings(int reputationPerDay, int maximumReputation, int maximumReputationReceivedDay, int reputationCooldown) {
        this.reputationPerDay = reputationPerDay;
        this.maximumReputation = maximumReputation;
        this.maximumReputationReceivedDay = maximumReputationReceivedDay;
        this.reputationCooldown = reputationCooldown;
    }

    /**
     * Returns the maximum reputation a user can give per cooldown. Defaults to 2.
     *
     * @return The maximum reputation a user can give per cooldown.
     */
    @CheckReturnValue
    public int getReputationPerDay() {
        return reputationPerDay;
    }

    /**
     * Returns the maximum reputation a user may receive. A value of 0 means disabled. Defaults to 0.
     *
     * @return The maximum reputation a user may receive.
     */
    @CheckReturnValue
    public int getMaximumReputation() {
        return maximumReputation;
    }

    /**
     * Returns the maximum reputation a user may receive per day. A value of 0 means disabled. Defaults to 0.
     *
     * @return The maximum reputation a user may receive per day.
     */
    @CheckReturnValue
    public int getMaximumReputationReceivedPerDay() {
        return maximumReputationReceivedDay;
    }

    /**
     * Returns the cooldown per reputation, in seconds. Defaults to 86400 (1 day).
     *
     * @return The cooldown per reputation, in seconds.
     */
    @CheckReturnValue
    public int getReputationCooldown() {
        return reputationCooldown;
    }

    /**
     * Creates a new Builder preconfigured with the values stored in this object.
     *
     * @return A new preconfigured Builder.
     */
    @CheckReturnValue
    @Nonnull
    public Builder edit() {
        return new Builder()
                .setReputationPerDay(reputationPerDay)
                .setMaximumReputation(maximumReputation)
                .setMaximumReputationReceivedPerDay(maximumReputationReceivedDay)
                .setReputationCooldown(reputationCooldown);
    }

    @CheckReturnValue
    @Nonnull
    public static Settings fromJSON(JSONObject json) {
        return new Settings(
                json.getInt("reputationPerDay"),
                json.getInt("maximumReputation"),
                json.getInt("maximumReputationReceivedDay"),
                json.getInt("reputationCooldown")
        );
    }

    public static final class Builder {
        private int reputationPerDay = 2;
        private int maximumReputation;
        private int maximumReputationReceivedDay;
        private int reputationCooldown = 86400;

        @CheckReturnValue
        @Nonnull
        public Builder setReputationPerDay(int reputationPerDay) {
            if(reputationPerDay < 1) {
                throw new IllegalArgumentException("Reputation per day must be greater than zero");
            }
            this.reputationPerDay = reputationPerDay;
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Builder setMaximumReputation(int maximumReputation) {
            if(maximumReputation < 0) {
                throw new IllegalArgumentException("Maximum reputation must be greater or equal to zero");
            }
            this.maximumReputation = maximumReputation;
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Builder setMaximumReputationReceivedPerDay(int maximumReputationReceivedDay) {
            if(maximumReputationReceivedDay < 0) {
                throw new IllegalArgumentException("Maximum reputation received per day must be greater or equal to zero");
            }
            this.maximumReputationReceivedDay = maximumReputationReceivedDay;
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Builder setReputationCooldown(int reputationCooldown) {
            if(reputationCooldown < 0) {
                throw new IllegalArgumentException("Reputation cooldown must be greater or equal to zero");
            }
            this.reputationCooldown = reputationCooldown;
            return this;
        }

        @CheckReturnValue
        @Nonnull
        public Settings build() {
            return new Settings(reputationPerDay, maximumReputation, maximumReputationReceivedDay, reputationCooldown);
        }
    }
}
