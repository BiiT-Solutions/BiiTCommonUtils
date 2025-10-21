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

import java.io.FileNotFoundException;
import java.util.Map;

public abstract class SourceFile<FileType> implements ISourceFile<FileType> {
    private String filePath;
    private final String fileName;

    public SourceFile(String fileName) {
        this.fileName = fileName;
        setFilePath(null);
    }

    public SourceFile(String filePath, String fileName) {
        this.fileName = fileName;
        setFilePath(filePath);
    }

    @Override
    public abstract FileType loadFile() throws FileNotFoundException;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return (getFilePath() != null ? getFilePath() + "/" : "") + getFileName();
    }

    public static String readEnvironmentVariable(String environmentVariable) {
        final Map<String, String> env = System.getenv();
        return env.get(environmentVariable);
    }
}
