package com.github.natanbc.weeb4j;

@SuppressWarnings({"unused", "WeakerAccess"})
public class WeebInfo {
    public static final String VERSION_MAJOR = "@VERSION_MAJOR@";
    public static final String VERSION_MINOR = "@VERSION_MINOR@";
    public static final String VERSION_REVISION = "@VERSION_REVISION@";
    public static final String COMMIT = "@COMMIT_HASH@";
    @SuppressWarnings("ConstantConditions")
    public static final String VERSION = VERSION_MAJOR.startsWith("@") ? "dev" : String.format("%s.%s.%s", VERSION_MAJOR, VERSION_MINOR, VERSION_REVISION);
}
