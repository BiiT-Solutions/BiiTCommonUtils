package com.biit.utils.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.file.PropertiesFile;
import com.biit.utils.file.watcher.FileWatcher;
import com.biit.utils.file.watcher.FileWatcher.FileModifiedListener;

public class PropertiesSourceFile extends SourceFile<Properties> implements IPropertiesSource {
	private Set<FileModifiedListener> fileModifiedListeners;
	private FileWatcher fileWatcher;

	public PropertiesSourceFile(String fileName) {
		super(fileName);
		fileModifiedListeners = new HashSet<>();
	}

	public PropertiesSourceFile(String filePath, String fileName) {
		super(filePath, fileName);
		fileModifiedListeners = new HashSet<>();
	}

	public void addFileModifiedListeners(FileModifiedListener fileModifiedListener) {
		fileModifiedListeners.add(fileModifiedListener);
	}

	@Override
	public void setFilePath(String filePath) {
		super.setFilePath(filePath);
		setWatcher();
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

	private String getDirectoryToWatch() {
		return (getFilePath() != null ? getFilePath() : this.getClass().getClassLoader().getResource(".").getPath());
	}

	private void setWatcher() {
		Set<String> checkedFiles = new HashSet<>(Arrays.asList(new String[] { getFileName() }));

		try {
			fileWatcher = new FileWatcher(getDirectoryToWatch(), checkedFiles);
			fileWatcher.addFileModifiedListener(new FileModifiedListener() {

				@Override
				public void changeDetected(Path pathToFile) {
					// Pass the listener to current listeners.S
					for (FileModifiedListener fileModifiedListener : fileModifiedListeners) {
						fileModifiedListener.changeDetected(pathToFile);
					}
				}
			});
		} catch (IOException e) {
			BiitCommonLogger.errorMessageNotification(this.getClass(), e);
		}
	}

	public void stopFileWatcher() {
		if (fileWatcher != null) {
			fileWatcher.closeFileWatcher();
		}
	}
}
