package com.biit.utils.string;

/*-
 * #%L
 * Generic utilities used in all Biit projects.
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
