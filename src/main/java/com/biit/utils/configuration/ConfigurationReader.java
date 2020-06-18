package com.biit.utils.configuration;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.configuration.exceptions.PropertyNotFoundException;

public class ConfigurationReader {
	private static final String VALUES_SEPARATOR_REGEX = " *, *";
	private static final char PREFIX_SEPARATOR_CHAR = '.';
	private final Map<Class<?>, IValueConverter<?>> converter;
	private final Map<String, String> propertiesDefault;
	private Map<String, String> propertiesFinalValue;
	private final List<IPropertiesSource> propertiesSources;
	private final Set<PropertyChangedListener> propertyChangedListeners;
	private final Map<IPropertiesSource, Map<String, String>> propertiesBySourceValues;

	public enum Case {
		SENSITIVE, INSENSITIVE;
	}

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

		// Log if any property has changed the value.
		addPropertyChangedListener(new PropertyChangedListener() {

			@Override
			public void propertyChanged(String propertyId, String oldValue, String newValue) {
				BiitCommonLogger.info(this.getClass(), "Property '" + propertyId + "' has changed value from '"
						+ oldValue + "' to '" + newValue + "'.");
			}
		});
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
		readConfigurations(Case.SENSITIVE);
	}

	public void readConfigurations(Case caseMode) {
		switch (caseMode) {
		case SENSITIVE:
			propertiesFinalValue = new HashMap<String, String>();
			break;
		case INSENSITIVE:
			propertiesFinalValue = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			break;
		}
		propertiesFinalValue.putAll(propertiesDefault);

		for (final IPropertiesSource propertiesSource : propertiesSources) {
			final Properties propertyFile = propertiesSource.loadFile();
			if (propertyFile != null) {
				readAllProperties(propertyFile, propertiesSource);
			}
		}
	}

	/**
	 * Reads all properties configured in this configuration reader from
	 * propertyFile. If they doesn't exist, then the current value is maintained as
	 * default value.
	 * 
	 * @param propertyFile
	 */
	private void readAllProperties(Properties propertyFile, IPropertiesSource propertiesSource) {
		for (final String propertyId : new HashMap<String, String>(propertiesFinalValue).keySet()) {
			final String value = propertyFile.getProperty(propertyId, propertiesFinalValue.get(propertyId));
			// Notify property change
			if (propertiesBySourceValues.get(propertiesSource) == null) {
				propertiesBySourceValues.put(propertiesSource, new HashMap<String, String>());
			}

			if (propertiesBySourceValues.get(propertiesSource).get(propertyId) != null
					&& propertiesBySourceValues.get(propertiesSource).get(propertyId).length() > 0
					&& !propertiesBySourceValues.get(propertiesSource).get(propertyId).equals(value)) {
				// Launch listeners.
				for (final PropertyChangedListener propertyChangedListener : propertyChangedListeners) {
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
			propertiesDefault.put(propertyName, ((String) defaultValue).trim());
			propertiesFinalValue.put(propertyName, ((String) defaultValue).trim());
		} else {
			propertiesDefault.put(propertyName, getConverter(defaultValue.getClass()).convertToString(defaultValue));
			propertiesFinalValue.put(propertyName, getConverter(defaultValue.getClass()).convertToString(defaultValue));
		}
	}

	/**
	 * Read all properties and set an empty string as default value.
	 */
	public void initializeAllProperties() {
		for (final IPropertiesSource propertiesSource : propertiesSources) {
			final Properties propertyFile = propertiesSource.loadFile();
			if (propertyFile != null) {
				final Enumeration<?> enumerator = propertyFile.propertyNames();
				while (enumerator.hasMoreElements()) {
					addProperty((String) enumerator.nextElement(), "");
				}
			}
		}
	}

	/**
	 * Gets all defined prefix for the properties.
	 * 
	 * @return
	 */
	public Set<String> getAllPropertiesPrefixes() {
		final Set<String> prefixes = new HashSet<>();
		for (final IPropertiesSource propertiesSource : propertiesSources) {
			final Properties propertyFile = propertiesSource.loadFile();
			if (propertyFile != null) {
				final Enumeration<?> enumerator = propertyFile.propertyNames();
				while (enumerator.hasMoreElements()) {
					final String element = (String) enumerator.nextElement();
					if (element.contains(String.valueOf(PREFIX_SEPARATOR_CHAR))) {
						prefixes.add(element.substring(0, element.indexOf(PREFIX_SEPARATOR_CHAR)));
					}
				}
			}
		}
		return prefixes;
	}

	public String getProperty(String propertyName) throws PropertyNotFoundException {
		if (propertiesFinalValue.containsKey(propertyName)) {
			if (propertiesFinalValue.get(propertyName) != null) {
				return propertiesFinalValue.get(propertyName).trim();
			} else {
				return null;
			}
		} else {
			throw new PropertyNotFoundException(
					"Property '" + propertyName + "' not defined in the configuration reader");
		}
	}

	public <T> T getProperty(String propertyName, Class<? extends T> type) throws PropertyNotFoundException {
		final String stringValue = getProperty(propertyName);
		if (stringValue != null) {
			return getConverter(type).convertFromString(stringValue);
		} else {
			return null;
		}
	}

	public Map<String, String> getAllProperties() {
		return this.propertiesFinalValue;
	}

	public List<IPropertiesSource> getPropertiesSources() {
		return propertiesSources;
	}

	public void addPropertyChangedListener(PropertyChangedListener propertyChangedListener) {
		propertyChangedListeners.add(propertyChangedListener);
	}

	/**
	 * Stops file watchers in from all configuration files. This is necessary in
	 * projects such as JpaSchemaExporters that waits until all threads are closed.
	 */
	public void stopFileWatchers() {
		for (final IPropertiesSource sources : propertiesSources) {
			if (sources instanceof PropertiesSourceFile) {
				((PropertiesSourceFile) sources).stopFileWatcher();
			}
		}
	}

	protected String[] getCommaSeparatedValues(String propertyName) throws PropertyNotFoundException {
		String value = getProperty(propertyName);
		// Remove useless spaces around commas.
		value = value.replaceAll(VALUES_SEPARATOR_REGEX, ",");
		// Split by commas.
		return value.split(",");

	}
}
