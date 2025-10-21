package com.biit.utils.file;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public final class FileWriter {

    private FileWriter() {

    }

    /**
     * Writes a temporal file with a string content
     *
     * @param prefix prefix of the file
     * @param sufix  sufix of the file
     * @param string string to write
     * @return a file
     * @throws IOException
     */
    public static File writeInTempFile(String prefix, String sufix, String string) throws IOException {
        final File temp;
        temp = File.createTempFile(prefix, sufix);

        final FileOutputStream fileStream = new FileOutputStream(temp);
        final OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");

        writer.write(string);
        writer.close();
        return temp;
    }
}
