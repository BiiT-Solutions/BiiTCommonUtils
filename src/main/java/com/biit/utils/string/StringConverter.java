package com.biit.utils.string;

public class StringConverter {
	public static String[] convertToArray(String commaSeparatedString) {
		String[] arrayNotFormatted = commaSeparatedString.split(",");
		String[] formattedArray = new String[arrayNotFormatted.length];
		for (int i = 0; i < arrayNotFormatted.length; i++) {
			formattedArray[i] = arrayNotFormatted[i].trim();
		}
		return formattedArray;
	}
}
