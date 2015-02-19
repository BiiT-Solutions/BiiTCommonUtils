package com.biit.utils.configuration;

import java.io.IOException;
import java.util.Properties;

import com.biit.utils.file.PropertiesFile;

public class SystemVariablePropertiesSourceFile extends PropertiesSourceFile{

	private final String environmentVariable;
	
	public SystemVariablePropertiesSourceFile(String environmentVariable, String fileName) {
		super(fileName);
		this.environmentVariable = environmentVariable;
	}

	public String getEnvironmentVariable() {
		return environmentVariable;
	}
	
	@Override
	public Properties loadFile() throws IOException{
		setFilePath(PropertiesFile.readEnvironmentVariable(getEnvironmentVariable()));
		return super.loadFile();
	}
	
}
