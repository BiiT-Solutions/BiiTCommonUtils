package com.biit.utils;

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

import com.biit.utils.file.FileReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.List;

@Test(groups = {"resourceTests"})
public class ResourceTests {
    private static final String RESOURCE_FOLDER = "folderWithResources";

    @Test
    public void listFilesInFolder() {
        final List<File> resources = FileReader.getResources(RESOURCE_FOLDER);
        Assert.assertEquals(resources.size(), 2);
    }

    @Test
    public void getResourceContent() throws FileNotFoundException {
        final String content = FileReader.getResource(RESOURCE_FOLDER + File.separator + "resource1.txt",
                Charset.defaultCharset());
        Assert.assertEquals(content, "text1\n");
    }
}
