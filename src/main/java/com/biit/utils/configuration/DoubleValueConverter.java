package com.biit.utils.configuration;

public class DoubleValueConverter implements IValueConverter<Double> {

	@Override
	public Double convertFromString(String value) {
		return Double.parseDouble(value);
	}

	@Override
	public String convertToString(Object value) {
		return value.toString();
	}

}
