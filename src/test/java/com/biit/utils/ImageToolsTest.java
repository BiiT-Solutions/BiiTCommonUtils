package com.biit.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.testng.annotations.Test;

import com.biit.utils.image.ImageTools;

@Test(groups = "imageTests")
public class ImageToolsTest {

	@Test
	public void checkImages() throws IOException {
		byte[] imageInBytes = ImageTools.loadImageFromResource("testImage.jpg");
		BufferedImage image = ImageTools.getImage(imageInBytes);
		BufferedImage newImage = ImageTools.resizeImage(image, 100, 100);
		ImageTools.saveInFile(newImage, "png", System.getProperty("java.io.tmpdir") + "/testImage2.png");
		File finalImage = new File(System.getProperty("java.io.tmpdir") + "/testImage2.png");
		Assert.assertTrue(finalImage.exists());
	}

}
