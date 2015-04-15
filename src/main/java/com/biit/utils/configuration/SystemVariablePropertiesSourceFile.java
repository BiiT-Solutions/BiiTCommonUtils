package com.biit.utils.configuration;

import java.util.Properties;

import com.biit.logger.BiitCommonLogger;
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
	public Properties loadFile(){
		String environmentVariableValue = PropertiesFile.readEnvironmentVariable(getEnvironmentVariable());
		if(environmentVariableValue!=null){
			setFilePath(environmentVariableValue);
			return super.loadFile();
		}else{
			BiitCommonLogger.debug(this.getClass(), "Environmental variable '"+getEnvironmentVariable()+"' is not set on the system.");
			return null;
		}
	}
	
}
