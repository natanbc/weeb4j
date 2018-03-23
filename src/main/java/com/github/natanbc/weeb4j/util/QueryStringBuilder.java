package com.github.natanbc.weeb4j.util;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public class QueryStringBuilder {
    private static final String UNSAFE_CHARS = " %$&+,/:;=?@<>#%";

    private final StringBuilder sb = new StringBuilder();
    private boolean hasQueryParams;

    @Nonnull
    public QueryStringBuilder append(@Nonnull String text) {
        sb.append(text);
        return this;
    }

    @Nonnull
    public QueryStringBuilder append(@Nonnull String key, @Nonnull String value) {
        if(hasQueryParams) {
            sb.append('&').append(key).append("=").append(encode(value));
        } else {
            sb.append('?').append(key).append("=").append(encode(value));
            hasQueryParams = true;
        }
        return this;
    }

    @Nonnull
    public QueryStringBuilder append(@Nonnull String key, @Nonnull List<String> values) {
        return append(key, values.stream().map(QueryStringBuilder::encode).collect(Collectors.joining(",")));
    }

    @Nonnull
    @CheckReturnValue
    public String build() {
        return sb.toString();
    }

    private static String encode(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private static boolean isUnsafe(char ch) {
        return ch > 128 || UNSAFE_CHARS.indexOf(ch) >= 0;
    }
}
