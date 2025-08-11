package com.example.beautysaas.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class SlugUtil {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-+");

    public static String toSlug(String input) {
        if (input == null) {
            return "";
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .toLowerCase(Locale.ENGLISH)
                .trim();

        normalized = NONLATIN.matcher(normalized).replaceAll("");
        normalized = WHITESPACE.matcher(normalized).replaceAll("-");
        normalized = MULTIPLE_HYPHENS.matcher(normalized).replaceAll("-");

        return normalized;
    }
}
