package com.github.natanbc.weeb4j.util;

import java.net.MalformedURLException;
import java.net.URL;

public class Utils {
    public static void notEmpty(String string) {
        if(string == null || string.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string");
        }
    }

    public static void validUrl(String string) {
        notEmpty(string);
        try {
            new URL(string);
        } catch(MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL " + string, e);
        }
    }
}
