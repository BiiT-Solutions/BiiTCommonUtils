package com.biit.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.biit.utils.image.ImageTools;

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
