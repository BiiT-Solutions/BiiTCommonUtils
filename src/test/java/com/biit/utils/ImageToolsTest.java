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

import com.biit.utils.image.ImageTools;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Test(groups = "imageTests")
public class ImageToolsTest {

    @Test
    public void checkImages() throws IOException {
        final byte[] imageInBytes = ImageTools.loadImageFromResource("testImage.jpg");
        final BufferedImage image = ImageTools.getImage(imageInBytes);
        final BufferedImage newImage = ImageTools.resizeImage(image, 100, 100);
        ImageTools.saveInFile(newImage, "png", System.getProperty("java.io.tmpdir") + "/testImage2.png");
        final File finalImage = new File(System.getProperty("java.io.tmpdir") + "/testImage2.png");
        Assert.assertTrue(finalImage.exists());
    }

}
