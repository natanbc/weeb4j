package com.github.natanbc.weeb4j.util;

import com.github.natanbc.weeb4j.Weeb4J;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

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

    public static String tryFindMainClass() {
        try {
            Enumeration<URL> entries = Weeb4J.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while(entries.hasMoreElements()) {
                try(InputStream is = entries.nextElement().openStream()) {
                    Manifest m = new Manifest(is);
                    String mainClass = m.getMainAttributes().getValue("Main-Class");
                    if(mainClass != null) return mainClass;
                } catch(IOException ignored) {}
            }
        } catch(IOException ignored) {}
        return null;
    }
}
