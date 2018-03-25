package com.github.natanbc.weeb4j;

import org.json.JSONObject;

/**
 * Represents an api token.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class TokenInfo {
    private final Account account;
    private final String token;
    private final boolean wolkeToken;

    private TokenInfo(Account account, String token, boolean wolkeToken) {
        this.account = account;
        this.token = token;
        this.wolkeToken = wolkeToken;
    }

    /**
     * Returns the account associated with this token.
     *
     * @return The account associated with this token.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Returns the token used for authenticating with the API.
     *
     * @return The token used for authenticating with the API.
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns whether or not this token is a wolke token.
     *
     * @return Whether or not this token is a wolke token.
     */
    public boolean isWolkeToken() {
        return wolkeToken;
    }

    /**
     * Returns this token's type.
     *
     * @return This token's type.
     *
     * @see #isWolkeToken()
     */
    public TokenType getTokenType() {
        return isWolkeToken() ? TokenType.WOLKE : TokenType.BEARER;
    }

    public static TokenInfo fromJSON(JSONObject obj, String token) {
        return new TokenInfo(
                Account.fromJSON(obj.getJSONObject("account")),
                token,
                obj.getBoolean("wolkeToken")
        );
    }
}
