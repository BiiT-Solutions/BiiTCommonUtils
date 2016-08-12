package com.biit.utils.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.configuration.exceptions.PropertyNotFoundException;

public class ConfigurationReader {

	private final Map<Class<?>, IValueConverter<?>> converter;
	private final Map<String, String> propertiesDefault;
	private final Map<String, String> propertiesFinalValue;
	private final List<IPropertiesSource> propertiesSources;
	private final Set<PropertyChangedListener> propertyChangedListeners;
	private final Map<IPropertiesSource, Map<String, String>> propertiesBySourceValues;

	public ConfigurationReader() {
		converter = new HashMap<Class<?>, IValueConverter<?>>();
		propertiesDefault = new HashMap<String, String>();
		propertiesFinalValue = new HashMap<String, String>();
		propertiesSources = new ArrayList<IPropertiesSource>();
		propertyChangedListeners = new HashSet<>();
		propertiesBySourceValues = new HashMap<>();

		addConverter(Boolean.class, new BooleanValueConverter());
		addConverter(Integer.class, new IntegerValueConverter());
		addConverter(Double.class, new DoubleValueConverter());
	}

	public interface PropertyChangedListener {
		void propertyChanged(String propertyId, String oldValue, String newValue);
	}

	public <T> void addConverter(Class<T> clazz, IValueConverter<T> valueConverter) {
		converter.put(clazz, valueConverter);
	}

	@SuppressWarnings("unchecked")
	public <T> IValueConverter<T> getConverter(Class<T> clazz) {
		return (IValueConverter<T>) converter.get(clazz);
	}

	public void addPropertiesSource(IPropertiesSource propertiesSource) {
		propertiesSources.add(propertiesSource);
	}

	/**
	 * Restarts all properties to their default values and then reads all the
	 * configuration files again.
	 */
	public void readConfigurations() {
		propertiesFinalValue.clear();
		propertiesFinalValue.putAll(propertiesDefault);

		for (IPropertiesSource propertiesSource : propertiesSources) {
			Properties propertyFile = propertiesSource.loadFile();
			if (propertyFile != null) {
				readAllProperties(propertyFile, propertiesSource);
			}
		}
	}

	/**
	 * Reads all properties configured in this configuration reader from
	 * propertyFile. If they doesn't exist, then the current value is mantained
	 * as default value.
	 * 
	 * @param propertyFile
	 */
	private void readAllProperties(Properties propertyFile, IPropertiesSource propertiesSource) {
		for (String propertyId : new HashSet<String>(propertiesFinalValue.keySet())) {
			String value = propertyFile.getProperty(propertyId, propertiesFinalValue.get(propertyId));
			// Notify property change
			if (propertiesBySourceValues.get(propertiesSource) == null) {
				propertiesBySourceValues.put(propertiesSource, new HashMap<String, String>());
			}

			if (propertiesBySourceValues.get(propertiesSource).get(propertyId) != null
					&& propertiesBySourceValues.get(propertiesSource).get(propertyId).length() > 0
					&& !propertiesBySourceValues.get(propertiesSource).get(propertyId).equals(value)) {
				// Launch listeners.
				for (PropertyChangedListener propertyChangedListener : propertyChangedListeners) {
					propertyChangedListener.propertyChanged(propertyId, propertiesFinalValue.get(propertyId), value);
				}
				BiitCommonLogger.info(this.getClass(), "Property '" + propertyId + "' updated as '" + value + "'.");
			}
			// Store value.
			propertiesBySourceValues.get(propertiesSource).put(propertyId, value);
			propertiesFinalValue.put(propertyId, value);
			BiitCommonLogger.debug(this.getClass(), "Property '" + propertyId + "' set as value '" + value + "'.");
		}
	}

	/**
	 * Adds a property
	 * 
	 * @param propertyName
	 * @param defaultValue
	 */
	public <T> void addProperty(String propertyName, T defaultValue) {
		if (defaultValue == null) {
			propertiesDefault.put(propertyName, null);
			propertiesFinalValue.put(propertyName, null);
		} else if (defaultValue instanceof String) {
			propertiesDefault.put(propertyName, new String((String) defaultValue).trim());
			propertiesFinalValue.put(propertyName, new String((String) defaultValue).trim());
		} else {
			propertiesDefault.put(propertyName, getConverter(defaultValue.getClass()).convertToString(defaultValue));
			propertiesFinalValue.put(propertyName, getConverter(defaultValue.getClass()).convertToString(defaultValue));
		}
	}

	public String getProperty(String propertyName) throws PropertyNotFoundException {
		if (propertiesFinalValue.containsKey(propertyName)) {
			if (propertiesFinalValue.get(propertyName) != null) {
				return new String(propertiesFinalValue.get(propertyName).trim());
			} else {
				return null;
			}
		} else {
			throw new PropertyNotFoundException("Property not defined in the configuration reader");
		}
	}

	public <T> T getProperty(String propertyName, Class<? extends T> type) throws PropertyNotFoundException {
		String stringValue = getProperty(propertyName);
		if (stringValue != null) {
			return getConverter(type).convertFromString(stringValue);
		} else {
			return null;
		}
	}

	public List<IPropertiesSource> getPropertiesSources() {
		return propertiesSources;
	}

	public void addPropertyChangedListener(PropertyChangedListener propertyChangedListener) {
		propertyChangedListeners.add(propertyChangedListener);
	}
}
