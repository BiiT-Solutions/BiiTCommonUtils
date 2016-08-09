package com.biit.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.Test;

import com.biit.utils.file.watcher.FileWatcher;
import com.biit.utils.file.watcher.FileWatcher.FileCreationListener;
import com.biit.utils.file.watcher.FileWatcher.FileDeletionListener;
import com.biit.utils.file.watcher.FileWatcher.FileModifiedListener;

@Test(groups = { "watcherTests" })
public class WatcherTests {
	private boolean fileModificationDetected = false;
	private boolean fileCreationDetected = false;
	private boolean fileDeletionDetected = false;

	private class FileModified extends Exception {
		private static final long serialVersionUID = 4567824418370310105L;
	}

	private class FileCreated extends Exception {
		private static final long serialVersionUID = 4567824418370310105L;
	}

	private class FileDeleted extends Exception {
		private static final long serialVersionUID = 4567824418370310105L;
	}

	@Test
	public void getResourceFolderPath() {
		// System.out.println(FileWatcher.class.getClassLoader().getResource("."));
	}

	@Test(expectedExceptions = { FileModified.class })
	public void checkFileModificationListener() throws IOException, FileModified, InterruptedException {

		// Create a file.
		File file = File.createTempFile("watcherTest", ".tmp");
		file.deleteOnExit();
		Set<String> checkedFiles = new HashSet<>(Arrays.asList(new String[] { file.getName() }));
		FileWatcher fileWatcher = new FileWatcher(file.getParent(), checkedFiles);
		fileWatcher.addFileModifiedListener(new FileModifiedListener() {

			@Override
			public void changeDetected(Path pathToFile) {
				fileModificationDetected = true;
			}
		});

		// Add content to file.
		Files.write(Paths.get(file.getPath()), "NewText".getBytes(), StandardOpenOption.APPEND);

		// Check change alert!
		for (int i = 0; i < 10; i++) {
			if (fileModificationDetected == true) {
				throw new FileModified();
			}
			Thread.sleep(100);
		}
	}

	@Test(expectedExceptions = { FileCreated.class })
	public void checkFileCreationListener() throws IOException, InterruptedException, FileCreated {
		String folder = System.getProperty("java.io.tmpdir");
		String fileName = "fileCreated.txt";

		// Create a file.
		Set<String> checkedFiles = new HashSet<>(Arrays.asList(new String[] { fileName }));
		FileWatcher fileWatcher = new FileWatcher(folder, checkedFiles);
		fileWatcher.addFileCreationListener(new FileCreationListener() {

			@Override
			public void fileCreated(Path pathToFile) {
				fileCreationDetected = true;
			}
		});

		File file = new File(Files.write(Paths.get(folder + File.separator + fileName), "NewFile".getBytes(), StandardOpenOption.CREATE_NEW).toString());
		file.deleteOnExit();

		// Check change alert!
		for (int i = 0; i < 10; i++) {
			if (fileCreationDetected == true) {
				throw new FileCreated();
			}
			Thread.sleep(100);
		}
	}

	@Test(expectedExceptions = { FileDeleted.class })
	public void checkFileDeletionListener() throws IOException, InterruptedException, FileCreated, FileDeleted {
		String folder = System.getProperty("java.io.tmpdir");
		String fileName = "fileDeleted.txt";

		// Create a file.
		Set<String> checkedFiles = new HashSet<>(Arrays.asList(new String[] { fileName }));
		FileWatcher fileWatcher = new FileWatcher(folder, checkedFiles);
		fileWatcher.addFileDeletionListener(new FileDeletionListener() {

			@Override
			public void fileDeleted(Path pathToFile) {
				fileDeletionDetected = true;
			}
		});

		File file = new File(Files.write(Paths.get(folder + File.separator + fileName), "NewFile".getBytes(), StandardOpenOption.CREATE_NEW).toString());
		file.delete();

		// Check change alert!
		for (int i = 0; i < 10; i++) {
			if (fileDeletionDetected == true) {
				throw new FileDeleted();
			}
			Thread.sleep(100);
		}
	}
}
