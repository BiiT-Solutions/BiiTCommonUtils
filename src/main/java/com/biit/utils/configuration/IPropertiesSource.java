package com.biit.utils.configuration;

import java.io.IOException;
import java.util.Properties;

public interface IPropertiesSource {

	public Properties loadFile() throws IOException;
	
}
