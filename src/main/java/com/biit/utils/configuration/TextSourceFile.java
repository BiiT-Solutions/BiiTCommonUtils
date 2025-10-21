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
import com.biit.utils.file.FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class TextSourceFile extends SourceFile<String> {

    public TextSourceFile(String fileName) {
        super(fileName);
    }

    public TextSourceFile(String filePath, String fileName) {
        super(filePath, fileName);
    }

    @Override
    public String loadFile() throws FileNotFoundException {
        try {
            if (getFilePath() == null) {
                // return PropertiesFile.load(getFileName());
                FileReader.getResource(getFileName(), StandardCharsets.UTF_8);
            } else {
                // return PropertiesFile.load(getFilePath(), getFileName());
                return readFile(getFilePath() + File.separator + getFileName(), StandardCharsets.UTF_8);
            }
        } catch (NoSuchFileException nsfe) {
            BiitCommonLogger.warning(this.getClass(), "File not found '" + getFilePath() + File.separator
                    + getFileName() + "'.");
        } catch (IOException e) {
            BiitCommonLogger.errorMessageNotification(this.getClass(), e);
        } catch (NullPointerException e) {
            BiitCommonLogger.info(this.getClass(), e.getMessage());
        }
        return null;
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        final byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
