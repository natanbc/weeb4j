package com.github.natanbc.weeb4j.image;

import com.github.natanbc.weeb4j.util.QueryStringBuilder;

import javax.annotation.Nonnull;

public enum FileType implements QueryParam {
    GIF("gif"), JPG("jpg", "jpeg"), PNG("png"), UNKNOWN;

    private final String[] aliases;

    FileType(String... aliases) {
        this.aliases = aliases;
    }

    @Override
    public void appendTo(@Nonnull QueryStringBuilder builder) {
        builder.append("filetype", name().toLowerCase());
    }

    static FileType fromString(String value) {
        for(FileType type : values()) {
            for(String alias : type.aliases) {
                if(alias.equalsIgnoreCase(value)) return type;
            }
        }
        return UNKNOWN;
    }
}
