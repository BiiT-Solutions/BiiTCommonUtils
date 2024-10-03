package com.biit.utils.string;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class RandomPasswordGenerator {
    private static final String ALPHABETIC_UPPERCASE_SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHABETIC_LOWERCASE_SYMBOLS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC_SYMBOLS = "0123456789";
    private static final String SYMBOLS = "$%&!?¿_-{}[]";
    private static final String ALPHANUMERIC_AND_SPECIAL_SYMBOLS = ALPHABETIC_UPPERCASE_SYMBOLS + ALPHABETIC_LOWERCASE_SYMBOLS
            + NUMERIC_SYMBOLS + SYMBOLS;
    private static final SecureRandom RANDOM = new SecureRandom();

    private RandomPasswordGenerator() {

    }

    public static String generateRandomPassword(int length) {
        final List<Character> chars = new ArrayList<>(length);
        // Make sure we have at least one upper, lower, and number
        chars.add(ALPHABETIC_UPPERCASE_SYMBOLS.charAt(RANDOM.nextInt(ALPHABETIC_UPPERCASE_SYMBOLS.length())));
        chars.add(ALPHABETIC_LOWERCASE_SYMBOLS.charAt(RANDOM.nextInt(ALPHABETIC_LOWERCASE_SYMBOLS.length())));
        chars.add(NUMERIC_SYMBOLS.charAt(RANDOM.nextInt(NUMERIC_SYMBOLS.length())));
        chars.add(SYMBOLS.charAt(RANDOM.nextInt(NUMERIC_SYMBOLS.length())));
        for (int i = 0; i < length - chars.size(); i++) {
            //Add random values.
            chars.add(ALPHANUMERIC_AND_SPECIAL_SYMBOLS.charAt(RANDOM.nextInt(ALPHANUMERIC_AND_SPECIAL_SYMBOLS.length())));
        }

        // Shuffle characters to mix up the first 4 characters
        Collections.shuffle(chars);

        return chars.stream().map(String::valueOf).collect(Collectors.joining());
    }
}
