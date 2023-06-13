package com.biit.utils.configuration;

import com.biit.logger.BiitCommonLogger;

import java.util.Properties;

public class SystemVariablePropertiesSourceFile extends PropertiesSourceFile {

    private final String environmentVariable;

    public SystemVariablePropertiesSourceFile(String environmentVariable, String fileName) {
        super(fileName);
        this.environmentVariable = environmentVariable;
    }

    public String getEnvironmentVariable() {
        return environmentVariable;
    }

    @Override
    public Properties loadFile() {
        final String environmentVariableValue = SourceFile.readEnvironmentVariable(getEnvironmentVariable());
        if (environmentVariableValue != null) {
            BiitCommonLogger.debug(this.getClass(), "Environmental variable '" + getEnvironmentVariable() + "' values is: '"
                    + environmentVariableValue + "'.");
            // Update file watcher.
            if (!environmentVariableValue.equals(getFilePath())) {
                setFilePath(environmentVariableValue);
            }
            return super.loadFile();
        } else {
            BiitCommonLogger.debug(this.getClass(), "Environmental variable '" + getEnvironmentVariable() + "' is not set on the system.");
            return null;
        }
    }

}
