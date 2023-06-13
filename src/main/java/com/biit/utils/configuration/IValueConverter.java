package com.biit.utils.configuration;

public interface IValueConverter<T> {

    T convertFromString(String value);

    String convertToString(Object value);

}
