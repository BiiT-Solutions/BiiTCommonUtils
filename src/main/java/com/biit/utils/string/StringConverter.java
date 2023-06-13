package com.biit.utils.string;

public final class StringConverter {

    private StringConverter() {

    }

    public static String[] convertToArray(String commaSeparatedString) {
        if (commaSeparatedString == null || commaSeparatedString.length() == 0) {
            return new String[0];
        }
        final String[] arrayNotFormatted = commaSeparatedString.split(",");
        final String[] formattedArray = new String[arrayNotFormatted.length];
        for (int i = 0; i < arrayNotFormatted.length; i++) {
            formattedArray[i] = arrayNotFormatted[i].trim();
        }
        return formattedArray;
    }
}
