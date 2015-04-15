package com.biit.utils.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.file.PropertiesFile;

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
			BiitCommonLogger.warning(this.getClass(), e.getMessage());
		} catch (IOException e) {
			BiitCommonLogger.errorMessageNotification(this.getClass(), e);
		} catch (NullPointerException e) {
			BiitCommonLogger.info(this.getClass(), e.getMessage());
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
