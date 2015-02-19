package com.biit.utils.configuration;

public class BooleanValueConverter implements IValueConverter<Boolean> {

	protected static final Object TRUE = "true";
	protected static final Object FALSE = "false";
	
	@Override
	public Boolean convertFromString(String value) {
		if(value.equals(TRUE)){
			return true;
		}
		if(value.equals(FALSE)){
			return false;
		}
		return false;
	}

	@Override
	public String convertToString(Object value) {
		return value.toString();
	}
	
}