package com.github.natanbc.weeb4j;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a <a href="https://weeb.sh">weeb.sh</a> account.
 */
@SuppressWarnings("unused")
public class Account {
    private final String id;
    private final String name;
    private final long discordId;
    private final boolean active;
    private final List<String> scopes;
    private final List<String> tokens;

    private Account(String id, String name, long discordId, boolean active, List<String> scopes, List<String> tokens) {
        this.id = id;
        this.name = name;
        this.discordId = discordId;
        this.active = active;
        this.scopes = scopes;
        this.tokens = tokens;
    }

    /**
     * Returns this account's ID.
     *
     * @return This account's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the account owner's name.
     *
     * @return The account owner's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the account owner's discord ID.
     *
     * @return The account owner's discord ID.
     */
    public long getDiscordId() {
        return discordId;
    }

    /**
     * Returns whether or not this account is active.
     *
     * @return Whether or not this account is active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the scopes this account has access to.
     *
     * @return The scopes this account has access to.
     */
    public List<String> getScopes() {
        return scopes;
    }

    /**
     * Unknown.
     *
     * @return Unknown.
     */
    public List<String> getTokens() {
        return tokens;
    }

    public static Account fromJSON(JSONObject obj) {
        List<String> scopes = new ArrayList<>();
        JSONArray scopesRaw = obj.getJSONArray("scopes");
        for(int i = 0; i < scopesRaw.length(); i++) {
            scopes.add(scopesRaw.getString(i));
        }
        List<String> tokens = new ArrayList<>();
        JSONArray tokensRaw = obj.getJSONArray("tokens");
        for(int i = 0; i < tokensRaw.length(); i++) {
            tokens.add(tokensRaw.getString(i));
        }
        return new Account(
                obj.getString("id"),
                obj.getString("name"),
                obj.getLong("discordUserId"),
                obj.getBoolean("active"),
                Collections.unmodifiableList(scopes),
                Collections.unmodifiableList(tokens)
        );
    }
}
