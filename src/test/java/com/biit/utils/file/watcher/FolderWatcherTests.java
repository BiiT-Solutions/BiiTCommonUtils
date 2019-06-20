package com.biit.utils.file.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.testng.annotations.Test;

import com.biit.utils.file.watcher.FileWatcher.FileAddedListener;
import com.biit.utils.file.watcher.FileWatcher.FileRemovedListener;

@Test(groups = { "folderWatcherTests" })
public class FolderWatcherTests {
	private static final String FOLDER = "WatchedFolder";

	private boolean fileAddedDetected = false;
	private boolean fileRemovedDetected = false;

	private class FileAdded extends Exception {
		private static final long serialVersionUID = 5797137086506193964L;
	}

	private class FileRemoved extends Exception {
		private static final long serialVersionUID = 992196416293719673L;
	}

	@Test(expectedExceptions = { FileAdded.class })
	public void checkFolderFileAdded() throws IOException, InterruptedException, FileAdded, FileRemoved {
		fileAddedDetected = false;

		final String folderPath = Paths.get(System.getProperty("java.io.tmpdir"), FOLDER).toString();
		final File folder = new File(folderPath);
		folder.mkdirs();
		folder.deleteOnExit();

		// Register the folder.
		final FileWatcher folderWatcher = new FileWatcher(folderPath);
		folderWatcher.addFileAddedListener(new FileAddedListener() {

			@Override
			public void fileCreated(Path pathToFile) {
				fileAddedDetected = true;
			}
		});

		final String fileName = "fileAddedToFolder.txt";
		final File file = new File(Files.write(Paths.get(folderPath + File.separator + fileName), "NewFile".getBytes(),
				StandardOpenOption.CREATE_NEW).toString());
		file.deleteOnExit();

		// Check change alert!
		for (int i = 0; i < 10; i++) {
			if (fileAddedDetected) {
				throw new FileAdded();
			}
			Thread.sleep(100);
		}
	}

	@Test(expectedExceptions = { FileRemoved.class })
	public void checkFolderFileDeleted() throws IOException, InterruptedException, FileAdded, FileRemoved {
		fileRemovedDetected = false;

		final String folderPath = Paths.get(System.getProperty("java.io.tmpdir"), FOLDER).toString();
		final File folder = new File(folderPath);
		folder.mkdirs();
		folder.deleteOnExit();

		// File already on the folder.
		final String fileName = "fileRemovedFromFolder.txt";
		final File file = new File(Files.write(Paths.get(folderPath + File.separator + fileName), "NewFile".getBytes(),
				StandardOpenOption.CREATE_NEW).toString());
		file.deleteOnExit();

		// Register the folder.
		final FileWatcher folderWatcher = new FileWatcher(folderPath);
		folderWatcher.addFileRemovedListener(new FileRemovedListener() {

			@Override
			public void fileDeleted(Path pathToFile) {
				fileRemovedDetected = true;

			}
		});

		// Remove file.
		file.delete();

		// Check change alert!
		for (int i = 0; i < 10; i++) {
			if (fileRemovedDetected) {
				throw new FileRemoved();
			}
			Thread.sleep(100);
		}
	}
}
