package com.biit.utils.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.biit.utils.file.PropertiesFile;
import com.biit.utils.logger.CommonUtilsLogger;

public class PropertiesSourceFile implements IPropertiesSource {

	private String filePath;
	private final String fileName;

	public PropertiesSourceFile(String fileName) {
		this.filePath = null;
		this.fileName = fileName;
	}

	public PropertiesSourceFile(String filePath, String fileName) {
		this.filePath = filePath;
		this.fileName = fileName;
	}

	public Properties loadFile() {
		try {
			if (filePath == null) {
				return PropertiesFile.load(fileName);
			} else {
				return PropertiesFile.load(filePath, fileName);
			}
		} catch (FileNotFoundException e) {
			CommonUtilsLogger.warning(this.getClass().getName(), e.getMessage());
		} catch (IOException e) {
			CommonUtilsLogger.errorMessage(this.getClass().getName(), e);
		} catch (NullPointerException e) {
			CommonUtilsLogger.info(this.getClass().getName(), e.getMessage());
		}
		return null;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getFileName() {
		return fileName;
	}
}
