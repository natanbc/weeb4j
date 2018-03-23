package com.github.natanbc.weeb4j.image;

import com.github.natanbc.weeb4j.internal.QueryParam;
import com.github.natanbc.weeb4j.util.QueryStringBuilder;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public enum HiddenMode implements QueryParam {
    ONLY {
        @Override
        public void appendTo(@Nonnull QueryStringBuilder builder) {
            builder.append("hidden", "true");
        }
    },
    HIDE {
        @Override
        public void appendTo(@Nonnull QueryStringBuilder builder) {
            builder.append("hidden", "false");
        }
    },
    DEFAULT {
        @Override
        public void appendTo(@Nonnull QueryStringBuilder builder) {}
    }
}
