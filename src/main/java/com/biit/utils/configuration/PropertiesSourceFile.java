package com.biit.utils.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.file.PropertiesFile;

public class PropertiesSourceFile extends SourceFile<Properties> implements IPropertiesSource {

	public PropertiesSourceFile(String fileName) {
		super(fileName);
	}

	public PropertiesSourceFile(String filePath, String fileName) {
		super(filePath, fileName);
	}

	@Override
	public Properties loadFile() {
		try {
			if (getFilePath() == null) {
				return PropertiesFile.load(getFileName());
			} else {
				return PropertiesFile.load(getFilePath(), getFileName());
			}
		} catch (FileNotFoundException e) {
			BiitCommonLogger.warning(this.getClass(), e.getMessage());
		} catch (IOException e) {
			BiitCommonLogger.errorMessageNotification(this.getClass(), e);
		} catch (NullPointerException e) {
			BiitCommonLogger.info(this.getClass(), e.getMessage());
		}
		return null;
	}

}
