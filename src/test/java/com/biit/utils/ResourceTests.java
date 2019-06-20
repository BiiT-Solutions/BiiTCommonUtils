package com.biit.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.List;

import junit.framework.Assert;

import org.testng.annotations.Test;

import com.biit.utils.file.FileReader;

@Test(groups = { "resourceTests" })
public class ResourceTests {
	private static final String RESOURCE_FOLDER = "folderWithResources";

	@Test
	public void listFilesInFolder() {
		final List<File> resources = FileReader.getResources(RESOURCE_FOLDER);
		Assert.assertEquals(2, resources.size());
	}

	@Test
	public void getResourceContent() throws FileNotFoundException {
		final String content = FileReader.getResource(RESOURCE_FOLDER + File.separator + "resource1.txt",
				Charset.defaultCharset());
		Assert.assertEquals("text1\n", content);
	}
}
