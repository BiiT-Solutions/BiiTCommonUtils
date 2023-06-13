package com.biit.utils.configuration;

import com.biit.logger.BiitCommonLogger;

import java.io.FileNotFoundException;

public class SystemVariableTextSourceFile extends TextSourceFile {
    private final String environmentVariable;

    public SystemVariableTextSourceFile(String environmentVariable, String fileName) {
        super(fileName);
        this.environmentVariable = environmentVariable;
    }

    public String getEnvironmentVariable() {
        return environmentVariable;
    }

    @Override
    public String loadFile() throws FileNotFoundException {
        final String environmentVariableValue = SourceFile.readEnvironmentVariable(getEnvironmentVariable());
        if (environmentVariableValue != null) {
            BiitCommonLogger.debug(this.getClass(), "Environmental variable '" + getEnvironmentVariable() + "' values is: '"
                    + environmentVariableValue + "'.");
            setFilePath(environmentVariableValue);
            return super.loadFile();
        } else {
            BiitCommonLogger.warning(this.getClass(), "Environmental variable '" + getEnvironmentVariable() + "' is not set on the system.");
            return null;
        }
    }
}
