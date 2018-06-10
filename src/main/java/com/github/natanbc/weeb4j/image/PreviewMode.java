package com.github.natanbc.weeb4j.image;

import com.github.natanbc.weeb4j.internal.QueryParam;
import com.github.natanbc.weeb4j.util.QueryStringBuilder;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public enum PreviewMode implements QueryParam {
    SHOW {
        @Override
        public void appendTo(@Nonnull QueryStringBuilder builder) {
            builder.append("preview", "true");
        }
    },
    HIDE {
        @Override
        public void appendTo(@Nonnull QueryStringBuilder builder) {
            builder.append("preview", "false");
        }
    },
    DEFAULT {
        @Override
        public void appendTo(@Nonnull QueryStringBuilder builder) {
            //don't set anything
        }
    }
}
