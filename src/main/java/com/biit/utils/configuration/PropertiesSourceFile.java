package com.biit.utils.configuration;

/*-
 * #%L
 * Generic utilities used in all Biit projects.
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.file.PropertiesFile;
import com.biit.utils.file.watcher.FileWatcher;
import com.biit.utils.file.watcher.FileWatcher.FileModifiedListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

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
            BiitCommonLogger.debug(this.getClass(), e.getMessage());
        } catch (IOException e) {
            BiitCommonLogger.errorMessageNotification(this.getClass(), e);
        } catch (NullPointerException e) {
            BiitCommonLogger.info(this.getClass(), e.getMessage());
        }
        BiitCommonLogger.debug(this.getClass(), "File '" + getFilePath() + "/" + getFileName() + "' not found.");
        return null;
    }

    private String getDirectoryToWatch() {
        return (getFilePath() != null ? getFilePath() : this.getClass().getClassLoader().getResource(".").getPath());
    }

    private void setWatcher() {
        final Set<String> checkedFiles = new HashSet<>(Arrays.asList(new String[]{getFileName()}));

        try {
            fileWatcher = new FileWatcher(getDirectoryToWatch(), checkedFiles);
            fileWatcher.addFileModifiedListener(new FileModifiedListener() {

                @Override
                public void changeDetected(Path pathToFile) {
                    // Pass the listener to current listeners.S
                    for (final FileModifiedListener fileModifiedListener : fileModifiedListeners) {
                        fileModifiedListener.changeDetected(pathToFile);
                    }
                }
            });
        } catch (IOException e) {
            BiitCommonLogger.errorMessageNotification(this.getClass(), e);
        } catch (NullPointerException npe) {
            BiitCommonLogger.warning(this.getClass(), "Directory to watch not found!");
        }
    }

    public void stopFileWatcher() {
        if (fileWatcher != null) {
            fileWatcher.closeFileWatcher();
        }
    }
}
