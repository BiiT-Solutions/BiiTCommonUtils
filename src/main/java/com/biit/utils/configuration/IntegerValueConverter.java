package com.biit.utils.configuration;

public class IntegerValueConverter implements IValueConverter<Integer> {

	@Override
	public Integer convertFromString(String value) {
		return Integer.parseInt(value);
	}

	@Override
	public String convertToString(Object value) {
		return value.toString();
	}

}
